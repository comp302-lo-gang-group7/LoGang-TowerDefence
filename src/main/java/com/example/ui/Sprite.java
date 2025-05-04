package com.example.ui;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Sprite
{
	private final StringProperty imagePath = new SimpleStringProperty();
	private final DoubleProperty x = new SimpleDoubleProperty();
	private final DoubleProperty y = new SimpleDoubleProperty();

	public Sprite()
	{
		imagePath.set(null);
		x.set(0.0);
		y.set(0.0);
	}

	public Sprite( String imagePath, double x, double y )
	{
		this.imagePath.set(imagePath);
		this.x.set(x);
		this.y.set(y);
	}

	public String getImagePath()
	{
		return imagePath.get();
	}

	public StringProperty imagePathProperty()
	{
		return imagePath;
	}

	public void setImagePath(String imagePath)
	{
		this.imagePath.set(imagePath);
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
		imagePath.unbind();
		x.unbind();
		y.unbind();
	}
}
