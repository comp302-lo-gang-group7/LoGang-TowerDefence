package com.example.game;

import com.example.entity.EntityGroup;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Wave {
    // The main idea is that waves can become more dynamic and consist of multiple different groups later.
    // public final List<EntityGroup> groups;
    public final EntityGroup group;

    @JsonCreator
    public Wave(@JsonProperty("group") EntityGroup group) {
        this.group = group != null ? group : new EntityGroup(0, 0, 0);
    }
}