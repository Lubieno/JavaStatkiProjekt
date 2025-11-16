package com.battleship.ui;

import com.battleship.board.Position;
import com.battleship.controller.GameController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class UIController {

    private GameController controller;

    @FXML
    private TextField hostField, portField;

    @FXML
    private Button connectBtn, hostBtn;

    @FXML
    private GridPane myGrid, enemyGrid;

    public void setController(GameController c) {
        this.controller = c;
        setupEnemyGrid();
    }

    @FXML
    private void onHost() {
        try {
            int port = Integer.parseInt(portField.getText());
            controller.host(port);
        } catch (Exception ignored) {}
    }

    @FXML
    private void onConnect() {
        try {
            controller.connect(hostField.getText(),
                    Integer.parseInt(portField.getText()));
        } catch (Exception ignored) {}
    }

    private void setupEnemyGrid() {
        for (int r = 0; r < 10; r++)
            for (int c = 0; c < 10; c++) {
                Button b = new Button();
                b.setPrefSize(30, 30);

                int rr = r, cc = c;
                b.setOnAction(e -> {
                    try {
                        controller.sendShot(new Position(rr, cc));
                    } catch (Exception ignored) {}
                });

                enemyGrid.add(b, c, r);
            }
    }
}
