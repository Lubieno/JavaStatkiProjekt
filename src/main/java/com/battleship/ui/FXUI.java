package com.battleship.ui;

import com.battleship.board.Board;
import com.battleship.board.Cell;
import com.battleship.board.Orientation;
import com.battleship.board.Position;
import com.battleship.controller.GameController;
import com.battleship.data.Profile;
import com.battleship.data.ProfileManager;
import com.battleship.game.Event;
import com.battleship.game.GameState;
import com.battleship.network.NetworkManager;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class FXUI extends Application {

    private static final int CELL_SIZE = 35;

    private static final String BG_GRADIENT_START = "#ffe6fa";
    private static final String BG_GRADIENT_END = "#ffcce6";
    private static final String PANEL_BG = "rgba(255, 255, 255, 0.85)";

    private static final String PUSHEEN_GREY = "#9ba4b5";
    private static final String SHIP_BORDER = "#7a8291";
    private static final String MINT_GREEN = "#b2f7ef";
    private static final String BUTTON_TEXT = "#5d4e60";
    private static final String WATER_COLOR = "#ffffff";
    private static final String GRID_LINES = "#ffb3d9";

    private static final String HIT_COLOR = "#ff6bb3";
    private static final String MISS_COLOR = "#a2d2ff";
    private static final String SUNK_COLOR = "#6b7a8f";

    private static final String BUTTON_STYLE =
            "-fx-background-color: " + MINT_GREEN + "; " +
                    "-fx-text-fill: " + BUTTON_TEXT + "; " +
                    "-fx-font-weight: bold; " +
                    "-fx-font-size: 14px; " +
                    "-fx-background-radius: 20; " +
                    "-fx-border-color: #98e6d9; " +
                    "-fx-border-radius: 20; " +
                    "-fx-border-width: 2; " +
                    "-fx-cursor: hand; " +
                    "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 3, 0, 0, 2);";

    private static final String BUTTON_HOVER_STYLE =
            "-fx-background-color: #ffcce6; " +
                    "-fx-text-fill: " + BUTTON_TEXT + "; " +
                    "-fx-font-weight: bold; " +
                    "-fx-font-size: 14px; " +
                    "-fx-background-radius: 20; " +
                    "-fx-border-color: #ff99cc; " +
                    "-fx-border-radius: 20; " +
                    "-fx-border-width: 2; " +
                    "-fx-cursor: hand;";

    private static final String INPUT_STYLE =
            "-fx-background-color: white; " +
                    "-fx-border-color: " + GRID_LINES + "; " +
                    "-fx-border-width: 2; " +
                    "-fx-border-radius: 10; " +
                    "-fx-background-radius: 10; " +
                    "-fx-text-fill: " + BUTTON_TEXT + ";";

    private static final String PANEL_STYLE =
            "-fx-background-color: " + PANEL_BG + "; " +
                    "-fx-background-radius: 20; " +
                    "-fx-effect: dropshadow(three-pass-box, rgba(150,100,120,0.2), 10, 0, 0, 5);";


    private UIController uiController;
    private Stage primaryStage;
    private ScheduledExecutorService gameLoop;

    private ProfileManager profileManager;
    private SoundManager soundManager;

    private Instant gameStartTime;
    private GridPane opponentGrid;
    private GridPane yourGrid;
    private Label infoLabel;
    private Label timerLabel;
    private final Set<Position> myFlags = new HashSet<>();

    private Orientation currentOrientation = Orientation.RIGHT;
    private int selectedShipSize = 0;
    private final int[] availableShips = {0, 4, 3, 2, 1};

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        this.profileManager = new ProfileManager();
        this.soundManager = new SoundManager();
        this.uiController = new UIController();

        primaryStage.setOnCloseRequest(e -> { shutdown(); Platform.exit(); });

        showProfileSelection();
    }

    private void shutdown() {
        uiController.closeNetwork();
        if (gameLoop != null) gameLoop.shutdownNow();
    }

    private Background createMainBackground() {
        Stop[] stops = new Stop[] {
                new Stop(0, Color.web(BG_GRADIENT_START)),
                new Stop(1, Color.web(BG_GRADIENT_END))
        };
        LinearGradient lg = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, stops);
        return new Background(new BackgroundFill(lg, CornerRadii.EMPTY, Insets.EMPTY));
    }

    private void showProfileSelection() {
        VBox root = new VBox(30);
        root.setBackground(createMainBackground());
        root.setAlignment(Pos.CENTER);

        Text title = new Text("WYBIERZ SWOJEGO KOTA");
        title.setFont(Font.font("Verdana", FontWeight.BOLD, 40));
        title.setFill(Color.web(BUTTON_TEXT));

        GridPane profilesGrid = new GridPane();
        profilesGrid.setAlignment(Pos.CENTER);
        profilesGrid.setHgap(25);
        profilesGrid.setVgap(25);

        List<Profile> profiles = profileManager.getProfiles();
        int col = 0;
        int row = 0;

        for (Profile p : profiles) {
            Text nameText = new Text(p.getName());
            nameText.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
            nameText.setFill(Color.web("#805b87"));

            String statsString =
                    "Mecze: " + p.getGamesPlayed() + "   Zbite: " + p.getShipsSunk() + "\n" +
                            "Wygrane: " + p.getWins() + "   Przegrane: " + p.getLosses() + "\n" +
                            "Skuteczność: " + p.getWinRate() + "\n" +
                            "Czas gry: " + p.getFormattedTime();

            Text statsText = new Text(statsString);
            statsText.setFont(Font.font("Arial", 13));
            statsText.setFill(Color.web(BUTTON_TEXT));
            statsText.setTextAlignment(TextAlignment.CENTER);

            VBox btnContent = new VBox(8, nameText, statsText);
            btnContent.setAlignment(Pos.CENTER);

            Button b = createStyledButton("", e -> {
                soundManager.playClick();
                profileManager.setCurrentProfile(p);
                showMainMenu();
            });

            b.setGraphic(btnContent);
            b.setPrefSize(300, 160);

            profilesGrid.add(b, col, row);

            col++;
            if (col > 2) {
                col = 0;
                row++;
            }
        }

        root.getChildren().addAll(title, profilesGrid);
        primaryStage.setScene(new Scene(root, 1100, 750));
        primaryStage.setTitle("Cat Wars - Profile");
        primaryStage.show();
    }
    private void showMainMenu() {
        shutdown();
        soundManager.resumeMusic();

        VBox root = new VBox(20);
        root.setBackground(createMainBackground());
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));

        Text title = new Text("🐱 CAT WARS 🐱");
        title.setFont(Font.font("Verdana", FontWeight.BOLD, 50));
        title.setFill(Color.web(BUTTON_TEXT));
        title.setEffect(new DropShadow(5, Color.web("#ff99cc")));

        Profile p = profileManager.getCurrentProfile();
        String statsText = "Zalogowany jako: " + p.getName().toUpperCase() + "\n" +
                "Mecze: " + p.getGamesPlayed() + "  |  Zbite: " + p.getShipsSunk() + "\n" +
                "Wygrane: " + p.getWins() + "  |  Przegrane: " + p.getLosses() + "\n" +
                "Skuteczność: " + p.getWinRate() + "  |  Czas: " + p.getFormattedTime();

        Label statsLabel = new Label(statsText);
        statsLabel.setStyle("-fx-text-alignment: center; -fx-text-fill: #5d4e60; -fx-font-weight: bold; -fx-font-size: 14px; -fx-background-color: rgba(255,255,255,0.6); -fx-padding: 15; -fx-background-radius: 15; -fx-border-color: " + GRID_LINES + "; -fx-border-radius: 15;");

        VBox menuBox = new VBox(15);
        menuBox.setAlignment(Pos.CENTER);
        menuBox.setMaxWidth(400);
        menuBox.setStyle(PANEL_STYLE + "-fx-padding: 40;");

        TextField ipField = new TextField("localhost:" + NetworkManager.DEFAULT_PORT);
        ipField.setPromptText("IP:Port");
        ipField.setStyle(INPUT_STYLE);

        Button btnJoin = createStyledButton("DOŁĄCZ DO GRY", e -> { soundManager.playClick(); handleConnect(false, ipField.getText()); });
        Button btnHost = createStyledButton("STWÓRZ SESJĘ", e -> { soundManager.playClick(); handleConnect(true, ipField.getText()); });
        Button btnRules = createStyledButton("ZASADY", e -> { soundManager.playClick(); showRules(); });
        Button btnAuthors = createStyledButton("AUTORZY", e -> { soundManager.playClick(); showAuthors(); });
        Button btnChangeProfile = createStyledButton("ZMIEŃ KOTA", e -> { soundManager.playClick(); showProfileSelection(); });
        Button btnExit = createStyledButton("WYJŚCIE", e -> Platform.exit());

        menuBox.getChildren().addAll(statsLabel, new Separator(), new Label("IP (dla gościa):"), ipField, btnJoin, btnHost, new Separator(), btnRules, btnAuthors, btnChangeProfile, btnExit);
        root.getChildren().addAll(title, menuBox);

        Scene scene = new Scene(root, 1100, 750);
        primaryStage.setScene(scene);
    }

    private void showAuthors() {
        StackPane root = new StackPane();
        root.setBackground(createMainBackground());

        VBox content = new VBox(20);
        content.setAlignment(Pos.CENTER);
        content.setStyle(PANEL_STYLE + "-fx-padding: 50; -fx-max-width: 500;");

        Text title = new Text("AUTORZY PROJEKTU");
        title.setFont(Font.font("Verdana", FontWeight.BOLD, 30));
        title.setFill(Color.web(BUTTON_TEXT));

        Label names = new Label("Natalia Chruściel\nKamil Lubieniecki");
        names.setFont(Font.font("Arial", 20));
        names.setTextFill(Color.web("#666"));
        names.setStyle("-fx-text-alignment: center;");

        content.getChildren().addAll(title, names);

        Button btnBack = createStyledButton("WRÓĆ", e -> { soundManager.playClick(); showMainMenu(); });
        StackPane.setAlignment(btnBack, Pos.TOP_RIGHT);
        StackPane.setMargin(btnBack, new Insets(30));

        root.getChildren().addAll(content, btnBack);
        primaryStage.setScene(new Scene(root, 1100, 750));
    }

    private void showRules() {
        VBox root = new VBox(20);
        root.setBackground(createMainBackground());
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));

        VBox panel = new VBox(20);
        panel.setAlignment(Pos.CENTER_LEFT);
        panel.setMaxWidth(600);
        panel.setStyle(PANEL_STYLE + "-fx-padding: 40;");

        Label title = new Label("ZASADY GRY:");
        title.setFont(Font.font("Verdana", FontWeight.BOLD, 24));
        title.setTextFill(Color.web(BUTTON_TEXT));

        Label l = new Label(
                "1. Rozmieść swoje koty (statki).\n    Pamiętaj o odstępie (nie mogą się stykać!).\n\n" +
                        "2. Zatop flotę przeciwnika.\n\n" +
                        "3. Trafienie = dodatkowy ruch.\n\n" +
                        "4. PPM na planszy wroga stawia flagę (🐾).");
        l.setTextFill(Color.web("#666"));
        l.setFont(Font.font("Arial", 18));

        panel.getChildren().addAll(title, l);

        Button back = createStyledButton("WRÓĆ", e -> { soundManager.playClick(); showMainMenu(); });

        root.getChildren().addAll(panel, back);
        primaryStage.setScene(new Scene(root, 1100, 750));
    }

    private void handleConnect(boolean isHost, String ipPortRaw) {
        String host = "localhost";
        if (ipPortRaw.contains(":")) host = ipPortRaw.split(":")[0];

        try {
            uiController.initNetworkGame(isHost ? "Host" : "Guest", isHost ? null : host, "ROOM1", isHost);
            uiController.getGameController().setProfileManager(profileManager);
            showPlacementScreen();
        } catch (Exception e) {
            Alert a = new Alert(Alert.AlertType.ERROR, "Błąd: " + e.getMessage());
            a.show();
        }
    }

    private void showPlacementScreen() {
        availableShips[1] = 4; availableShips[2] = 3; availableShips[3] = 2; availableShips[4] = 1;
        selectedShipSize = 0;

        uiController.getGameController().getGame().getPlayer().getBoard().clear();

        BorderPane root = new BorderPane();
        root.setBackground(createMainBackground());

        Label header = new Label("ROZMIEŚĆ SWOJE KOTKI");
        header.setFont(Font.font("Verdana", FontWeight.BOLD, 28));
        header.setTextFill(Color.web(BUTTON_TEXT));
        HBox top = new HBox(header);
        top.setAlignment(Pos.CENTER);
        top.setPadding(new Insets(20));
        root.setTop(top);

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(1); grid.setVgap(1);
        grid.setStyle("-fx-border-color: " + GRID_LINES + "; -fx-border-width: 4; -fx-background-color: white; -fx-padding: 5; -fx-background-radius: 5; -fx-border-radius: 5;");

        refreshPlacementGrid(grid);
        root.setCenter(grid);

        VBox sidebar = new VBox(20);
        sidebar.setPadding(new Insets(20));
        sidebar.setAlignment(Pos.TOP_CENTER);
        sidebar.setPrefWidth(320);
        sidebar.setStyle("-fx-background-color: rgba(255,255,255,0.6); -fx-border-left-color: white; -fx-border-left-width: 2;");

        Label info = new Label("Wybierz rozmiar:");
        info.setTextFill(Color.web(BUTTON_TEXT));
        info.setFont(Font.font(16));

        VBox shipsBox = new VBox(10);
        updateShipButtons(shipsBox);

        Label rotLabel = new Label("Obrót: PPM na planszy");
        rotLabel.setTextFill(Color.GRAY);
        rotLabel.setStyle("-fx-text-alignment: center;");

        Button autoBtn = createStyledButton("LOSOWO", e -> {
            soundManager.playClick();
            uiController.getGameController().getGame().getPlayer().getBoard().randomPlaceFleet();
            for(int i=0;i<5;i++) availableShips[i] = 0;
            refreshPlacementGrid(grid);
            updateShipButtons(shipsBox);
        });

        Button clearBtn = createStyledButton("WYCZYŚĆ", e -> {
            soundManager.playClick();
            uiController.getGameController().getGame().getPlayer().getBoard().clear();
            availableShips[1]=4; availableShips[2]=3; availableShips[3]=2; availableShips[4]=1;
            refreshPlacementGrid(grid);
            updateShipButtons(shipsBox);
        });

        Button startBtn = createStyledButton("GOTOWY DO WALKI!", e -> {
            soundManager.playClick();
            if (isFleetPlaced()) {
                showGameScreenWaiting();
            } else {
                new Alert(Alert.AlertType.WARNING, "Rozmieść wszystkie statki!").show();
            }
        });
        startBtn.setStyle("-fx-background-color: " + HIT_COLOR + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20; -fx-cursor: hand; -fx-font-size: 16px;");

        sidebar.getChildren().addAll(info, shipsBox, rotLabel, new Separator(), autoBtn, clearBtn, new Region(){{setPrefHeight(30);}}, startBtn);
        root.setRight(sidebar);

        grid.setUserData(shipsBox);

        primaryStage.setScene(new Scene(root, 1100, 750));
    }

    private boolean isFleetPlaced() {
        return availableShips[1] == 0 && availableShips[2] == 0 && availableShips[3] == 0 && availableShips[4] == 0;
    }

    private void updateShipButtons(VBox container) {
        container.getChildren().clear();
        container.getChildren().add(createShipSelectBtn(4, "Duży Kot (1x)", container));
        container.getChildren().add(createShipSelectBtn(3, "Średni Kot (2x)", container));
        container.getChildren().add(createShipSelectBtn(2, "Mały Kot (3x)", container));
        container.getChildren().add(createShipSelectBtn(1, "Kociak (4x)", container));
    }

    private Button createShipSelectBtn(int size, String label, VBox container) {
        String count = " [" + availableShips[size] + "]";
        Button b = new Button(label + count);
        b.setPrefWidth(250);
        b.setStyle(selectedShipSize == size ? BUTTON_HOVER_STYLE : BUTTON_STYLE);

        if (availableShips[size] == 0) {
            b.setDisable(true);
            b.setStyle("-fx-background-color: #eee; -fx-text-fill: #aaa; -fx-background-radius: 20;");
        }

        b.setOnAction(e -> {
            soundManager.playClick();
            if (availableShips[size] > 0) {
                selectedShipSize = size;
                updateShipButtons(container);
            }
        });
        return b;
    }

    private void refreshPlacementGrid(GridPane grid) {
        grid.getChildren().clear();
        Board b = uiController.getGameController().getGame().getPlayer().getBoard();

        for(int r=0; r<10; r++) {
            for(int c=0; c<10; c++) {
                Rectangle rect = new Rectangle(CELL_SIZE, CELL_SIZE);
                rect.setArcWidth(10); rect.setArcHeight(10);
                rect.setStroke(Color.web(GRID_LINES));

                Cell cell = b.getCell(new Position(r, c));
                if (cell.hasShip()) rect.setFill(Color.web(PUSHEEN_GREY));
                else rect.setFill(Color.web(WATER_COLOR));

                int rr=r, cc=c;
                rect.setOnMouseEntered(e -> {
                    if (selectedShipSize > 0) rect.setFill(Color.web(MISS_COLOR));
                });
                rect.setOnMouseExited(e -> {
                    if (!b.getCell(new Position(rr,cc)).hasShip()) rect.setFill(Color.web(WATER_COLOR));
                    else rect.setFill(Color.web(PUSHEEN_GREY));
                });

                rect.setOnMouseClicked(e -> {
                    if (e.getButton() == MouseButton.SECONDARY) {
                        currentOrientation = (currentOrientation == Orientation.RIGHT) ? Orientation.DOWN : Orientation.RIGHT;
                    } else if (e.getButton() == MouseButton.PRIMARY) {
                        if (selectedShipSize > 0 && availableShips[selectedShipSize] > 0) {
                            List<Position> coords = b.getLinearCoords(new Position(rr, cc), currentOrientation, selectedShipSize);
                            if (coords != null && b.placeShip(coords)) {
                                soundManager.playClick();
                                availableShips[selectedShipSize]--;
                                refreshPlacementGrid(grid);

                                if(grid.getUserData() instanceof VBox vbox) updateShipButtons(vbox);

                                if (availableShips[selectedShipSize] == 0) selectedShipSize = 0;
                            }
                        }
                    }
                });

                grid.add(rect, c, r);
            }
        }
    }

    private void showGameScreenWaiting() {
        VBox root = new VBox(20);
        root.setBackground(createMainBackground());
        root.setAlignment(Pos.CENTER);

        Label status = new Label("Czekamy na drugiego kota...");
        status.setFont(Font.font("Verdana", 24));
        status.setTextFill(Color.web(BUTTON_TEXT));
        ProgressIndicator pin = new ProgressIndicator();
        pin.setStyle("-fx-progress-color: " + HIT_COLOR + ";");

        root.getChildren().addAll(status, pin);
        primaryStage.setScene(new Scene(root, 1100, 750));

        gameLoop = Executors.newSingleThreadScheduledExecutor();
        gameLoop.scheduleWithFixedDelay(() -> {
            boolean connected = uiController.getNetworkController().isConnected();

            if (connected) {
                Platform.runLater(() -> {
                    status.setText("POŁĄCZONO! STARTUJEMY!");
                    status.setTextFill(Color.web(HIT_COLOR));
                    pin.setVisible(false);

                    if (gameLoop != null && !gameLoop.isShutdown()) {
                        gameLoop.shutdown();
                    }

                    PauseTransition pause = new PauseTransition(Duration.seconds(1.5));
                    pause.setOnFinished(e -> showGameScreen());
                    pause.play();
                });
            }
        }, 0, 500, TimeUnit.MILLISECONDS);
    }

    private void showGameScreen() {
        gameStartTime = Instant.now();

        BorderPane root = new BorderPane();
        root.setBackground(createMainBackground());

        HBox top = new HBox(20);
        top.setAlignment(Pos.CENTER);
        top.setPadding(new Insets(10));
        top.setStyle("-fx-background-color: rgba(255,255,255,0.7); -fx-background-radius: 0 0 20 20;");

        timerLabel = new Label("CZAS: 00:00");
        timerLabel.setTextFill(Color.web(BUTTON_TEXT));
        timerLabel.setFont(Font.font("Monospaced", FontWeight.BOLD, 20));

        infoLabel = new Label("OCZEKIWANIE...");
        infoLabel.setTextFill(Color.web(HIT_COLOR));
        infoLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 20));

        top.getChildren().addAll(timerLabel, new Separator(), infoLabel);
        root.setTop(top);

        HBox boards = new HBox(50);
        boards.setAlignment(Pos.CENTER);

        VBox pBoard = new VBox(10, new Label("TWÓJ TEREN"){{setTextFill(Color.web(BUTTON_TEXT));setFont(Font.font("Verdana", 18));}});
        pBoard.setAlignment(Pos.CENTER);
        yourGrid = makeGameGrid(false);
        pBoard.getChildren().add(yourGrid);

        VBox oBoard = new VBox(10, new Label("TEREN WROGA"){{setTextFill(Color.web(HIT_COLOR));setFont(Font.font("Verdana", 18));}});
        oBoard.setAlignment(Pos.CENTER);
        opponentGrid = makeGameGrid(true);
        oBoard.getChildren().add(opponentGrid);

        boards.getChildren().addAll(pBoard, oBoard);
        root.setCenter(boards);

        HBox bottom = new HBox(20);
        bottom.setAlignment(Pos.CENTER);
        bottom.setPadding(new Insets(15));
        bottom.setStyle("-fx-background-color: rgba(255,255,255,0.7); -fx-background-radius: 20 20 0 0;");

        Button volDown = createStyledButton("-", e -> { soundManager.playClick(); soundManager.setVolume(soundManager.getVolume() - 0.1); });
        Button volUp = createStyledButton("+", e -> { soundManager.playClick(); soundManager.setVolume(soundManager.getVolume() + 0.1); });
        volDown.setPrefWidth(50); volUp.setPrefWidth(50);
        Label volLbl = new Label("Głośność");
        volLbl.setTextFill(Color.web(BUTTON_TEXT));

        Button menuBtn = createStyledButton("MENU GŁÓWNE", e -> showMainMenu());

        bottom.getChildren().addAll(volLbl, volDown, volUp, new Region(){{setPrefWidth(50);}}, menuBtn);
        root.setBottom(bottom);

        primaryStage.setScene(new Scene(root, 1100, 800));

        gameLoop = Executors.newSingleThreadScheduledExecutor();
        gameLoop.scheduleWithFixedDelay(this::gameTick, 0, 200, TimeUnit.MILLISECONDS);
    }

    private void gameTick() {
        Platform.runLater(() -> {
            java.time.Duration d = java.time.Duration.between(gameStartTime, Instant.now());
            timerLabel.setText(String.format("%02d:%02d", d.toMinutes(), d.toSecondsPart()));

            if (uiController.getGameController().getGame().getState() == GameState.FINISHED) {
                long secondsPlayed = d.getSeconds();
                boolean iWon = !uiController.getGameController().getGame().getPlayer().getBoard().allSunk();

                Profile p = profileManager.getCurrentProfile();
                if (iWon) p.addWin(secondsPlayed);
                else p.addLoss(secondsPlayed);

                profileManager.saveProfiles();

                showWinScreen();
            } else {
                refreshBoards();
            }
        });
    }

    private GridPane makeGameGrid(boolean isOpponent) {
        GridPane g = new GridPane();
        g.setHgap(2); g.setVgap(2);
        g.setStyle("-fx-background-color: white; -fx-padding: 10; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);");

        for(int r=0;r<10;r++) for(int c=0;c<10;c++) {
            Rectangle rect = new Rectangle(CELL_SIZE, CELL_SIZE);
            rect.setFill(Color.web(WATER_COLOR));
            rect.setStroke(Color.web(GRID_LINES));
            rect.setArcWidth(8); rect.setArcHeight(8);

            StackPane cell = new StackPane(rect);
            int rr=r, cc=c;

            cell.setOnMouseClicked(e -> {
                if (!isOpponent) return;

                if (e.getButton() == MouseButton.PRIMARY) {
                    if (uiController.getGameController().getGame().isPlayerTurn()) {
                        soundManager.playShot();
                        Event ev = uiController.shoot(new Position(rr, cc));
                        infoLabel.setText(ev.message());
                        if(ev.type() == Event.Type.HIT || ev.type() == Event.Type.SUNK || ev.type() == Event.Type.WIN) {
                            soundManager.playHit();
                        } else {
                            soundManager.playMiss();
                        }

                        if(!uiController.getGameController().isNetworked()) {
                            uiController.getGameController().botTurn();
                        }
                    }
                }
                else if (e.getButton() == MouseButton.SECONDARY) {
                    soundManager.playClick();
                    Position p = new Position(rr,cc);
                    if(myFlags.contains(p)) myFlags.remove(p); else myFlags.add(p);
                }
            });

            g.add(cell, c, r);
        }
        return g;
    }

    private void refreshBoards() {
        updateGridContent(yourGrid, uiController.getGameController().getGame().getPlayer().getBoard(), false);
        updateGridContent(opponentGrid, uiController.getGameController().getGame().getOpponent().getBoard(), true);

        boolean myTurn = uiController.getGameController().getGame().isPlayerTurn();
        infoLabel.setText(myTurn ? "TWOJA TURA!" : "CZEKAMY NA WROGA...");
        infoLabel.setTextFill(myTurn ? Color.web(HIT_COLOR) : Color.GRAY);
    }

    private void updateGridContent(GridPane g, Board b, boolean hidden) {
        boolean isOppGrid = (g == opponentGrid);

        for(Node n : g.getChildren()) {
            if (!(n instanceof StackPane sp)) continue;
            Rectangle r = (Rectangle) sp.getChildren().get(0);
            Integer row = GridPane.getRowIndex(sp);
            Integer col = GridPane.getColumnIndex(sp);
            if(row==null) continue;

            Position p = new Position(row, col);
            Cell c = b.getCell(p);

            String currentStateKey = c.getState().name() + "_" + hidden + "_" + (isOppGrid && myFlags.contains(p));
            if (currentStateKey.equals(sp.getUserData())) continue;

            sp.setUserData(currentStateKey);

            if (sp.getChildren().size() > 1) sp.getChildren().remove(1, sp.getChildren().size());

            if (!hidden) {
                if (c.hasShip()) {
                    r.setFill(Color.web(PUSHEEN_GREY));
                    r.setStroke(Color.web(SHIP_BORDER));
                } else {
                    r.setFill(Color.web(WATER_COLOR));
                }

                if (c.getState() == Cell.State.HIT) {
                    r.setFill(Color.web("#ffe6e6"));
                    if (c.getShip() != null && c.getShip().isSunk()) {
                        r.setFill(Color.web(SUNK_COLOR));
                    }
                    addTextSymbol(sp, "X", Color.web(HIT_COLOR));
                } else if (c.getState() == Cell.State.MISS) {
                    addTextSymbol(sp, "●", Color.web(MISS_COLOR));
                }
            } else {
                r.setFill(Color.web(WATER_COLOR));

                if (c.getState() == Cell.State.HIT) {
                    r.setFill(Color.web("#ffe6e6"));
                    addTextSymbol(sp, "X", Color.web(HIT_COLOR));
                } else if (c.getState() == Cell.State.MISS) {
                    addTextSymbol(sp, "●", Color.web(MISS_COLOR));
                }

                if (myFlags.contains(p) && c.getState() == Cell.State.EMPTY) {
                    addTextSymbol(sp, "🐾", Color.web("#ffcc00"));
                }
            }
        }
    }

    private void addTextSymbol(StackPane sp, String s, Color c) {
        Text t = new Text(s);
        t.setFont(Font.font("Arial", FontWeight.BOLD, 26));
        t.setFill(c);
        sp.getChildren().add(t);
    }

    private void showWinScreen() {
        if (gameLoop != null) gameLoop.shutdownNow();

        VBox root = new VBox(30);
        root.setBackground(createMainBackground());
        root.setAlignment(Pos.CENTER);

        boolean iLost = uiController.getGameController().getGame().getPlayer().getBoard().allSunk();

        if (!iLost) soundManager.playWin();
        else soundManager.playLose();

        Label res = new Label(iLost ? "UPS... PRZEGRANA 😿" : "MIAU! WYGRANA! 😸");
        res.setFont(Font.font("Verdana", FontWeight.BOLD, 45));
        res.setTextFill(iLost ? Color.GRAY : Color.web(HIT_COLOR));
        res.setEffect(new DropShadow(5, Color.WHITE));

        Button menu = createStyledButton("WRÓĆ DO MENU", e -> { soundManager.playClick(); showMainMenu(); });

        root.getChildren().addAll(res, menu);
        primaryStage.setScene(new Scene(root, 1100, 750));
    }

    private Button createStyledButton(String text, javafx.event.EventHandler<javafx.event.ActionEvent> action) {
        Button b = new Button(text);
        b.setStyle(BUTTON_STYLE);
        b.setPrefWidth(220);
        b.setPrefHeight(50);

        b.setOnAction(action);
        b.setOnMouseEntered(e -> b.setStyle(BUTTON_HOVER_STYLE));
        b.setOnMouseExited(e -> b.setStyle(BUTTON_STYLE));
        return b;
    }

    public static void main(String[] args) {
        launch();
    }
}