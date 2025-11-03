package com.battleship.util;

import com.battleship.game.GameState;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Odpowiada za zapis danych trwałych (wyników gry) do pliku JSON.
 */
public class GameLogger {

    public void zapiszDoPliku(GameState stan) {
        String dane = "{\n" +
                "  \"zakonczona\": " + stan.isZakonczona() + ",\n" +
                "  \"aktualnyGracz\": " + stan.getAktualnyGracz() + "\n" +
                "}";
        try (FileWriter f = new FileWriter("game_summary.json")) {
            f.write(dane);
            System.out.println("Zapisano dane trwałe do game_summary.json");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
