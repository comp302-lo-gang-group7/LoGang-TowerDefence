package com.example.game;

import com.example.entity.EntityGroup;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Class Wave
 */
public class Wave {


    public final EntityGroup group;

    @JsonCreator
    /**
     * TODO
     */
    public Wave(@JsonProperty("group") EntityGroup group) {
        this.group = group != null ? group : new EntityGroup(0, 0, 0);
    }
}