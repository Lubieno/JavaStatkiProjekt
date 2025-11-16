package com.battleship.ui;

import com.battleship.controller.GameController;
import com.battleship.util.GameLogger;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ConsoleUI implements UI {

    private GameController controller;
    private final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

    @Override
    public void setController(GameController gc) {
        this.controller = gc;
    }

    @Override
    public void showMainMenu() {
        try {
            GameLogger.log("1) Host game");
            GameLogger.log("2) Connect to host");
            String choice = in.readLine();

            if ("1".equals(choice)) {
                GameLogger.log("Enter port:");
                int port = Integer.parseInt(in.readLine());
                controller.host(port);
            } else {
                GameLogger.log("Enter host:");
                String host = in.readLine();
                GameLogger.log("Enter port:");
                int port = Integer.parseInt(in.readLine());
                controller.connect(host, port);
            }
        } catch (Exception e) {
            GameLogger.log("Console error: " + e.getMessage());
        }
    }
}
