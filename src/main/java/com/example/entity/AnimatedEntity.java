package com.example.entity;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

/**
 * An entity with N frames laid out horizontally in spriteSheet.
 */
public class AnimatedEntity extends Entity {
    private final Image[] frames;
    private final double frameDuration;  // seconds per frame
    private double frameTimer = 0;
    private int currentFrame = 0;

    public AnimatedEntity(Image spriteSheet,
                          int frameCount,
                          int frameSize,
                          double frameDuration,
                          double x, double y,
                          int hp)
    {
        super(x, y, hp);
        this.frameDuration = frameDuration;
        this.frames = new Image[frameCount];

        // slice the spriteSheet into frames
        for (int i = 0; i < frameCount; i++) {
            frames[i] = new WritableImage(
                    spriteSheet.getPixelReader(),
                    i * frameSize, 0,
                    frameSize, frameSize
            );
        }
    }

    @Override
    public void update(double dt) {
        // advance animation timer
        frameTimer += dt;
        if (frameTimer >= frameDuration) {
            frameTimer -= frameDuration;
            currentFrame = (currentFrame + 1) % frames.length;
        }
        // you can also put movement / AI logic here...
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.drawImage(frames[currentFrame], x, y);
    }

    @Override
    public void onClick() {
        // maybe show a hit flash, or manually step frames:
        currentFrame = (currentFrame + 1) % frames.length;
    }
}
