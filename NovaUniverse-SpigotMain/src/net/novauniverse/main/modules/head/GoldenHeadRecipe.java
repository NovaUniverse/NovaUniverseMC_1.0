package net.novauniverse.main.modules.head;

import org.bukkit.Material;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;

import net.zeeraa.novacore.spigot.abstraction.VersionIndependantUtils;
import net.zeeraa.novacore.spigot.customcrafting.CustomRecipe;

public class GoldenHeadRecipe extends CustomRecipe {

	@Override
	public Recipe getRecipe() {
		ShapedRecipe recipe = new ShapedRecipe(GoldenHead.getInstance().getItem());

		recipe.shape("AAA", "ABA", "AAA");

		recipe.setIngredient('A', Material.GOLD_INGOT);
		VersionIndependantUtils.get().setShapedRecipeIngredientAsPlayerSkull(recipe, 'B');

		return recipe;
	}

	@Override
	public String getName() {
		return "Golden head";
	}
}