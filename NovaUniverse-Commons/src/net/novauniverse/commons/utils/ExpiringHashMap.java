package net.novauniverse.commons.utils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ExpiringHashMap<K, V> implements Map<K, V> {
	private Map<K, V> dataMap = new HashMap<K, V>();
	private Map<K, LocalDateTime> ttl = new HashMap<K, LocalDateTime>();
	private List<ExpiryCondition<V>> expiryConditions = new ArrayList<ExpiryCondition<V>>();

	private int secondsToLive = 0;

	public ExpiringHashMap(int secondsToLive) {
		this.secondsToLive = secondsToLive;
	}

	public ExpiringHashMap() {
		this(120);
	}
	
	public void setSecondsToLive(int secondsToLive) {
		this.secondsToLive = secondsToLive;
	}

	public List<ExpiryCondition<V>> getExpiryConditions() {
		return expiryConditions;
	}

	public void cleanup() {
		List<K> toBeRemoved = new ArrayList<K>();

		for (K key : this.keySet()) {
			if (ttl.containsKey(key)) {
				if (ttl.get(key).isAfter(LocalDateTime.now())) {
					boolean canExpire = true;

					for (ExpiryCondition<V> expiryCondition : expiryConditions) {
						if (!expiryCondition.canExpire(dataMap.get(key))) {
							canExpire = false;
							break;
						}
					}

					if (canExpire) {
						toBeRemoved.add(key);
					}
				}
			}
		}

		for (K key : toBeRemoved) {
			this.remove(key);
		}
	}

	@Override
	public int size() {
		return dataMap.size();
	}

	@Override
	public boolean isEmpty() {
		return dataMap.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return dataMap.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return dataMap.containsValue(value);
	}

	@Override
	public V get(Object key) {
		return this.get(key, true);
	}

	@SuppressWarnings("unchecked")
	public V get(Object key, boolean updateTTL) {
		if (updateTTL) {
			ttl.put((K) key, LocalDateTime.now().plusSeconds(secondsToLive));
		}
		return dataMap.get(key);
	}

	@Override
	public V put(K key, V value) {
		ttl.put(key, LocalDateTime.now().plusSeconds(secondsToLive));
		return dataMap.put(key, value);
	}

	public V put(K key, V value, int secondsToLive) {
		ttl.put(key, LocalDateTime.now().plusSeconds(secondsToLive));
		return dataMap.put(key, value);
	}

	@Override
	public V remove(Object key) {
		ttl.remove(key);
		return dataMap.remove(key);
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		for (K key : m.keySet()) {
			ttl.put(key, LocalDateTime.now().plusSeconds(secondsToLive));
		}
		dataMap.putAll(m);
	}

	@Override
	public void clear() {
		ttl.clear();
		dataMap.clear();
	}

	@Override
	public Set<K> keySet() {
		return dataMap.keySet();
	}

	@Override
	public Collection<V> values() {
		return dataMap.values();
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		return dataMap.entrySet();
	}
}