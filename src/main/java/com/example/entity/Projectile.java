package com.example.entity;

import com.example.game.GameManager;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Projectile extends Entity {
	private final Image image;
	private final double x1, y1, x2, y2;
	private double x, y;
	private final double dx, dy;
	private double dirx, diry;
	private double speed = 10;
	private final double angle;

	public Projectile( Image image, double x1, double y1, double x2, double y2 )
	{
		super(x1, y1, 0);
		//System.out.printf("%.2f %.2f %.2f %.2f\n", x1, y1, x2, y2);
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		this.x = x1;
		this.y = y1;
		this.image = image;
		dx = x2 - x1;
		dy = y2 - y1;

		System.out.println(Math.hypot(dirx, diry));
		angle = Math.toDegrees(Math.atan2(dy, dx));
		System.out.println("angle: " + angle);
	}

	@Override
	public void update(double dt) {
		if ( Math.abs(x - x2) + Math.abs(y - y2) > 0.1 ) {
			double magnitude = Math.hypot(dx, dy);
			dirx = dx / magnitude;
			diry = dy / magnitude;

			x += dirx * speed * dt;
			y += diry * speed * dt;
		}
		else
		{
			GameManager.getInstance().removeProjectile(this);
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
