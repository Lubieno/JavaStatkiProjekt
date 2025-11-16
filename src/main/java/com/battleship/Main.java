package com.battleship;

import com.battleship.controller.GameController;
import com.battleship.network.NetworkManager;
import com.battleship.ui.ConsoleUI;

public class Main {
    public static void main(String[] args) throws Exception {
        ConsoleUI ui = new ConsoleUI();
        NetworkManager nm = new NetworkManager();
        GameController gc = new GameController(ui, nm);
        ui.setController(gc);
        gc.start();
    }
}
