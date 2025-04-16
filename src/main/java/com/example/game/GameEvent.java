package com.example.game;

public class GameEvent
{
	public enum GameEventType
	{
		START,
		GAME_VICTORY,
		GAME_OVER,
		MESSAGE,
		UPDATE_GOLD,
		REPAINT
	}

	public GameEventType type;

	public GameEvent(GameEventType type)
	{
		this.type = type;
	}
}
