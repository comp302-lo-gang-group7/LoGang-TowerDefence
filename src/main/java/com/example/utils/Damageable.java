package com.example.utils;

/**
 * Class Damageable
 */
public interface Damageable
{
	HP getHP();
	void applyDamage(int amount);
}
