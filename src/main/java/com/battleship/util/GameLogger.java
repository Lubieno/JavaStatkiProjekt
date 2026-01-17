package com.battleship.util;

/**
 * Klasa narzędziowa (Utility Class) realizująca prosty mechanizm logowania zdarzeń.
 * Zastosowano tu statyczną metodę dostępową, aby umożliwić rejestrowanie logów
 * z dowolnego miejsca w kodzie bez konieczności wstrzykiwania zależności.
 *
 * Obecna implementacja stanowi wrapper na standardowy strumień wyjścia (System.out),
 * co ułatwia debugowanie i pozwala w przyszłości na łatwą podmianę implementacji
 */
public class GameLogger {
    /**
     * Rejestruje komunikat w konsoli systemowej z prefiksem [LOG].
     *
     * @param s Treść komunikatu do zalogowania.
     */
    public static void log(String s) {
        System.out.println("[LOG] " + s);
    }
}