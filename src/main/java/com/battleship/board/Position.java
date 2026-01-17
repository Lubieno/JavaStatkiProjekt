package com.battleship.board;

import java.io.Serializable;

/**
 * Obiekt transferu danych (DTO) reprezentujący współrzędne w układzie kartezjańskim (wiersz, kolumna).
 * Zaimplementowany jako rekord (Java Record) w celu zapewnienia niemutowalności (immutability)
 * oraz automatycznej generacji metod `equals`, `hashCode` i `toString`.
 *
 * Implementacja interfejsu `Serializable` jest kluczowa, ponieważ obiekty te są
 * przesyłane bezpośrednio przez strumień sieciowy (ObjectOutputStream) jako część pakietu akcji gracza.
 */
public record Position(int row, int col) implements Serializable {
    /**
     * Metoda walidująca, sprawdzająca czy współrzędne mieszczą się w zakresie tablicy.
     * Zapobiega wyrzuceniu wyjątku `ArrayIndexOutOfBoundsException` przy próbie dostępu do planszy.
     *
     * @param size Rozmiar planszy (zazwyczaj 10).
     * @return true, jeśli pozycja jest poprawna.
     */
    public boolean inBounds(int size) {
        return row >= 0 && col >= 0 && row < size && col < size;
    }
}