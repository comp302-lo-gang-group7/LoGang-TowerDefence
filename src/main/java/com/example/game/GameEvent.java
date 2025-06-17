package com.example.game;

/**
 * Class GameEvent
 */
public class GameEvent
{
	/**
	 * Class GameEventType
	 */
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
