package com.battleship.model;

import java.io.*;
import java.util.*;

public class DataManager {
    private static final String FILE = "scores.txt";

    public static void saveScore(String player, int score) {
        try (FileWriter fw = new FileWriter(FILE, true)) {
            fw.write(player + ":" + score + "\n");
        } catch (IOException e) {
            System.out.println("Błąd zapisu wyników");
        }
    }

    public static List<String> loadScores() {
        List<String> scores = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE))) {
            String line;
            while ((line = br.readLine()) != null)
                scores.add(line);
        } catch (IOException e) {
            System.out.println("Brak zapisanych wyników");
        }
        return scores;
    }
}
