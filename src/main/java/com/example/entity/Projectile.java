package com.example.entity;

import com.example.game.GameManager;
import com.example.ui.ImageLoader;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Projectile extends Entity
{

	private final Image image;
	private double x1, y1, x2, y2;
	private double x, y;
	private double dx, dy;
	private double dirx, diry, magnitude;
	private double speed = 10;
	private double angle;
	private boolean active;
	private AnimatedEntity target;
	private Tower parent;
	private double scaleFactor;

	public Projectile( Tower parent, double x1, double y1, AnimatedEntity target )
	{
		super(0, 0, 0);
		switch ( parent )
		{
			case ArcherTower _:
				image = ImageLoader.getImage("/com/example/assets/effects/arrow.png");
				scaleFactor = 0.2;
				break;
			case MageTower _:
				image = ImageLoader.getImage("/com/example/assets/effects/spell.png");
				scaleFactor = 0.3;
				break;
			case ArtilleryTower _:
				image = ImageLoader.getImage("/com/example/assets/effects/bomb.png");
				scaleFactor = 0.2;
				break;
			default:
				image = null;
				throw new IllegalArgumentException();
		}

		this.active = true;
		this.x1 = x1;
		this.y1 = y1;
		this.x = x1;
		this.y = y1;
		this.target = target;
		this.parent = parent;
		this.x2 = target.getFuturePosition().x();
		this.y2 = target.getFuturePosition().y();

		dx = x2 - x1;
		dy = y2 - y1;
		magnitude = Math.hypot(dx, dy);
		dirx = dx / magnitude;
		diry = dy / magnitude;

		angle = Math.toDegrees(Math.atan2(dy, dx));
	}

	@Override
	public void update(double dt) {
		if ( active )
		{
			if ( Math.abs(x - x2) + Math.abs(y - y2) > 1 )
			{
				x += dirx * speed * dt;
				y += diry * speed * dt;
			} else
			{
				this.active = false;
				int dmg = target.modifyDamage(parent, parent.baseDamage);
				target.applyDamage(dmg);

				if (parent instanceof MageTower m && m.upgradeLevel >= 2 && target instanceof AnimatedEntity a) {
					a.applySlow(0.8, 4.0);
				}
				GameManager.getInstance().removeEntity(this);

				if (parent instanceof MageTower && Math.random() < 0.03 && target.getHP() > 0) {
					target.resetToStart();
				}

				GameManager.getInstance().removeEntity(this);
				GameManager.getInstance().spawnEffect(parent, x, y);
			}
		}
	}

	@Override
	public void render( GraphicsContext gc )
	{
		gc.save();
		gc.translate(x, y);
		gc.rotate(angle);
		gc.scale(scaleFactor, scaleFactor);
		gc.drawImage(image, -image.getWidth() / 2, -image.getHeight() / 2);
		gc.restore();
	}
}
