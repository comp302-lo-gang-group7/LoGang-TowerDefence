package com.example.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Class EntityGroup
 */
public class EntityGroup {
    public final int goblins;
    public final int warriors;
    public final double delayAfter;

    @JsonCreator
    public EntityGroup(@JsonProperty("goblins") int goblins,
                       @JsonProperty("warriors") int warriors,
                       @JsonProperty("delayAfter") double delayAfter) {
        this.goblins = goblins;
        this.warriors = warriors;
        this.delayAfter = delayAfter;
    }

}
