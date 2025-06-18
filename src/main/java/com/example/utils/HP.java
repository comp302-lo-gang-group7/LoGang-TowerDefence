package com.example.utils;

/**
 * Represents a health points (HP) system for an entity.
 * This class provides methods to manage and modify the HP value.
 */
public class HP
{
	/**
	 * The current health points of the entity.
	 */
	private int hp;

	/**
	 * Constructs an HP object with the specified base health points.
	 *
	 * @param baseHp The initial health points value.
	 */
	public HP(int baseHp)
	{
		this.hp = baseHp;
	}

	/**
	 * Modifies the current health points by adding the specified amount.
	 * Positive values increase the HP, while negative values decrease it.
	 *
	 * @param amount The amount to change the health points by.
	 */
	public void changeHp(int amount)
	{
		hp += amount;
	}

	/**
	 * Sets the health points to the specified value.
	 *
	 * @param hp The new health points value.
	 */
	public void setHp(int hp)
	{
		this.hp = hp;
	}

	/**
	 * Retrieves the current health points value.
	 *
	 * @return The current health points.
	 */
	public int getHp()
	{
		return hp;
	}
}
