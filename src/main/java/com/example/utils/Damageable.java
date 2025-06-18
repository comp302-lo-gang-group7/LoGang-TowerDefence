package com.example.utils;

/**
 * Represents an entity that can take damage and has health points (HP).
 */
public interface Damageable
{
	/**
	 * Retrieves the current health points (HP) of the entity.
	 *
	 * @return the current HP of the entity.
	 */
	HP getHP();

	/**
	 * Applies damage to the entity, reducing its health points (HP) by the specified amount.
	 *
	 * @param amount the amount of damage to apply to the entity.
	 */
	void applyDamage(int amount);
}
