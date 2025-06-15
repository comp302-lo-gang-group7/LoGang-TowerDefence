package com.example.map;

import com.example.entity.ArcherTower;
import com.example.entity.ArtilleryTower;
import com.example.entity.MageTower;
import com.example.entity.Tower;
import com.example.game.GameManager;
import com.example.utils.HP;
import com.example.utils.Damageable;

public class TileModel {
	private final int x, y;
	private TileEnum type;

	private Tower tower;

	public TileModel(int x, int y) {
		this.x = x;
		this.y = y;
		this.type = null;
	}

	// --- Location ---
	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	// --- Tower state ---
	public boolean hasTower() {
		return tower != null;
	}

	public void setTower(TileEnum towerType, int hpValue, int damage, int cost) {
		this.type = towerType;
		switch ( towerType ) {
			case ARCHERY_TOWER:
			{
				tower = new ArcherTower(x, y, hpValue, damage, cost, 1);
				break;
			}
			case MAGE_TOWER:
			{
				tower = new MageTower(x, y, hpValue, damage, cost, 1);
				break;
			}
			case ARTILLERY_TOWER:
			{
				tower = new ArtilleryTower(x, y, hpValue, damage, cost, 1);
				break;
			}
			default:
				throw new IllegalArgumentException("Invalid tower type");
		}
		GameManager.getInstance().placeTower(tower);
	}

	public void removeTower() {
		GameManager.getInstance().removeTower(tower);
		this.type = TileEnum.EMPTY_TOWER_TILE;
		this.tower = null;
	}

	public Tower getTower()
	{
		return tower;
	}

	public TileEnum getType() {
		return type;
	}

	public void setType( TileEnum type ) {
		this.type = type;
	}
}
