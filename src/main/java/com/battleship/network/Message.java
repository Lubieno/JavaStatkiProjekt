package com.battleship.network;

import java.io.Serializable;
import java.util.Map;

/**
 * Prosty kontener wiadomości sieciowej. Serializable by po implementacji TCP
 * łatwiej było marshallować (lub użyć JSON).
 */
public class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum MsgType { CONNECT, DISCONNECT, SHOT, RESULT, PLACE, READY, INFO }

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
