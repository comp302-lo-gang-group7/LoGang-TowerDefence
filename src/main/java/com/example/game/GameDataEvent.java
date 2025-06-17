package com.example.game;

/**
 * Class GameDataEvent
 */
public class GameDataEvent<T> extends GameEvent
{
	public T data;

	public GameDataEvent( GameEventType type, T data )
	{
		super(type);
		this.data = data;
	}
}
