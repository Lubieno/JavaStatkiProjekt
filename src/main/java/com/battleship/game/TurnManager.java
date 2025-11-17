package com.battleship.game;

import com.battleship.player.Player;

/**
 * @Author Student
 *
 * Prosty menadżer tur. Pozwala śledzić aktywnego gracza i przełączać się.
 */
public class TurnManager {
    private Player current;
    private Player other;

    public TurnManager(Player p1, Player p2) {
        this.current = p1;
        this.other = p2;
    }

    public Player current() { return current; }
    public Player other() { return other; }

    public void switchTurn() {
        Player tmp = current;
        current = other;
        other = tmp;
    }

    public boolean isPlayerTurn(Player p) {
        return current == p;
    }
}
