package com.battleship.player;

import com.battleship.board.Board;

/**
 * Abstrakcyjna klasa bazowa dla wszystkich typów graczy.
 * Definiuje wspólne cechy, takie jak nazwa oraz własna plansza (Board).
 * Dzięki dziedziczeniu, GameController może operować na abstrakcji gracza,
 * nie wiedząc czy gra toczy się przeciwko AI, czy człowiekowi przez sieć.
 */
public abstract class Player {
    protected final Board board = new Board();
    protected final String name;
    public Player(String name) { this.name = name; }
    public Board getBoard() { return board; }
    public String getName(){ return name; }
}