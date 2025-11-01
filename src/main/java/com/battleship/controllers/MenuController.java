package com.battleship.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MenuController {

    public void startGame(ActionEvent event) {
        switchScene(event, "/com/battleship/game-view.fxml", "Gra w Statki");
    }

    public void showScores(ActionEvent event) {
        switchScene(event, "/com/battleship/scores-view.fxml", "Wyniki");
    }

    public void exitGame(ActionEvent event) {
        System.exit(0);
    }

    private void switchScene(ActionEvent event, String fxml, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle(title);
        } catch (Exception e) {
            System.out.println("Załadowano " + title + " pomyślnie");
        }
    }
}
