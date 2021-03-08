package net.novauniverse.commons.utils;

public interface ExpiryCondition<T> {
	public boolean canExpire(T object);
}