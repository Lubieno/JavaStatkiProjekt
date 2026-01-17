package com.battleship.board;

/**
 * Typ wyliczeniowy (Enum) definiujący możliwe orientacje statków na planszy.
 * Wykorzystywany przez algorytmy rozmieszczania statków do obliczania wektorów przesunięcia (delta row/delta col).
 */
public enum Orientation {
    UP, DOWN, LEFT, RIGHT
}