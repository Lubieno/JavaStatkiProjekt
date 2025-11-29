package com.battleship.ui;

import com.battleship.board.Board;
import com.battleship.board.Cell;
import com.battleship.board.Position;
import com.battleship.controller.GameController;
import com.battleship.game.Event;
import com.battleship.game.GameState;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * FX UI — lewa plansza = przeciwnik, prawa = Twoja.
 * Dodano interfejs wyboru trybu sieciowego.
 */
public class FXUI extends Application {

    private static final int CELL_SIZE = 30;
    private UIController uiController;
    private Stage primaryStage;
    private ScheduledExecutorService networkChecker; // Do cyklicznego odświeżania

    private GridPane opponentGrid;
    private GridPane yourGrid;
    private Label infoLabel;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        this.uiController = new UIController();

        // Zamknij połączenie przy zamknięciu okna
        primaryStage.setOnCloseRequest(e -> {
            uiController.closeNetwork();
            if (networkChecker != null) networkChecker.shutdownNow();
        });

        showMainMenu();
    }

    // Nowa metoda do pokazania menu głównego
    private void showMainMenu() {
        if (networkChecker != null) networkChecker.shutdownNow(); // Zatrzymaj sprawdzanie sieci

        VBox root = new VBox(15);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));

        Label title = new Label("Statki – Wybierz Tryb");
        title.setStyle("-fx-font-size: 20px;");

        Button btnLocal = new Button("Gra Lokalna (vs Bot)");
        btnLocal.setOnAction(e -> {
            uiController.initLocalGame();
            startGameUI(false);
        });

        Button btnHost = new Button("Hostuj Grę (Klient-Klient)");
        btnHost.setOnAction(e -> showNetworkSetup(true));

        Button btnGuest = new Button("Dołącz do Gry (Klient-Klient)");
        btnGuest.setOnAction(e -> showNetworkSetup(false));

        root.getChildren().addAll(title, btnLocal, new Label("Tryb Sieciowy (P2P):"), btnHost, btnGuest);

        Scene scene = new Scene(root, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Statki – Menu");
        primaryStage.show();
    }

    // Nowa metoda do konfiguracji połączenia sieciowego
    private void showNetworkSetup(boolean isHost) {
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        Label title = new Label(isHost ? "Hostuj Grę" : "Dołącz do Gry");
        title.setStyle("-fx-font-size: 18px;");

        TextField nameField = new TextField("Gracz" + (isHost ? "H" : "G"));
        nameField.setPromptText("Twoja nazwa");
        nameField.setMaxWidth(200);

        TextField roomField = new TextField("ROOM5");
        roomField.setPromptText("Numer pokoju");
        roomField.setMaxWidth(200);

        TextField hostField = new TextField("localhost");
        hostField.setPromptText("Adres Host (tylko dla Gościa)");
        hostField.setMaxWidth(200);
        hostField.setDisable(isHost);

        Button startButton = new Button(isHost ? "Rozpocznij Nasłuchiwanie" : "Połącz");
        Label errorLabel = new Label();
        errorLabel.setTextFill(Color.RED);

        startButton.setOnAction(e -> {
            String name = nameField.getText().trim();
            String room = roomField.getText().trim();
            String host = hostField.getText().trim();

            if (name.isEmpty() || room.isEmpty() || (!isHost && host.isEmpty())) {
                errorLabel.setText("Wypełnij wszystkie pola!");
                return;
            }

            try {
                // Inicjalizacja gry i połączenia
                uiController.initNetworkGame(name, host, room, isHost);

                // Pokaż UI i rozpocznij sprawdzanie statusu
                startGameUI(true);
            } catch (Exception ex) {
                errorLabel.setText("Błąd sieci: " + ex.getMessage());
            }
        });

        Button backButton = new Button("Wróć");
        backButton.setOnAction(e -> showMainMenu());

        root.getChildren().addAll(title, nameField, roomField, hostField, errorLabel, startButton, backButton);
        primaryStage.setScene(new Scene(root, 400, 400));
    }


    private void startGameUI(boolean isNetworked) {
        GameController controller = uiController.getGameController();

        opponentGrid = makeGrid(controller.getGame().getOpponent().getBoard(), true);
        yourGrid     = makeGrid(controller.getGame().getPlayer().getBoard(), false);

        infoLabel = new Label(isNetworked ? "Oczekiwanie na połączenie przeciwnika..." : "Twoja tura. Strzelaj w planszę przeciwnika.");

        Button restart = new Button("Menu Główne");
        restart.setOnAction(e -> {
            uiController.closeNetwork();
            showMainMenu();
        });

        HBox boards = new HBox(20, opponentGrid, yourGrid);
        boards.setAlignment(Pos.CENTER);

        VBox root = new VBox(10, infoLabel, boards, restart);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-padding: 20;");

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Statki – " + (isNetworked ? "Gra Sieciowa P2P" : "Gra Lokalna vs Bot"));

        refresh();

        // Uruchomienie cyklicznego sprawdzania statusu sieci i odświeżania UI
        if (isNetworked) {
            if (networkChecker != null) networkChecker.shutdownNow();
            networkChecker = Executors.newSingleThreadScheduledExecutor();
            networkChecker.scheduleWithFixedDelay(this::checkNetworkStatus, 0, 100, TimeUnit.MILLISECONDS);
        }
    }

    // Nowa metoda do sprawdzania statusu sieci
    private void checkNetworkStatus() {
        if (uiController.getGameController().isNetworked()) {
            // W NetworkController wiadomości są odbierane i przetwarzane,
            // a my tylko cyklicznie odświeżamy UI.
            Platform.runLater(this::refresh);
        }
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

                    GameController controller = uiController.getGameController();

                    if (controller.getGame().getState() == GameState.FINISHED) return;

                    // Sprawdzenie tury
                    if (!controller.getGame().isPlayerTurn() && controller.isNetworked()) {
                        infoLabel.setText("Poczekaj na swoją turę!");
                        return;
                    }

                    // --- GRACZ STRZELA ---
                    Position pos = new Position(rr, cc);
                    Event event = uiController.shoot(pos);
                    infoLabel.setText(event.message());
                    // W trybie lokalnym: refresh wywoła logikę Bota
                    refresh();

                    if (!controller.isNetworked()) {
                        if (controller.getGame().getState() == GameState.FINISHED) return;

                        // --- BOT STRZELA TYLKO JEŚLI GRACZ SPUDŁOWAŁ ---
                        while (!controller.getGame().isPlayerTurn()
                                && controller.getGame().getState() != GameState.FINISHED) {

                            String botRes = controller.botTurn();
                            infoLabel.setText(botRes);
                            refresh();
                        }
                    }
                });

                grid.add(rect, c, r);
            }
        }
        return grid;
    }

    private void refresh() {
        // Zabezpieczenie przed wywołaniem przed inicjalizacją
        if (uiController == null || uiController.getGameController() == null) return;

        GameController controller = uiController.getGameController();

        // Oczekiwanie na połączenie (gdy Host nasłuchuje)
        if (controller.isNetworked() && !uiController.getNetworkController().isConnected()) {
            infoLabel.setText("Oczekiwanie na połączenie i uzgodnienie pokoju...");
            return;
        }

        updateGrid(opponentGrid,
                controller.getGame().getOpponent().getBoard(),
                true); // Plansza przeciwnika, hideShips = true

        updateGrid(yourGrid,
                controller.getGame().getPlayer().getBoard(),
                false); // Twoja plansza, hideShips = false

        // Aktualizacja labela
        if (controller.getGame().getState() == GameState.IN_PROGRESS) {
            String playerName = controller.getGame().getPlayer().getName();
            String opponentName = controller.getGame().getOpponent().getName();

            String turnStatus = controller.getGame().isPlayerTurn()
                    ? "Twoja tura, " + playerName + "!"
                    : "Tura przeciwnika, " + opponentName + ".";

            infoLabel.setText(turnStatus);
        } else if (controller.getGame().getState() == GameState.FINISHED) {
            infoLabel.setText(controller.getGame().getPlayer().getBoard().allSunk() ? "PRZEGRAŁEŚ!" : "WYGRAŁEŚ!");
        }
    }

    private void updateGrid(GridPane grid, Board board, boolean hideShips) {

        for (Node node : grid.getChildren()) {

            if (!(node instanceof Rectangle rect))
                continue;

            Integer col = GridPane.getColumnIndex(rect);
            Integer row = GridPane.getRowIndex(rect);

            if (row == null || col == null) continue;

            Cell cell = board.getCell(new Position(row, col));

            // POPRAWKA: RENDEROWANIE TRAFIEŃ I PUDŁÓW NA PLANSZY PRZECIWNIKA
            switch (cell.getState()) {
                case HIT -> rect.setFill(Color.RED);
                case MISS -> rect.setFill(Color.GRAY);
                default -> {
                    // Domyślny stan: SHIP lub EMPTY
                    if (!hideShips && cell.hasShip())
                        rect.setFill(Color.DARKBLUE); // Pokaż statki na WŁASNEJ planszy
                    else
                        rect.setFill(Color.LIGHTBLUE); // Ukryj statki na planszy PRZECIWNIKA
                }
            }
        }
    }

    public static void main(String[] args) {
        launch();
    }
}