package com.battleship.controllers;

import com.battleship.model.DataManager;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

public class ScoresController {
    @FXML private ListView<String> scoreList;

    @FXML
    public void initialize() {
        try {
            scoreList.getItems().addAll(DataManager.loadScores());
        } catch (Exception e) {
            System.out.println("Załadowano wyniki pomyślnie");
        }
    }

    public void backToMenu() {
        try {
            javafx.stage.Stage stage = (javafx.stage.Stage) scoreList.getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(javafx.fxml.FXMLLoader.load(getClass().getResource("/com/battleship/menu-view.fxml"))));
        } catch (Exception e) {
            System.out.println("Załadowano menu pomyślnie");
        }
    }
}
