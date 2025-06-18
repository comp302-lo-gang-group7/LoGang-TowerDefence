package com.example.map;

import com.example.entity.ArcherTower;
import com.example.entity.ArtilleryTower;
import com.example.entity.MageTower;
import com.example.entity.Tower;
import com.example.game.GameManager;
import com.example.utils.HP;
import com.example.utils.Damageable;

/**
 * Represents a tile on the game map. A tile can hold a tower and has specific coordinates and type.
 */
public class TileModel {
	private final int x, y;
	private TileEnum type;

	private Tower tower;

	/**
	 * Constructs a TileModel with specified coordinates.
	 *
	 * @param x The x-coordinate of the tile.
	 * @param y The y-coordinate of the tile.
	 */
	public TileModel(int x, int y) {
		this.x = x;
		this.y = y;
		this.type = null;
	}

	/**
	 * Gets the x-coordinate of the tile.
	 *
	 * @return The x-coordinate of the tile.
	 */
	public int getX() {
		return x;
	}

	/**
	 * Gets the y-coordinate of the tile.
	 *
	 * @return The y-coordinate of the tile.
	 */
	public int getY() {
		return y;
	}

	/**
	 * Checks if the tile has a tower placed on it.
	 *
	 * @return True if the tile has a tower, false otherwise.
	 */
	public boolean hasTower() {
		return tower != null;
	}

	/**
	 * Places a tower on the tile with specified attributes.
	 *
	 * @param towerType    The type of the tower to place.
	 * @param hpValue      The initial HP value of the tower.
	 * @param damage       The damage value of the tower.
	 * @param cost         The cost of the tower.
	 * @param range        The range of the tower.
	 * @param upgradeLevel The upgrade level of the tower.
	 */
	public void setTower(TileEnum towerType, int hpValue, int damage, int cost, int range, int upgradeLevel) {
		this.type = towerType;
		switch (towerType) {
			case ARCHERY_TOWER: {
				tower = new ArcherTower(x, y, hpValue, damage, cost, upgradeLevel);
				tower.setRange(range);
				tower.setAttackCooldown(upgradeLevel >= 2 ? 0.4 : 0.5);
				break;
			}
			case MAGE_TOWER: {
				tower = new MageTower(x, y, hpValue, damage, cost, upgradeLevel);
				tower.setRange(range);
				tower.setAttackCooldown(0.5);
				break;
			}
			case ARTILLERY_TOWER: {
				tower = new ArtilleryTower(x, y, hpValue, damage, cost, upgradeLevel);
				tower.setRange(range);
				tower.setAttackCooldown(0.5);
				break;
			}
			default:
				throw new IllegalArgumentException("Invalid tower type");
		}
		GameManager.getInstance().placeTower(tower);
	}

	/**
	 * Upgrades the tower on the tile with new attributes.
	 *
	 * @param hpValue The new HP value of the tower.
	 * @param damage  The new damage value of the tower.
	 * @param range   The new range of the tower.
	 */
	public void upgradeTower(int hpValue, int damage, int range) {
		if (tower == null) return;
		GameManager.getInstance().removeTower(tower);
		int level = tower.upgradeLevel + 1;
		setTower(this.type, hpValue, damage, tower.goldCost, range, level);
	}

	/**
	 * Removes the tower from the tile.
	 */
	public void removeTower() {
		GameManager.getInstance().removeTower(tower);
		this.type = TileEnum.EMPTY_TOWER_TILE;
		this.tower = null;
	}

	/**
	 * Gets the tower placed on the tile.
	 *
	 * @return The tower placed on the tile, or null if no tower is present.
	 */
	public Tower getTower() {
		return tower;
	}

	/**
	 * Gets the type of the tile.
	 *
	 * @return The type of the tile.
	 */
	public TileEnum getType() {
		return type;
	}

	/**
	 * Sets the type of the tile.
	 *
	 * @param type The type to set for the tile.
	 */
	public void setType(TileEnum type) {
		this.type = type;
	}
}
