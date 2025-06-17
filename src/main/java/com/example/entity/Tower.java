package com.example.entity;

import com.example.controllers.GameScreenController;
import com.example.game.GameManager;
import javafx.scene.canvas.GraphicsContext;

public abstract class Tower extends Entity
{
	public int baseDamage;
	public int goldCost;
	public int upgradeLevel;

	protected double timerTime = 0;
	protected double attackCooldown = 0.5;
	protected double range = 2;

	public Tower(int x, int y, int baseHp, int baseDamage, int goldCost, int upgradeLevel)
	{
		super(x, y, baseHp);
		this.baseDamage = baseDamage;
		this.goldCost = goldCost;
		this.upgradeLevel = upgradeLevel;
	}

	public double getRange()
	{
		return range;
	}

	public void setRange(int range) {
		this.range = range;
	}

	public void setAttackCooldown(double cooldown) {
		this.attackCooldown = cooldown;
	}

	@Override
	public void update( double dt )
	{
		if ( timerTime < attackCooldown ) {
			timerTime += dt;
		} else {
			AnimatedEntity nearestEnemy = GameManager.getInstance().nearestEnemy(this);
			if (nearestEnemy != null) {
				double dx = getX() * GameScreenController.TILE_SIZE - nearestEnemy.getX();
				double dy = getY() * GameScreenController.TILE_SIZE - nearestEnemy.getY();
				double distance = Math.sqrt(dx * dx + dy * dy);

				if (distance <= range * GameScreenController.TILE_SIZE) {
					timerTime = 0;
					GameManager.getInstance().attackEntity(this, nearestEnemy);
				}
			}
		}
	}

	@Override
	public void render( GraphicsContext gc ) {}
}
