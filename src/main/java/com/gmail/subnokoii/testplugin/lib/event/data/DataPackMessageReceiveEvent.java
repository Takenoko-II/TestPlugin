package com.gmail.subnokoii.testplugin.lib.event.data;

import org.bukkit.entity.Entity;

import java.util.Arrays;

public class DataPackMessageReceiveEvent {
    private final Entity[] targets;

    private final String[] message;

    public DataPackMessageReceiveEvent(Entity[] targets, String[] message) {
        if (message.length == 0) {
            throw new IllegalArgumentException("messageの長さは1以上である必要があります");
        }

        this.targets = targets;
        this.message = message;
    }

    public Entity[] getTargets() {
        return targets;
    }

    public String getId() {
        return message[0];
    }

    public String[] getParameters() {
        return Arrays.copyOfRange(message, 1, message.length);
    }
}
