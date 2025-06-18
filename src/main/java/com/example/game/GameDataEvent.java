package com.example.game;

/**
 * Represents a generic game event that carries additional data.
 *
 * @param <T> the type of the data associated with the event
 */
public class GameDataEvent<T> extends GameEvent {
    
    /**
     * The data associated with the event.
     */
    public T data;

    /**
     * Constructs a new GameDataEvent with the specified type and associated data.
     *
     * @param type the type of the game event
     * @param data the data to associate with the event
     */
    public GameDataEvent(GameEventType type, T data) {
        super(type);
        this.data = data;
    }
}
