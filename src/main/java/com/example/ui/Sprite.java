package com.example.ui;

import javafx.beans.property.*;
import javafx.scene.image.Image;

public class Sprite
{
	private final DoubleProperty x = new SimpleDoubleProperty();
	private final DoubleProperty y = new SimpleDoubleProperty();
	private final ObjectProperty<Image> image = new SimpleObjectProperty<>();

	public Sprite()
	{
		image.set(null);
		x.set(0.0);
		y.set(0.0);
	}

	public Sprite( Image image, double x, double y )
	{
		this.image.set(image);
		this.x.set(x);
		this.y.set(y);
	}

	public double getX()
	{
		return x.get();
	}

	public DoubleProperty xProperty()
	{
		return x;
	}

	public double getY()
	{
		return y.get();
	}

	public DoubleProperty yProperty()
	{
		return y;
	}

	/// This method must be called by the SpriteProvider when it should no longer exist!!!
	public void unbind()
	{
		image.unbind();
		x.unbind();
		y.unbind();
	}

	public Image getImage()
	{
		return image.get();
	}

	public void setImage(Image image)
	{
		this.image.set(image);
	}

	public ObjectProperty<Image> imageProperty()
	{
		return image;
	}
}
