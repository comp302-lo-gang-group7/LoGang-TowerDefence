package com.example.game;

/**
 * Represents a listener for game events. Classes implementing this interface
 * should define how to handle specific game events.
 */
public interface GameEventListener
{
	/**
	 * Handles a game event.
	 *
	 * @param event the game event to be handled
	 */
	void handle(GameEvent event);
}
