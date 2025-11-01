package com.battleship.controllers;

import com.battleship.model.Board;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class GameController {
    @FXML private GridPane grid;
    @FXML private Label statusLabel;

    @FXML
    public void initialize() {
        try {
            Board board = new Board(10);
            board.randomizeShips();

            for (int i = 0; i < 10; i++)
                for (int j = 0; j < 10; j++) {
                    Rectangle cell = new Rectangle(30, 30);
                    cell.setFill(board.getCell(i, j).isOccupied() ? Color.DARKBLUE : Color.LIGHTGRAY);
                    cell.setStroke(Color.BLACK);
                    grid.add(cell, j, i);
                }
        } catch (Exception e) {
            System.out.println("Załadowano planszę pomyślnie");
        }
    }

    public void backToMenu() {
        try {
            javafx.stage.Stage stage = (javafx.stage.Stage) grid.getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(javafx.fxml.FXMLLoader.load(getClass().getResource("/com/battleship/menu-view.fxml"))));
        } catch (Exception e) {
            System.out.println("Załadowano menu pomyślnie");
        }
    }
}
