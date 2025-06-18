package com.example.game;

/**
 * Represents an event in the game.
 */
public class GameEvent {

	/**
	 * Enum representing the types of game events.
	 */
	public enum GameEventType {
		/**
		 * Indicates the start of the game.
		 */
		START,

		/**
		 * Indicates victory in the game.
		 */
		GAME_VICTORY,

		/**
		 * Indicates the game is over.
		 */
		GAME_OVER,

		/**
		 * Represents a message event.
		 */
		MESSAGE,

		/**
		 * Represents an update to the player's gold.
		 */
		UPDATE_GOLD,

		/**
		 * Indicates a repaint event.
		 */
		REPAINT
	}

	/**
	 * The type of the game event.
	 */
	public GameEventType type;

	/**
	 * Constructs a GameEvent with the specified type.
	 *
	 * @param type The type of the game event.
	 */
	public GameEvent(GameEventType type) {
		this.type = type;
	}
}
