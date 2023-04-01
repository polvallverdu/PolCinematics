package dev.polv.polcinematics.cinematic.manager;

import com.google.gson.JsonObject;
import dev.polv.polcinematics.cinematic.Cinematic;
import dev.polv.polcinematics.exception.InvalidCinematicException;
import dev.polv.polcinematics.exception.NameException;
import dev.polv.polcinematics.net.ServerPacketHandler;
import dev.polv.polcinematics.utils.GsonUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ServerCinematicManager {

    public static class SimpleCinematic {
        private final UUID uuid;
        private final String name;

        public SimpleCinematic(UUID uuid, String name) {
            this.uuid = uuid;
            this.name = name;
        }

        public UUID getUuid() {
            return uuid;
        }

        public String getName() {
            return name;
        }
    }

    private final File cinematicFolder;
    private final List<Cinematic> loadedCinematics;
    private final List<SimpleCinematic> fileCinematicsCache;
    private long lastCacheRefresh;

    private boolean running;

    public ServerCinematicManager() {
        cinematicFolder = new File("cinematics/v0");

        if (!cinematicFolder.exists()) {
            cinematicFolder.mkdirs();
        }

        this.loadedCinematics = new ArrayList<>();
        this.fileCinematicsCache = new ArrayList<>();

        new ServerPacketHandler();
    }

    public List<SimpleCinematic> getFileCinematics() {
        if (System.currentTimeMillis() - lastCacheRefresh > 30000) {
            this.loadCache();
        }
        return new ArrayList<>(fileCinematicsCache);
    }

    public List<Cinematic> getLoadedCinematics() {
        return new ArrayList<>(loadedCinematics);
    }

    private void loadCache() {
        fileCinematicsCache.clear();

        if (cinematicFolder.exists()) {
            for (File file : cinematicFolder.listFiles()) {
                if (file.getName().endsWith(".json")) {
                    try {
                        JsonObject json = GsonUtils.jsonFromFile(file);
                        SimpleCinematic cinematic = new SimpleCinematic(UUID.fromString(json.get("uuid").getAsString()), json.get("name").getAsString());
                        fileCinematicsCache.add(cinematic);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        this.lastCacheRefresh = System.currentTimeMillis();
    }

    public Cinematic createCinematic(String name, long duration) {
        if (this.isNameTaken(name)) {
            throw new NameException("Cinematic name already taken");
        }
        Cinematic cinematic = Cinematic.create(name, duration);
        this.loadedCinematics.add(cinematic);
        return cinematic;
    }

    public Cinematic loadCinematic(String fileName) {
        File cinematicFile = new File(cinematicFolder, fileName);
        if (!cinematicFile.exists()) {
            throw new InvalidCinematicException("Cinematic file does not exist");
        }

        Cinematic loadedCinematic;
        try {
            JsonObject cinematicJson = GsonUtils.jsonFromFile(cinematicFile);
            loadedCinematic = Cinematic.fromJson(cinematicJson);
        } catch (IOException e) {
            throw new InvalidCinematicException("Cinematic file is invalid");
        }

        Cinematic currentlyLoadedCinematic = getCinematic(loadedCinematic.getUuid());
        if (currentlyLoadedCinematic != null) {
            throw new InvalidCinematicException("Cinematic is already loaded");
        }

        this.loadedCinematics.add(loadedCinematic);

        return loadedCinematic;
    }

    public void unloadCinematic(UUID cinematicUUID) {
        Cinematic cinematic = getCinematic(cinematicUUID);
        if (cinematic == null) {
            throw new InvalidCinematicException("Cinematic is not loaded");
        }

        if (this.running) {
            throw new RuntimeException("Cannot unload when a cinematic is running.");
        }

        this.loadedCinematics.remove(cinematic);
    }

    public void saveCinematic(UUID cinematicUUID) {
        Cinematic cinematic = getCinematic(cinematicUUID);
        if (cinematic == null) {
            throw new InvalidCinematicException("Cinematic is not loaded");
        }

        if (this.running) {
            throw new RuntimeException("Cannot save when a cinematic is running.");
        }

        File cinematicFile = new File(cinematicFolder, cinematic.getUuid().toString() + ".json");

        try {
            cinematicFile.createNewFile();
            JsonObject cinematicJson = cinematic.toJson();
            String jsonString = GsonUtils.jsonToString(cinematicJson);
            FileWriter writer = new FileWriter(cinematicFile, false);
            writer.write(jsonString);
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException("Failed to save cinematic");
        }
    }

    public void saveAllCinematics() {
        for (Cinematic cinematic : loadedCinematics) {
            saveCinematic(cinematic.getUuid());
        }
    }

    public Cinematic getCinematic(String name) {
        for (Cinematic cinematic : loadedCinematics) {
            if (cinematic.getName().equalsIgnoreCase(name)) {
                return cinematic;
            }
        }
        return null;
    }

    public Cinematic getCinematic(UUID uuid) {
        for (Cinematic cinematic : loadedCinematics) {
            if (cinematic.getUuid().equals(uuid)) {
                return cinematic;
            }
        }
        return null;
    }

    public boolean isCinematicLoaded(String name) {
        return this.getCinematic(name) != null;
    }

    public boolean isCinematicLoaded(UUID uuid) {
        return this.getCinematic(uuid) != null;
    }

    private SimpleCinematic getSimpleCinematic(UUID uuid) {
        for (SimpleCinematic cinematic : fileCinematicsCache) {
            if (cinematic.getUuid().equals(uuid)) {
                return cinematic;
            }
        }
        return null;
    }

    private SimpleCinematic getSimpleCinematic(String nameOrUUID) {
        for (SimpleCinematic cinematic : fileCinematicsCache) {
            if (cinematic.getName().equalsIgnoreCase(nameOrUUID) ||
                    cinematic.getUuid().toString().replaceAll("-", "").equalsIgnoreCase(nameOrUUID.replace("-", ""))) {
                return cinematic;
            }
        }
        return null;
    }

    public boolean isNameTaken(String name) {
        return this.getSimpleCinematic(name) != null;
    }

    public String[] getCinematicFiles() {
        File[] files = cinematicFolder.listFiles();
        String[] fileNames = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            fileNames[i] = files[i].getName();
        }
        return fileNames;
    }

}
