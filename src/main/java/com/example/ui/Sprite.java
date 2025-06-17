package com.example.ui;

import javafx.beans.property.*;
import javafx.scene.image.Image;

/**
 * Class Sprite
 */
public class Sprite
{
	/**
	 * TODO
	 */
	private final DoubleProperty x = new SimpleDoubleProperty();
	/**
	 * TODO
	 */
	private final DoubleProperty y = new SimpleDoubleProperty();
	/**
	 * TODO
	 */
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
