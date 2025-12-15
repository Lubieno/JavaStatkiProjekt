package com.battleship.data;

import java.io.Serializable;

public class Profile implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private int gamesPlayed;
    private int wins;
    private int losses;
    private int shipsSunk;
    private long totalTimeSeconds;

    public Profile(String name) {
        this.name = name;
    }

    public String getName() { return name; }
    public int getGamesPlayed() { return gamesPlayed; }
    public int getWins() { return wins; }
    public int getLosses() { return losses; }
    public int getShipsSunk() { return shipsSunk; }
    public long getTotalTimeSeconds() { return totalTimeSeconds; }

    public void addWin(long timePlayed) {
        this.gamesPlayed++;
        this.wins++;
        this.totalTimeSeconds += timePlayed;
    }

    public void addLoss(long timePlayed) {
        this.gamesPlayed++;
        this.losses++;
        this.totalTimeSeconds += timePlayed;
    }

    public void addSunkShip() {
        this.shipsSunk++;
    }

    public String getWinRate() {
        if (gamesPlayed == 0) return "0%";
        int percent = (int)((double)wins / gamesPlayed * 100);
        return percent + "%";
    }

    public String getFormattedTime() {
        long hours = totalTimeSeconds / 3600;
        long minutes = (totalTimeSeconds % 3600) / 60;
        return String.format("%dh %dm", hours, minutes);
    }
}
