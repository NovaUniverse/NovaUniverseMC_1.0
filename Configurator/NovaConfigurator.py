from pathlib import Path

import os
import json
import ruamel.yaml

import sys

def reqursive_replacer(key, data, new_value):
	key_parts = key.split(".")

	if len(key_parts) == 1:
		data[key] = new_value
	else:
		key_to_use = key_parts[0]
		key_parts.pop(0)
		reqursive_replacer(".".join(key_parts), data[key_to_use], new_value)

home = str(Path.home())
print("[INFO] Home directory is: " + home)

config_file_path = ""

if os.path.isfile(home + '/novaconfig.json'):
	config_file_path = home + '/novaconfig.json'
elif os.path.isfile(os.curdir + '/novaconfig.json'):
	config_file_path = os.curdir + '/novaconfig.json'
else:
	print("[ERR] novaconfig.json not found")
	exit(0)


has_overrides = False
overrides = None

if os.path.isfile(os.curdir + '/novaoverrides.json'):
	print("[INFO] Reading overrides file")
	with open(os.curdir + '/novaoverrides.json') as json_file:
		overrides = json.load(json_file)
		has_overrides = True

print(overrides)

print("[INFO] Found config at: " + config_file_path)

plugin_path = os.curdir + "/plugins"

if not os.path.isdir(plugin_path):
	print("[ERR] Plugin folder at " + plugin_path + " does not exist")
	exit(0)

with open(config_file_path) as json_file:
	json_data = json.load(json_file)

	# Loop thru plugins
	for plugin in json_data["plugins"]:
		data_folder = plugin_path + "/" + plugin

		# Check if plugin folder exits
		if not os.path.isdir(data_folder):
			print("[ERR] Plugin data folder for " + plugin + " was not found")
			continue

		print("[INFO] Updating config for plugin: " + plugin)
		plugin_json = json_data["plugins"][plugin]

		# Loop thru config files
		for config_file in plugin_json:
			config_file_path = data_folder + "/" + config_file

			# Check if config file exits
			if not os.path.isfile(config_file_path):
				print("[ERR] Cant find " + config_file + " in plugin " + plugin)
				continue
			
			print("[INFO] Updating " + config_file + " in plugin " + plugin)
			yaml = ruamel.yaml.YAML()
			yaml.preserve_quotes = True

			# Read the config file
			with open(config_file_path) as fp:
				data = yaml.load(fp)
				
				values = json_data["plugins"][plugin][config_file]

				for value in values:
					new_value = values[value]

					if has_overrides:
						if plugin in overrides:
							if config_file in overrides[plugin]:
								if value in overrides[plugin][config_file]:
									print("[INFO] Using override for " + value + " in " + config_file + " for plugin " + plugin)
									new_value = overrides[plugin][config_file][value]
					
					reqursive_replacer(value, data, new_value)

				print("[INFO] Writing config to file")
				outf = Path(config_file_path)
				yaml.dump(data, outf)