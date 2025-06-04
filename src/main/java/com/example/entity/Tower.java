package com.example.entity;

import com.example.game.GameManager;
import com.example.utils.Damageable;
import com.example.utils.HP;
import com.example.map.TileModel;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public abstract class Tower extends Entity
{
	private final int baseDamage;
	private int goldCost;
	private int upgradeLevel;

	private double timerTime = 0;
	private double attackCooldown = 2.0;

	public Tower(int x, int y, int baseHp, int baseDamage, int goldCost, int upgradeLevel)
	{
		super(x, y, baseHp);
		this.baseDamage = baseDamage;
	}

	@Override
	public void update( double dt )
	{
		timerTime += dt;
		if ( timerTime >= attackCooldown )
		{
			timerTime = 0;
			GameManager.getInstance().spawnProjectile(this);
		}
	}

	@Override
	public void render( GraphicsContext gc ) {}
}
