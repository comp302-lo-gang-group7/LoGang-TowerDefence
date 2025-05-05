package com.example.storage_manager;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface Serializable {
    public String toJson() throws JsonProcessingException;
    public void fromJson(String json) throws JsonProcessingException;
}
