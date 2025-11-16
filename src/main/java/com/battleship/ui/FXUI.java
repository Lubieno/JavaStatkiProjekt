package com.battleship.ui;

import com.battleship.controller.GameController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class FXUI extends Application implements UI {

    private static GameController controller;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/MainView.fxml"));
        Scene scene = new Scene(loader.load());

        UIController uiController = loader.getController();
        uiController.setController(controller);

        stage.setScene(scene);
        stage.setTitle("Battleship");
        stage.show();
    }

    @Override
    public void setController(GameController gc) {
        controller = gc;
    }

    @Override
    public void showMainMenu() {
        launch();
    }
}
