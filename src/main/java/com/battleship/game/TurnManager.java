package com.battleship.game;

import com.battleship.player.Player;

/**
 * Klasa pomocnicza (Helper) odpowiedzialna za zarządzanie cyklem tur w grze.
 * Przechowuje referencje do obu graczy i zarządza przełączaniem aktywnego uczestnika.
 *
 * Chociaż klasa `Game` posiada własną flagę `playerTurn` (boolean), `TurnManager`
 * zapewnia bardziej obiektowe podejście, operując bezpośrednio na instancjach `Player`.
 * Może być wykorzystywana w przyszłości do implementacji bardziej złożonych mechanik
 * (np. gry dla więcej niż 2 graczy lub dodatkowe tury za specjalne akcje).
 */
public class TurnManager {
    private Player current;
    private Player other;

    /**
     * Inicjalizuje menedżera tur.
     *
     * @param p1 Gracz rozpoczynający rozgrywkę.
     * @param p2 Gracz oczekujący.
     */
    public TurnManager(Player p1, Player p2) {
        this.current = p1;
        this.other = p2;
    }

    /**
     * Zwraca gracza, którego tura aktualnie trwa.
     * @return Instancja aktywnego gracza.
     */
    public Player current() { return current; }

    /**
     * Zwraca gracza oczekującego na swoją kolej.
     * @return Instancja nieaktywnego gracza.
     */
    public Player other() { return other; }

    /**
     * Zamienia rolami gracza aktywnego i oczekującego.
     * Wykorzystuje zmienną tymczasową do bezpiecznej permutacji referencji.
     */
    public void switchTurn() {
        Player tmp = current;
        current = other;
        other = tmp;
    }

    /**
     * Weryfikuje, czy podany gracz jest aktualnie uprawniony do wykonania ruchu.
     * Sprawdzenie odbywa się poprzez porównanie referencji (tożsamość obiektów).
     *
     * @param p Gracz do sprawdzenia.
     * @return true, jeśli to tura gracza p.
     */
    public boolean isPlayerTurn(Player p) {
        return current == p;
    }
}