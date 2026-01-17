package com.battleship.player;

/**
 * Klasa reprezentująca gracza zdalnego.
 * Działa na zasadzie "cienia" (Shadow Object) - jego plansza nie odzwierciedla rzeczywistego
 * układu statków przeciwnika (który jest nieznany), lecz jest aktualizowana
 * na bieżąco o wyniki strzałów (HIT/MISS) otrzymywane z sieci.
 */
public class RemotePlayer extends Player {
    public RemotePlayer(String name) { super(name); }
}