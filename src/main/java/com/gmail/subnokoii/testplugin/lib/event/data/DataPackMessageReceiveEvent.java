package com.gmail.subnokoii.testplugin.lib.event.data;

import com.gmail.subnokoii.testplugin.lib.datacontainer.EntityDataContainerManager;
import org.bukkit.entity.Entity;

import java.util.Arrays;

public class DataPackMessageReceiveEvent {
    private final Entity core;

    private final Entity[] targets;

    private final String[] message;

    public DataPackMessageReceiveEvent(Entity core, Entity[] targets, String[] message) {
        if (message.length == 0) {
            throw new IllegalArgumentException("messageの長さは1以上である必要があります");
        }

        this.core = core;
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

    public void returnValue(Object value) {
        new EntityDataContainerManager(core).set("out", value);
    }
}
