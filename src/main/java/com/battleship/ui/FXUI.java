package com.battleship.ui;

import com.battleship.board.Board;
import com.battleship.board.Cell;
import com.battleship.board.Position;
import com.battleship.controller.GameController;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import com.battleship.game.GameState;

/**
 * FX UI — lewa plansza = przeciwnik, prawa = Twoja.
 */
public class FXUI extends Application {

    private static final int CELL_SIZE = 30;
    private final GameController controller = new GameController();

    private GridPane opponentGrid;
    private GridPane yourGrid;
    private Label infoLabel;

    @Override
    public void start(Stage stage) {

        opponentGrid = makeGrid(controller.getGame().getOpponent().getBoard(), true);
        yourGrid     = makeGrid(controller.getGame().getPlayer().getBoard(), false);

        infoLabel = new Label("Twoja tura. Strzelaj w planszę przeciwnika.");

        Button restart = new Button("Restart");
        restart.setOnAction(e -> {
            FXUI newUI = new FXUI();
            try { newUI.start(new Stage()); }
            catch (Exception ex) { ex.printStackTrace(); }
            stage.close();
        });

        HBox boards = new HBox(20, opponentGrid, yourGrid);
        boards.setAlignment(Pos.CENTER);

        VBox root = new VBox(10, infoLabel, boards, restart);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-padding: 20;");

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Statki – Gra vs Bot");
        stage.show();

        refresh();
    }

    private GridPane makeGrid(Board board, boolean interactive) {
        GridPane grid = new GridPane();
        grid.setGridLinesVisible(true);

        for (int r = 0; r < Board.SIZE; r++) {
            for (int c = 0; c < Board.SIZE; c++) {

                Rectangle rect = new Rectangle(CELL_SIZE, CELL_SIZE);
                rect.setStroke(Color.GRAY);
                rect.setFill(Color.LIGHTBLUE);

                int rr = r, cc = c;

                rect.setOnMouseClicked(ev -> {

                    if (!interactive) return;
                    if (ev.getButton() != MouseButton.PRIMARY) return;
                    if (controller.getGame().getState() == GameState.FINISHED) return;

                    // jeśli nie tura gracza, ignorujemy klik
                    if (!controller.getGame().isPlayerTurn()) return;

                    // --- GRACZ STRZELA ---
                    Position pos = new Position(rr, cc);
                    String result = controller.playerShoot(pos);
                    infoLabel.setText(result);
                    refresh();

                    if (controller.getGame().getState() == GameState.FINISHED) return;

                    // --- BOT STRZELA TYLKO JEŚLI GRACZ SPUDŁOWAŁ ---
                    while (!controller.getGame().isPlayerTurn()
                            && controller.getGame().getState() != GameState.FINISHED) {

                        String botRes = controller.botTurn();
                        infoLabel.setText(botRes);
                        refresh();
                    }
                });

                grid.add(rect, c, r);
            }
        }
        return grid;
    }

    private void refresh() {
        updateGrid(opponentGrid,
                controller.getGame().getOpponent().getBoard(),
                true);

        updateGrid(yourGrid,
                controller.getGame().getPlayer().getBoard(),
                false);
    }

    private void updateGrid(GridPane grid, Board board, boolean hideShips) {

        for (Node node : grid.getChildren()) {

            if (!(node instanceof Rectangle rect))
                continue;

            Integer col = GridPane.getColumnIndex(rect);
            Integer row = GridPane.getRowIndex(rect);

            if (row == null || col == null) continue;

            Cell cell = board.getCell(new Position(row, col));

            switch (cell.getState()) {
                case HIT -> rect.setFill(Color.RED);
                case MISS -> rect.setFill(Color.GRAY);
                default -> {
                    if (!hideShips && cell.hasShip())
                        rect.setFill(Color.DARKBLUE); // pokaż statki gracza
                    else
                        rect.setFill(Color.LIGHTBLUE);
                }
            }
        }
    }

    public static void main(String[] args) {
        launch();
    }
}