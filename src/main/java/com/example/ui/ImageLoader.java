package com.example.ui;

import javafx.scene.image.Image;
import java.util.HashMap;
import java.util.Objects;

public class ImageLoader
{
	private static HashMap< String, Image > cache;

	public static Image getImage( String path )
	{
		if (cache == null) cache = new HashMap<>();

		return cache.computeIfAbsent(path, p -> {
			try
			{
				return new Image(Objects.requireNonNull(ImageLoader.class.getResourceAsStream(p), 
					"Could not find image resource: " + p));
			}
			catch ( Exception e )
			{
				System.err.println( "Error loading image: " + e.getMessage());
				return null;
			}
		});
	}
}
