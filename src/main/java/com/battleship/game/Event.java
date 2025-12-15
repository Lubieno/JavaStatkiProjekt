package com.battleship.game;

public class Event {
    public enum Type { HIT, MISS, ALREADY, INVALID, WIN, INFO, SUNK }

    private final Type type;
    private final String message;

    public Event(Type type, String message) {
        this.type = type;
        this.message = message;
    }

    public Type type() { return type; }
    public String message() { return message; }

    @Override
    public String toString() {
        return "Event{" + "type=" + type + ", message='" + message + '\'' + '}';
    }
}