package com.battleship.network;

/**
 * Prosty obiekt wiadomości sieciowej.
 */
public class Message {
    private String typ;
    private String tresc;

    public Message(String typ, String tresc) {
        this.typ = typ;
        this.tresc = tresc;
    }

    public String getTyp() { return typ; }
    public String getTresc() { return tresc; }
}
