package com.example.game;

import javafx.scene.image.Image;

public abstract class Tower extends Tile implements Damageable
{
	private final HP hp;
	private final int baseDamage;

	public Tower(int x, int y, Image sprite, int baseHp, int baseDamage)
	{
		super(x, y, sprite);
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
