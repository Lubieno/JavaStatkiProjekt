package com.battleship.player;

/**
 * Reprezentuje gracza zdalnego w trybie sieciowym.
 */
public class RemotePlayer extends Player {
    public RemotePlayer(String nazwa) {
        super(nazwa);
    }

    public void odbierzRuch() {
        // Przykładowe miejsce na odbiór danych z sieci
    }
}
