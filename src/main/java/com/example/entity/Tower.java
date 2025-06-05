package com.example.entity;

import com.example.game.GameManager;
import javafx.scene.canvas.GraphicsContext;

public abstract class Tower extends Entity
{
	public final int baseDamage;
	public int goldCost;
	public int upgradeLevel;

	private double timerTime = 0;
	private double attackCooldown = 0.5;

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
			GameManager.getInstance().attackEntity(this);
		}
	}

	@Override
	public void render( GraphicsContext gc ) {}
}
