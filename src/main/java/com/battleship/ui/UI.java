package com.battleship.ui;

import com.battleship.controller.GameController;

/**
 * Interfejs definiujący kontrakt dla warstwy widoku (View) w architekturze MVC.
 * Pozwala na abstrakcję konkretnej implementacji interfejsu użytkownika
 * (np. JavaFX, Swing, Konsola) od logiki sterującej.
 *
 * Dzięki temu kontrolery nie zależą bezpośrednio od biblioteki graficznej (`FXUI`),
 * co ułatwia testowanie i ewentualną wymianę technologii frontendowej.
 */
public interface UI {
    /**
     * Wstrzykuje zależność do głównego kontrolera gry.
     * Umożliwia widokowi komunikację zwrotną z warstwą logiki (np. przesyłanie akcji użytkownika).
     *
     * @param gc Instancja kontrolera gry.
     */
    void setController(GameController gc);

    /**
     * Nakazuje warstwie prezentacji wyświetlenie ekranu głównego menu.
     * Metoda ta powinna zresetować widok do stanu początkowego.
     */
    void showMainMenu();
}