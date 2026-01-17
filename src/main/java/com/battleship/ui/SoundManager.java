package com.battleship.ui;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.net.URL;

/**
 * Menedżer zasobów audio wykorzystujący JavaFX Media API.
 * Obsługuje odtwarzanie efektów dźwiękowych (AudioClip) o niskim opóźnieniu
 * oraz muzyki w tle (MediaPlayer) w pętli.
 */
public class SoundManager {
    private double volume = 0.5;
    private MediaPlayer musicPlayer;

    private AudioClip clickSound;
    private AudioClip shotSound;
    private AudioClip hitSound;
    private AudioClip missSound;
    private AudioClip winSound;
    private AudioClip loseSound;

    public SoundManager() {
        clickSound = loadClip("click.wav");
        shotSound = loadClip("shot.wav");
        hitSound = loadClip("hit.wav");
        missSound = loadClip("miss.wav");
        winSound = loadClip("win.mp3");
        loseSound = loadClip("lose.mp3");

        initMusic();
    }

    private void initMusic() {
        try {
            URL res = getClass().getResource("/music.mp3");
            if (res != null) {
                Media media = new Media(res.toExternalForm());
                musicPlayer = new MediaPlayer(media);
                musicPlayer.setCycleCount(MediaPlayer.INDEFINITE);
                musicPlayer.setVolume(volume * 0.5);
                musicPlayer.play();
            }
        } catch (Exception e) {
            System.out.println("Brak pliku music.mp3");
        }
    }

    /**
     * Ładuje klip dźwiękowy z zasobów aplikacji.
     * @param name Nazwa pliku w katalogu resources.
     */
    private AudioClip loadClip(String name) {
        try {
            URL res = getClass().getResource("/" + name);
            if (res != null) return new AudioClip(res.toExternalForm());
        } catch (Exception e) {
            System.out.println("Nie znaleziono dzwieku: " + name);
        }
        return null;
    }

    public void setVolume(double v) {
        this.volume = Math.max(0.0, Math.min(1.0, v));
        if (musicPlayer != null) musicPlayer.setVolume(volume * 0.5);
    }

    public double getVolume() { return volume; }

    private void play(AudioClip clip) {
        if (clip != null) clip.play(volume);
    }

    public void playClick() { play(clickSound); }
    public void playShot() { play(shotSound); }
    public void playHit() { play(hitSound); }
    public void playMiss() { play(missSound); }
    public void playWin() {
        if(musicPlayer != null) musicPlayer.pause();
        play(winSound);
    }
    public void playLose() {
        if(musicPlayer != null) musicPlayer.pause();
        play(loseSound);
    }

    public void resumeMusic() {
        if(musicPlayer != null) musicPlayer.play();
    }
}