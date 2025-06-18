package com.example.storage_manager;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * Interface representing a serializable object that can be converted to and from JSON format.
 */
public interface Serializable {

    /**
     * Converts the object to its JSON representation.
     *
     * @return A string containing the JSON representation of the object.
     * @throws JsonProcessingException If an error occurs during JSON processing.
     */
    public String toJson() throws JsonProcessingException;

    /**
     * Populates the object from its JSON representation.
     *
     * @param json A string containing the JSON representation of the object.
     * @throws JsonProcessingException If an error occurs during JSON processing.
     */
    public void fromJson(String json) throws JsonProcessingException;
}
