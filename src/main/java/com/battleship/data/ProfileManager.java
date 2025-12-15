package com.battleship.data;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ProfileManager {
    private static final String FILE_NAME = "profiles.dat";
    private List<Profile> profiles;
    private Profile currentProfile;

    public ProfileManager() {
        this.profiles = loadProfiles();
        if (this.profiles.isEmpty()) {
            String[] catNames = {"Puszin", "Kłaczek", "Mruczek", "Luna", "Simba", "Filemon"};

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
            return new ArrayList<>();
        }
    }
}