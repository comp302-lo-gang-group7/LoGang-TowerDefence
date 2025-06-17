package com.example.ui;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;



/**
 * Class SpriteView
 */
public class SpriteView
{
	/**
	 * TODO
	 */
	private final ImageView imageView = new ImageView();


	private final ChangeListener<Image> imageChangeListener =
			( _, _, newImage ) -> imageView.setImage(newImage);

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

		imageView.setImage(provider.getSprite().getImage());
		provider.getSprite().imageProperty().addListener(imageChangeListener);

		imageView.setX(provider.getSprite().getX());
		imageView.setY(provider.getSprite().getY());
		imageView.xProperty().bind(provider.getSprite().xProperty());
		imageView.yProperty().bind(provider.getSprite().yProperty());
	}

	public void replaceSpriteProvider ( SpriteProvider newProvider, SpriteProvider oldProvider )
	{
		if ( oldProvider != null )
		{
			oldProvider.getSprite().imageProperty().removeListener(imageChangeListener);
			imageView.xProperty().unbind();
			imageView.yProperty().unbind();
		}
		setSpriteProvider(newProvider);
	}

	public ImageView getImageView()
	{
		return imageView;
	}
}
