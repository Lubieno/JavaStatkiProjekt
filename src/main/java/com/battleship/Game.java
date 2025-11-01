package com.battleship;

import com.battleship.model.Player;

public class Game {
    private final Player player1;
    private final Player player2;

    public Game() {
        player1 = new Player("Gracz 1", 10);
        player2 = new Player("Gracz 2", 10);
    }

    public void start() {
        System.out.println("Gra rozpoczęta. Załadowano struktury danych pomyślnie.");
    }
}
