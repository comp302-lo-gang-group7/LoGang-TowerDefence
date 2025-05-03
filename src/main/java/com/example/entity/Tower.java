package com.example.entity;

import com.example.utils.Damageable;
import com.example.utils.HP;
import com.example.map.Tile;
import javafx.scene.image.Image;

public abstract class Tower extends Tile implements Damageable
{
	private final HP hp;
	private final int baseDamage;

	public Tower(int x, int y, int baseHp, int baseDamage, String spritePath)
	{
		super(x, y, spritePath);
		this.baseDamage = baseDamage;
		this.hp = new HP(baseHp);
	}

	public HP getHP()
	{
		return hp;
	}

	public int getBaseDamage()
	{
		return baseDamage;
	}
}
