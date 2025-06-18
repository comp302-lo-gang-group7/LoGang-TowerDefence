package com.example.entity;

import com.example.game.GameManager;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

/**
 * Represents an animated effect entity in the game.
 * The effect is displayed using a series of frames from a sprite sheet.
 */
public class Effect extends Entity {
	private final Image[] frames;
	private final double frameDuration;
	private final double scaleFactor;
	private double frameTimer = 0;
	private int currentFrame = 0;

	/**
	 * Constructs an Effect entity.
	 *
	 * @param spriteSheet The sprite sheet containing the animation frames.
	 * @param frameCount The number of frames in the animation.
	 * @param frameSize The size of each frame in the sprite sheet.
	 * @param frameDuration The duration of each frame in seconds.
	 * @param scaleFactor The scaling factor for rendering the effect.
	 * @param x The x-coordinate of the effect's position.
	 * @param y The y-coordinate of the effect's position.
	 */
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

	/**
	 * Updates the state of the effect.
	 * Advances the animation frames based on the elapsed time.
	 * Removes the effect from the game manager when the animation is complete.
	 *
	 * @param dt The time elapsed since the last update, in seconds.
	 */
	@Override
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

	/**
	 * Renders the current frame of the effect to the graphics context.
	 *
	 * @param gc The graphics context used for rendering.
	 */
	@Override
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