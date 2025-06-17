package com.example.map;

import com.example.entity.ArcherTower;
import com.example.entity.ArtilleryTower;
import com.example.entity.MageTower;
import com.example.entity.Tower;
import com.example.game.GameManager;
import com.example.utils.HP;
import com.example.utils.Damageable;

/**
 * Class TileModel
 */
public class TileModel {
	private final int x, y;
	private TileEnum type;

	private Tower tower;

	/**
	 * TODO
	 */
	public TileModel(int x, int y) {
		this.x = x;
		this.y = y;
		this.type = null;
	}


	/**
	 * TODO
	 */
	public int getX() {
		return x;
	}

	/**
	 * TODO
	 */
	public int getY() {
		return y;
	}


	/**
	 * TODO
	 */
	public boolean hasTower() {
		return tower != null;
	}

	/**
	 * TODO
	 */
	public void setTower(TileEnum towerType, int hpValue, int damage, int cost, int range, int upgradeLevel) {
		this.type = towerType;
		switch ( towerType ) {
			case ARCHERY_TOWER:
			{
				tower = new ArcherTower(x, y, hpValue, damage, cost, upgradeLevel);
				tower.setRange(range);
				tower.setAttackCooldown(upgradeLevel >= 2 ? 0.4 : 0.5);
				break;
			}
			case MAGE_TOWER:
			{
				tower = new MageTower(x, y, hpValue, damage, cost, upgradeLevel);
				tower.setRange(range);
				tower.setAttackCooldown(0.5);
				break;
			}
			case ARTILLERY_TOWER:
			{
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
	 * TODO
	 */
	public void upgradeTower(int hpValue, int damage, int range) {
		if (tower == null) return;
		GameManager.getInstance().removeTower(tower);
		int level = tower.upgradeLevel + 1;
		setTower(this.type, hpValue, damage, tower.goldCost, range, level);
	}

	/**
	 * TODO
	 */
	public void removeTower() {
		GameManager.getInstance().removeTower(tower);
		this.type = TileEnum.EMPTY_TOWER_TILE;
		this.tower = null;
	}

	public Tower getTower()
	{
		return tower;
	}

	/**
	 * TODO
	 */
	public TileEnum getType() {
		return type;
	}

	/**
	 * TODO
	 */
	public void setType( TileEnum type ) {
		this.type = type;
	}
}
