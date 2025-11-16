package com.battleship.ui;

import com.battleship.controller.GameController;

public interface UI {
    void setController(GameController gc);
    void showMainMenu();
}
