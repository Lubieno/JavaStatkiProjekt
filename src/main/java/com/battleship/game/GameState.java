package com.battleship.game;

/**
 * Enum definiujący maszynę stanów gry.
 * Służy do sterowania przepływem sterowania w GameControllerze oraz blokowania interakcji UI
 * w nieodpowiednich momentach (np. strzelanie przed rozpoczęciem meczu).
 */
public enum GameState {
    WAITING, IN_PROGRESS, FINISHED
}