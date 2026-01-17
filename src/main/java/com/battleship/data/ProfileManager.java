package com.battleship.data;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Warstwa trwałości danych (Persistence Layer).
 * Zarządza cyklem życia plików danych, w tym odczytem i zapisem.
 * Implementuje strategię "fail-safe" przy odczycie uszkodzonych plików:
 * w przypadku błędu deserializacji tworzy kopię zapasową (.bak) i inicjalizuje
 * pusty stan, zamiast crashować aplikację.
 */
public class ProfileManager {
    private static final String FILE_NAME = "profiles.dat";
    private List<Profile> profiles;
    private Profile currentProfile;

    public ProfileManager() {
        this.profiles = loadProfiles();
        if (this.profiles.isEmpty()) {
            String[] catNames = {"Puszin", "Stormy", "Pip", "Tommy", "Simba", "Filemon"};

            for (String name : catNames) {
                this.profiles.add(new Profile(name));
            }
            saveProfiles();
        }
    }

    public List<Profile> getProfiles() { return profiles; }

    public void setCurrentProfile(Profile p) { this.currentProfile = p; }
    public Profile getCurrentProfile() { return currentProfile; }

    public void saveProfiles() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(profiles);
        } catch (IOException e) {
            System.err.println("Błąd zapisu profili: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private List<Profile> loadProfiles() {
        File f = new File(FILE_NAME);
        if (!f.exists()) return new ArrayList<>();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            return (List<Profile>) ois.readObject();
        } catch (Exception e) {
            System.err.println("Błąd odczytu profili (plik uszkodzony?): " + e.getMessage());

            // Mechanizm Backup: Zmiana nazwy uszkodzonego pliku
            File backup = new File(FILE_NAME + ".bak");
            if (f.renameTo(backup)) {
                System.out.println("Utworzono kopię zapasową uszkodzonego profilu: " + backup.getName());
            }

            return new ArrayList<>();
        }
    }
}