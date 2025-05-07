package com.example.map;

import com.example.entity.Tower;
import com.example.utils.HP;
import com.example.utils.Damageable;

public class TileModel implements Damageable {
	private final int tileX, tileY;
	private boolean hasTower;
	private TileEnum towerType;
	private HP hp;
	private int baseDamage;
	private int goldCost;
	private int upgradeLevel;

	public TileModel(int tileX, int tileY) {
		this.tileX = tileX;
		this.tileY = tileY;
		this.hasTower = false;
		this.towerType = null;
		this.hp = null;
		this.baseDamage = 0;
		this.goldCost = 0;
		this.upgradeLevel = 0;
	}

	// --- Location ---
	public int getTileX() {
		return tileX;
	}

	public int getTileY() {
		return tileY;
	}

	// --- Tower state ---
	public boolean hasTower() {
		return hasTower;
	}

	public void setTower(TileEnum towerType, int hpValue, int damage, int cost) {
		this.hasTower = true;
		this.towerType = towerType;
		this.hp = new HP(hpValue);
		this.baseDamage = damage;
		this.goldCost = cost;
		this.upgradeLevel = 1;
	}

	public void removeTower() {
		this.hasTower = false;
		this.towerType = null;
		this.hp = null;
		this.baseDamage = 0;
		this.goldCost = 0;
		this.upgradeLevel = 0;
	}

	public TileEnum getTowerType() {
		return towerType;
	}

	public int getBaseDamage() {
		return baseDamage;
	}

	public int getGoldCost() {
		return goldCost;
	}

// For future uses
//	public int getUpgradeLevel() {
//		return upgradeLevel;
//	}
//
//	public void upgrade(int newDamage, int newCost) {
//		this.baseDamage = newDamage;
//		this.goldCost += newCost;
//		this.upgradeLevel++;
//	}

	// --- Damageable interface ---
	@Override
	public HP getHP() {
		return hp;
	}

	@Override
	public void applyDamage(int amount) {
		if (hp != null) {
			hp.changeHp(amount);
			if (hp.getHp() == 0) removeTower();
		}
	}

	public void setTowerType(TileEnum towerType) {
		this.towerType = towerType;
	}
}
