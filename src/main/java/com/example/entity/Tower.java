package com.example.entity;

import com.example.controllers.GameScreenController;
import com.example.game.GameManager;
import javafx.scene.canvas.GraphicsContext;

public abstract class Tower extends Entity
{
	public final int baseDamage;
	public int goldCost;
	public int upgradeLevel;

	private double timerTime = 0;
	private double attackCooldown = 0.5;
	private double minRadius = 5;

	public Tower(int x, int y, int baseHp, int baseDamage, int goldCost, int upgradeLevel)
	{
		super(x, y, baseHp);
		this.baseDamage = baseDamage;
	}

	@Override
	public void update( double dt )
	{
		if ( timerTime < attackCooldown )
			timerTime += dt;
		else
		{
			AnimatedEntity nearestEnemy = GameManager.getInstance().nearestEnemy(this);
			if ( Math.abs(getX() * 64 - nearestEnemy.getX()) + Math.abs(getY() * 64 - nearestEnemy.getY())
					<= minRadius * GameScreenController.TILE_SIZE)
			{
				timerTime = 0;
				GameManager.getInstance().attackEntity(this, nearestEnemy);
			}
		}
	}

	@Override
	public void render( GraphicsContext gc ) {}
}
