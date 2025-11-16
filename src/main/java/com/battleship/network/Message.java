package com.battleship.network;

import java.io.Serializable;
import java.util.Map;

public class Message implements Serializable {
    public final String type;
    public final Map<String, Object> data;

    public Message(String type, Map<String, Object> data) {
        this.type = type;
        this.data = data;
    }
}
