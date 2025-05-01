package com.example.ui;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.ImageView;

public class SpriteView
{
	private final StringProperty imagePath = new SimpleStringProperty();
	private final ObjectProperty<SpriteProvider> spriteProvider = new SimpleObjectProperty<>();
	private final ImageView imageView = new ImageView();

	public SpriteView()
	{
		imagePath.addListener((obs, oldPath, newPath) -> {
			if ( newPath != null )
				imageView.setImage(ImageLoader.getImage(newPath));
			else
				imageView.setImage(null);
		});
		spriteProviderProperty().addListener(( spriteObs, oldSprite, newSprite) -> {
			if ( oldSprite != null )
			{
				imagePathProperty().unbind();
			}
			if ( newSprite != null )
			{
				imagePathProperty().bindBidirectional(newSprite.getSprite().imagePathProperty());
			}
			else
			{
				imageView.setImage(null);
			}
		});
	}

	public String getImagePath()
	{
		return imagePath.get();
	}

	public StringProperty imagePathProperty()
	{
		return imagePath;
	}

	public ImageView getImageView()
	{
		return imageView;
	}

	public ObjectProperty<SpriteProvider> spriteProviderProperty()
	{
		return spriteProvider;
	}
}
