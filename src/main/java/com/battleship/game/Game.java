package com.battleship.game;

import com.battleship.player.BotPlayer;
import com.battleship.player.HumanPlayer;
import com.battleship.player.Player;
import com.battleship.player.RemotePlayer;

/**
 * Główna klasa kontrolująca stan sesji gry (Session State).
 * Odpowiada za przechowywanie referencji do graczy, zarządzanie stanem rozgrywki
 * (oczekiwanie, gra, koniec) oraz kontrolę przepływu tur.
 *
 * Klasa stanowi serce modelu w architekturze MVC, agregując logikę biznesową
 * niezależną od interfejsu użytkownika.
 */
public class Game {
    private final Player player;
    private Player opponent;
    private GameState state = GameState.WAITING;
    private boolean playerTurn = false;

    /**
     * Konstruktor inicjalizujący grę w trybie sieciowym.
     * Wykorzystuje polimorfizm: przeciwnikiem jest instancja {@link RemotePlayer},
     * która działa jako proxy dla gracza zdalnego.
     *
     * @param playerName Nazwa gracza lokalnego.
     * @param opponent Obiekt gracza przeciwnika.
     * @param isHost Flaga określająca rolę w sieci (Host/Guest), decydująca o pierwszeństwie ruchu.
     */
    public Game(String playerName, Player opponent, boolean isHost) {
        this.player = new HumanPlayer(playerName);
        this.opponent = opponent;
        this.playerTurn = !isHost; // Zgodnie z zasadami: Gość wykonuje pierwszy ruch
        player.getBoard().randomPlaceFleet();
    }

    /**
     * Konstruktor domyślny inicjalizujący grę lokalną przeciwko sztucznej inteligencji.
     * Przeciwnikiem jest instancja {@link BotPlayer}.
     */
    public Game() {
        this("You", new BotPlayer("Bot"), false);
        this.playerTurn = true;
    }

    public Player getPlayer() { return player; }
    public Player getOpponent() { return opponent; }
    public GameState getState() { return state; }

    public void setOpponent(Player opponent) { this.opponent = opponent; }
    public void setPlayerTurn(boolean isTurn) { this.playerTurn = isTurn; }

    /**
     * Przełącza stan gry na IN_PROGRESS, co odblokowuje możliwość wykonywania akcji.
     */
    public void start() { state = GameState.IN_PROGRESS; }

    /**
     * Wymusza natychmiastowe przejście gry w stan FINISHED.
     * Metoda ta jest krytyczna w środowisku asynchronicznym (sieciowym),
     * gdy otrzymujemy komunikat o zwycięstwie (WIN) od przeciwnika,
     * mimo że lokalna symulacja planszy wroga może nie posiadać pełnych danych.
     */
    public void finish() { state = GameState.FINISHED; }

    /**
     * Sprawdza, czy aktualnie trwa tura gracza lokalnego.
     * @return true, jeśli gracz może wykonać ruch.
     */
    public boolean isPlayerTurn() { return playerTurn; }

    /**
     * Przełącza flagę tury na przeciwną wartość (XOR logiczny).
     */
    public void switchTurn() { playerTurn = !playerTurn; }

    /**
     * Weryfikuje warunki zakończenia gry poprzez sprawdzenie stanu flot obu graczy.
     * Jeśli wszystkie statki jednego z graczy są zatopione, zmienia stan gry na FINISHED.
     *
     * @return true, jeśli gra została zakończona w tym cyklu.
     */
    public boolean checkFinish() {
        if (player.getBoard().allSunk() || opponent.getBoard().allSunk()) {
            state = GameState.FINISHED;
            return true;
        }
        return false;
    }
}