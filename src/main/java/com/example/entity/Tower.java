package com.example.entity;

import com.example.utils.Damageable;
import com.example.utils.HP;
import com.example.map.TileModel;
import javafx.scene.image.Image;

public abstract class Tower extends TileModel implements Damageable
{
	private final HP hp;
	private final int baseDamage;

	public Tower(int x, int y, int baseHp, int baseDamage, Image image)
	{
		super(x, y);
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
