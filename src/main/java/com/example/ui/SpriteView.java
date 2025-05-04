package com.example.ui;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.image.ImageView;

public class SpriteView
{
	private final ObjectProperty<SpriteProvider> spriteProvider = new SimpleObjectProperty<>();
	private final ImageView imageView = new ImageView();

	// Processes Sprite changing the Image.
	private final ChangeListener<String> imagePathListener = ( _, s, t1 ) ->
	{
		if ( t1 != null )
			imageView.setImage(ImageLoader.getImage(t1));
		else
			imageView.setImage(null);
	};

	public SpriteView()
	{
	}

	public void setSpriteProvider( SpriteProvider provider )
	{
		if ( provider == null )
		{
			imageView.setImage(null);
			return;
		}

		imageView.setImage(ImageLoader.getImage(provider.getSprite().getImagePath()));
		provider.getSprite().imagePathProperty().addListener(imagePathListener);

		imageView.setX(provider.getSprite().getX());
		imageView.setY(provider.getSprite().getY());
		imageView.xProperty().bind(provider.getSprite().xProperty());
		imageView.yProperty().bind(provider.getSprite().yProperty());
	}

	public void replaceSpriteProvider ( SpriteProvider newProvider, SpriteProvider oldProvider )
	{
		if ( oldProvider != null )
		{
			oldProvider.getSprite().imagePathProperty().removeListener(imagePathListener);
			imageView.xProperty().unbind();
			imageView.yProperty().unbind();
		}
		setSpriteProvider(newProvider);
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
