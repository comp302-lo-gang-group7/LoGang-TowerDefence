package com.example.ui;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Sprite
{
	private final StringProperty imagePath = new SimpleStringProperty();

	public Sprite()
	{
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
}
