package com.battleship.controller;

import com.battleship.board.Board;
import com.battleship.board.Position;
import com.battleship.network.Message;
import com.battleship.network.NetworkManager;
import com.battleship.ui.UI;
import com.battleship.util.GameLogger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GameController {

    private final UI ui;
    private final NetworkManager net;

    private final Board board = new Board();
    private final Board enemy = new Board();

    private boolean myTurn = false;

    public GameController(UI ui, NetworkManager net) {
        this.ui = ui;
        this.net = net;
        net.setHandler(this::onNetworkMessage);
    }

    public void start() throws IOException {
        ui.showMainMenu();
    }

    public void host(int port) throws IOException {
        net.startServer(port);
        myTurn = true;
        GameLogger.log("Hosting game");
    }

    public void connect(String host, int port) throws IOException {
        net.connectTo(host, port);
        myTurn = false;
        GameLogger.log("Connected to host");
    }

    public void sendShot(Position p) throws IOException {
        Map<String, Object> data = new HashMap<>();
        data.put("row", p.row);
        data.put("col", p.col);

        net.send(new Message("SHOT", data));
    }

    private void onNetworkMessage(Message m) {
        GameLogger.log("Received message: " + m.type);

        switch (m.type) {
            case "SHOT":
                int r = (int) m.data.get("row");
                int c = (int) m.data.get("col");
                Board.ShotResult res = board.shoot(new Position(r, c));

                Map<String, Object> reply = new HashMap<>();
                reply.put("row", r);
                reply.put("col", c);
                reply.put("result", res.name());

                try {
                    net.send(new Message("SHOT_RESULT", reply));
                } catch (IOException e) {
                    GameLogger.log(e.getMessage());
                }

                if (board.allSunk())
                    GameLogger.log("All my ships sunk");

                break;

            case "SHOT_RESULT":
                int rr = (int) m.data.get("row");
                int cc = (int) m.data.get("col");
                String result = (String) m.data.get("result");

                GameLogger.log("Shot at (" + rr + "," + cc + ") => " + result);

                if (result.equals("MISS"))
                    myTurn = true;

                break;
        }
    }

    public Board getBoard() {
        return board;
    }
}
