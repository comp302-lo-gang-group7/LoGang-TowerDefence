package com.example.entity;

import com.example.game.GameManager;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

/**
 * Class Effect
 */
public class Effect extends Entity {
	private final Image[] frames;
	private final double frameDuration;
	private final double scaleFactor;
	private double frameTimer = 0;
	private int currentFrame = 0;

	public Effect(Image spriteSheet,
	              int frameCount,
	              int frameSize,
	              double frameDuration,
				  double scaleFactor,
	              double x, double y)
	{
		super(x, y, 0);
		this.frameDuration = frameDuration;
		this.scaleFactor = scaleFactor;
		this.frames = new Image[frameCount];


		for (int i = 0; i < frameCount; i++) {
			frames[i] = new WritableImage(
					spriteSheet.getPixelReader(),
					i * frameSize, 0,
					frameSize, frameSize
			);
		}
	}

	@Override
	/**
	 * TODO
	 */
	public void update(double dt) {
		if ( currentFrame >= frames.length ) {
			GameManager.getInstance().removeEntity(this);
		}
		else
		{
			frameTimer += dt;
			if ( frameTimer >= frameDuration )
			{
				frameTimer -= frameDuration;
				currentFrame++;
			}
		}
	}

	@Override
	/**
	 * TODO
	 */
	public void render(GraphicsContext gc) {
		if ( currentFrame < frames.length )
		{
			double spriteWidth = frames[currentFrame].getWidth();
			double spriteHeight = frames[currentFrame].getHeight();

			gc.save();
			gc.translate(x, y);
			gc.scale(scaleFactor, scaleFactor);
			gc.drawImage(frames[currentFrame], -spriteWidth / 2, -spriteHeight / 2);
			gc.restore();
		}
	}
}