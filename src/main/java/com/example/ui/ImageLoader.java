package com.example.ui;

import javafx.scene.image.Image;
import java.util.HashMap;

/**
 * A utility class for loading and caching images.
 */
public class ImageLoader
{
	private static HashMap<String, Image> cache;

	/**
	 * Retrieves an image from the specified path. If the image has already been loaded,
	 * it is retrieved from the cache; otherwise, it is loaded and added to the cache.
	 *
	 * @param path The path to the image file.
	 * @return The loaded Image object, or null if an error occurs during loading.
	 */
	public static Image getImage(String path)
	{
		if (cache == null) cache = new HashMap<>();

		return cache.computeIfAbsent(path, p -> {
			try
			{
				return new Image(ImageLoader.class.getResourceAsStream(p));
			}
			catch (Exception e)
			{
				System.err.println("Error loading image: " + e.getMessage());
				return null;
			}
		});
	}
}
