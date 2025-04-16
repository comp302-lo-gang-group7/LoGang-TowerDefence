package com.example.game;

public class GameDataEvent<T> extends GameEvent
{
	public T data;

	public GameDataEvent( GameEventType type, T data )
	{
		super(type);
		this.data = data;
	}
}
