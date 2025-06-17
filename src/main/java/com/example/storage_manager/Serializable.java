package com.example.storage_manager;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * Class Serializable
 */
public interface Serializable {
    /**
     * TODO
     */
    public String toJson() throws JsonProcessingException;
    /**
     * TODO
     */
    public void fromJson(String json) throws JsonProcessingException;
}
