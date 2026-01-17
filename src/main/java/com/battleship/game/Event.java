package com.battleship.game;

/**
 * Klasa reprezentująca zdarzenie zwrotne (Feedback) generowane przez silnik gry w odpowiedzi na akcję.
 * Przenosi informacje o skutku działania (np. HIT, MISS, SUNK) oraz czytelny komunikat dla użytkownika.
 * Jest używana zarówno do aktualizacji UI, jak i wymiany informacji o wynikach strzałów przez sieć.
 */
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