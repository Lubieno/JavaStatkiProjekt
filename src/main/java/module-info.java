module com.battleship {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.media;
    requires java.desktop;

    exports com.battleship;
    exports com.battleship.board;
    exports com.battleship.controller;
    exports com.battleship.game;
    exports com.battleship.network;
    exports com.battleship.player;
    exports com.battleship.ui;
    exports com.battleship.util;
    exports com.battleship.data;

    opens com.battleship.ui to javafx.fxml;
}