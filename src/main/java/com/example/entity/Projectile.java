package com.example.entity;

import com.example.game.GameManager;
import com.example.ui.ImageLoader;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Projectile extends Entity {

	private final Image image;
	private double x1, y1, x2, y2;
	private double x, y;
	private double dx, dy;
	private double dirx, diry, magnitude;
	private double speed = 10;
	private double angle;
	private boolean active;

	public Projectile( Tower tower )
	{
		super(0, 0, 0);
		switch ( tower )
		{
			case ArcherTower _:
				image = ImageLoader.getImage("/arrow.png");
				break;
			case MageTower _:
				image = ImageLoader.getImage("/spell.png");
				break;
			case ArtilleryTower _:
				image = ImageLoader.getImage("/bomb.png");
				break;
			default:
				image = null;
				throw new IllegalArgumentException();
		}
	}

	public void initialize(double x1, double y1, double x2, double y2)
	{
		this.active = true;
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		this.x = x1;
		this.y = y1;

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
			if ( Math.abs(x - x2) + Math.abs(y - y2) > 0.1 ) {
			double magnitude = Math.hypot(x - x2, y - y2);
			dirx = (x2 - x) / magnitude;
			diry = (y2 - y) / magnitude;

			x += dirx * speed * dt;
			y += diry * speed * dt;
			} else
			{
				this.active = false;
				GameManager.getInstance().removeProjectile(this);
			}
		}
	}

	@Override
	public void render(GraphicsContext gc) {
		gc.save();
		gc.translate(x, y);
		gc.rotate(angle);
		gc.drawImage(image, -image.getWidth() / 2, -image.getHeight() / 2);
		gc.restore();
	}
}
