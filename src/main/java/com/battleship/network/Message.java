package com.battleship.network;

import java.io.Serializable;
import java.util.Map;

public class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum MsgType { CONNECT, DISCONNECT, SHOT, RESULT, PLACE, READY, INFO, JOIN_ROOM }

    private final MsgType type;
    private final Map<String, Object> payload;

    public Message(MsgType type, Map<String, Object> payload) {
        this.type = type;
        this.payload = payload;
    }

    public MsgType type() { return type; }
    public Map<String, Object> payload() { return payload; }

    @Override
    public String toString() {
        return "Message{" + "type=" + type + ", payload=" + payload + '}';
    }
}