package com.example.game;

import com.example.entity.EntityGroup;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Represents a wave in the game, consisting of an entity group.
 */
public class Wave {
    /**
     * The entity group associated with this wave.
     */
    public final EntityGroup group;

    /**
     * Constructs a Wave object with the specified entity group.
     * If the provided group is null, a default EntityGroup is created.
     *
     * @param group The entity group for this wave. Can be null.
     */
    @JsonCreator
    public Wave(@JsonProperty("group") EntityGroup group) {
        this.group = group != null ? group : new EntityGroup(0, 0, 0);
    }
}