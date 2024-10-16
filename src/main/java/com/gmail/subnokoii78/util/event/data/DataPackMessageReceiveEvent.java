package com.gmail.subnokoii78.util.event.data;

import com.gmail.subnokoii78.util.datacontainer.EntityDataContainerManager;
import org.bukkit.entity.Entity;

import java.util.Arrays;

@Deprecated
public class DataPackMessageReceiveEvent {
    private final Entity messenger;

    private final Entity[] targets;

    private final String[] message;

    public DataPackMessageReceiveEvent(Entity messenger, Entity[] targets, String[] message) {
        if (message.length == 0) {
            throw new IllegalArgumentException("messageの長さは1以上である必要があります");
        }

        this.messenger = messenger;
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
        new EntityDataContainerManager(messenger).set("out", value);
    }
}
