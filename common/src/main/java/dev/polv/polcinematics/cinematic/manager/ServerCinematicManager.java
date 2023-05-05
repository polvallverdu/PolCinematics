package dev.polv.polcinematics.cinematic.manager;

import com.google.gson.JsonObject;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.platform.Platform;
import dev.polv.polcinematics.PolCinematics;
import dev.polv.polcinematics.cinematic.Cinematic;
import dev.polv.polcinematics.exception.AlreadyLoadedCinematicException;
import dev.polv.polcinematics.exception.InvalidCinematicException;
import dev.polv.polcinematics.exception.NameException;
import dev.polv.polcinematics.net.ServerPacketHandler;
import dev.polv.polcinematics.utils.GsonUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ServerCinematicManager {

    private final File cinematicFolder;
    private final List<Cinematic> loadedCinematics;
    private final List<SimpleCinematic> fileCinematicsCache;
    private long lastCacheRefresh;

    private boolean running;

    public ServerCinematicManager() {
        Path cinematicsPath = Platform.getConfigFolder().resolve("polcinematics/cinematics/v" + PolCinematics.MOD_VERSION);
        cinematicFolder = cinematicsPath.toFile();

        if (!cinematicFolder.exists()) {
            cinematicFolder.mkdirs();
        }

        this.loadedCinematics = new ArrayList<>();
        this.fileCinematicsCache = new ArrayList<>();

        LifecycleEvent.SERVER_STOPPING.register(server -> {
            this.running = false;
            new ArrayList<>(this.loadedCinematics).forEach((c) -> unloadCinematic(c.getUuid()));
        });
        PlayerEvent.PLAYER_JOIN.register(player -> {
            // TODO: SEND CINEMATICS TO PLAYER
        });

        new ServerPacketHandler();
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

    /**
     * Creates a {@link Cinematic} and adds it to the loaded cinematics.
     *
     * @param name Name of the cinematic
     * @param duration Duration of the cinematic in milliseconds
     * @return The created cinematic
     * @throws NameException If the name is already taken
     */
    public Cinematic createCinematic(String name, long duration) {
        if (this.isNameTaken(name)) {
            throw new NameException("Cinematic name already taken");
        }
        Cinematic cinematic = Cinematic.create(name, duration);
        this.loadedCinematics.add(cinematic);
        this.saveCinematic(cinematic.getUuid());
        return cinematic;
    }

    /**
     * Loads a cinematic from file
     *
     * @param fileName Name of the file
     * @return The loaded cinematic
     */
    public Cinematic loadCinematic(String fileName) throws InvalidCinematicException, AlreadyLoadedCinematicException {
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
            throw new AlreadyLoadedCinematicException("Cinematic is already loaded");
        }

        this.loadedCinematics.add(loadedCinematic);

        return loadedCinematic;
    }

    /**
     * Unload a loaded cinematic
     *
     * @param cinematicUUID The UUID of the cinematic
     */
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

    /**
     * Save a loaded cinematic.
     *
     * @param cinematicUUID The UUID of the cinematic
     */
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

    /**
     * Save all loaded cinematics
     */
    public void saveAllCinematics() {
        for (Cinematic cinematic : loadedCinematics) {
            saveCinematic(cinematic.getUuid());
        }
    }

    /**
     * Get a loaded {@link Cinematic} by name
     *
     * @param name The name of the cinematic
     * @return The cinematic, or null if not found
     */
    public Cinematic getCinematic(String name) {
        for (Cinematic cinematic : loadedCinematics) {
            if (cinematic.getName().equalsIgnoreCase(name)) {
                return cinematic;
            }
        }
        return null;
    }

    /**
     * Get a loaded {@link Cinematic} by {@link UUID}
     *
     * @param uuid The {@link UUID} of the cinematic
     * @return The cinematic, or null if not found
     */
    public Cinematic getCinematic(UUID uuid) {
        for (Cinematic cinematic : loadedCinematics) {
            if (cinematic.getUuid().equals(uuid)) {
                return cinematic;
            }
        }
        return null;
    }

    /**
     * @return A list of all loaded {@link Cinematic}
     */
    public List<Cinematic> getLoadedCinematics() {
        return new ArrayList<>(loadedCinematics);
    }

    /**
     * Check if a cinematic is loaded by name
     *
     * @return true if the cinematic is loaded, false otherwise
     */
    public boolean isCinematicLoaded(String name) {
        return this.getCinematic(name) != null;
    }

    /**
     * Check if a cinematic is loaded by {@link UUID}
     *
     * @return true if the cinematic is loaded, false otherwise
     */
    public boolean isCinematicLoaded(UUID uuid) {
        return this.getCinematic(uuid) != null;
    }

    /**
     * Get a {@link SimpleCinematic} by {@link UUID}
     * <p>
     * A {@link SimpleCinematic} is a cinematic that is not loaded into memory, but exists in the cinematics folder.
     *
     * @param uuid The {@link UUID} of the cinematic
     * @return The cinematic, or null if not found
     */
    public SimpleCinematic getSimpleCinematic(UUID uuid) {
        for (SimpleCinematic cinematic : fileCinematicsCache) {
            if (cinematic.uuid().equals(uuid)) {
                return cinematic;
            }
        }
        return null;
    }

    /**
     * Get a {@link SimpleCinematic} by name or {@link UUID#toString()}
     * <p>
     * A {@link SimpleCinematic} is a cinematic that is not loaded into memory, but exists in the cinematics folder.
     *
     * @param nameOrUUID The name of the cinematic or it's UUID strigified.
     * @return
     */
    public SimpleCinematic getSimpleCinematic(String nameOrUUID) {
        for (SimpleCinematic cinematic : fileCinematicsCache) {
            if (cinematic.name().equalsIgnoreCase(nameOrUUID) ||
                    cinematic.uuid().toString().replaceAll("-", "").equalsIgnoreCase(nameOrUUID.replace("-", ""))) {
                return cinematic;
            }
        }
        return null;
    }

    /**
     * @return list of all {@link SimpleCinematic}
     */
    public List<SimpleCinematic> getSimpleCinematics() {
        if (System.currentTimeMillis() - lastCacheRefresh > 30000) {
            this.loadCache();
        }
        return new ArrayList<>(fileCinematicsCache);
    }

    /**
     * Checks if a name for a cinematic is taken. It is case insensitive.
     *
     * @param name The name to check
     * @return true if the name is taken, false otherwise
     */
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
