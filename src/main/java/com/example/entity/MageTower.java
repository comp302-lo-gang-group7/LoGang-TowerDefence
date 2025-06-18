package com.example.entity;

/**
 * Represents a Mage Tower in the game, capable of dealing magical damage to enemies.
 */
public class MageTower extends Tower {
    /**
     * Constructs a Mage Tower entity.
     *
     * @param x The x-coordinate of the tower.
     * @param y The y-coordinate of the tower.
     * @param baseHp The base health points of the tower.
     * @param baseDamage The base damage dealt by the tower.
     * @param goldCost The gold cost to build the tower.
     * @param upgradeLevel The upgrade level of the tower.
     */
    public MageTower(int x, int y, int baseHp, int baseDamage, int goldCost, int upgradeLevel) {
        super(x, y, baseHp, baseDamage, goldCost, upgradeLevel);
    }
}
