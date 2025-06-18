package com.example.entity;

import javafx.scene.image.Image;

/**
 * Represents an Archer Tower entity in the game. This class extends the Tower class
 * and provides specific functionality for an Archer Tower.
 */
public class ArcherTower extends Tower
{
	/**
	 * Constructs an ArcherTower with the specified attributes.
	 *
	 * @param x             The x-coordinate of the tower's position.
	 * @param y             The y-coordinate of the tower's position.
	 * @param baseHp        The base health points of the tower.
	 * @param baseDamage    The base damage dealt by the tower.
	 * @param goldCost      The gold cost to build the tower.
	 * @param upgradeLevel  The upgrade level of the tower.
	 */
	public ArcherTower( int x, int y, int baseHp, int baseDamage, int goldCost, int upgradeLevel )
	{
		super(x, y, baseHp, baseDamage, goldCost, upgradeLevel);
	}
}
