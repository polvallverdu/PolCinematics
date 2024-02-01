package dev.polv.polcinematics.cinematic.manager;

import com.google.gson.JsonObject;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.platform.Platform;
import dev.polv.polcinematics.PolCinematics;
import dev.polv.polcinematics.cinematic.Timeline;
import dev.polv.polcinematics.exception.AlreadyLoadedCinematicException;
import dev.polv.polcinematics.exception.InvalidCinematicException;
import dev.polv.polcinematics.exception.NameException;
import dev.polv.polcinematics.net.Packets;
import dev.polv.polcinematics.net.ServerPacketHandler;
import dev.polv.polcinematics.utils.GsonUtils;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.function.Consumer;

public class ServerCinematicManager {

    public enum ECinematicState {
        LOADING,
        LOADED,
        SAVING,
        UNLOADING,
    }

    private final File cinematicFolder;
    private final List<Timeline> loadedTimelines;
    private final HashMap<UUID, ECinematicState> loadedCinematicState;
    private List<FileCinematic> fileCinematicsCache;
    private long lastCacheRefresh;

    private final ConcurrentHashMap<UUID, Timeline> selectedCinematics;
    private final List<UUID> broadcastedCinematics;
    private final Semaphore fileCinematicsLoadLock = new Semaphore(1);

    public ServerCinematicManager() {
        Path cinematicsPath = Platform.getConfigFolder().resolve("polcinematics/cinematics/v" + PolCinematics.MOD_VERSION);
        cinematicFolder = cinematicsPath.toFile();

        if (!cinematicFolder.exists()) {
            cinematicFolder.mkdirs();
        }

        this.loadedTimelines = new ArrayList<>();
        this.loadedCinematicState = new HashMap<>();
        this.fileCinematicsCache = new ArrayList<>();

        this.selectedCinematics = new ConcurrentHashMap<>();
        this.broadcastedCinematics = new ArrayList<>();

        LifecycleEvent.SERVER_STOPPING.register(server -> {
            new ArrayList<>(this.loadedTimelines).forEach(this::unloadCinematic);
            this.broadcastedCinematics.clear();
        });
        PlayerEvent.PLAYER_JOIN.register(player -> {
            this.broadcastedCinematics.forEach((uuid) -> {
                Timeline timeline = this.getCinematic(uuid);
                if (timeline != null) {
                    Packets.broadcastCinematic(timeline, List.of(player));
                }
            });
        });
        PlayerEvent.PLAYER_QUIT.register(player -> {
            this.selectedCinematics.remove(player.getUuid());
        });

        PolCinematics.getTaskManager().runAsync((ctx) -> {
            this.loadCache();
        }).repeat(Duration.ZERO, Duration.ofSeconds(15));

        new ServerPacketHandler();
    }

    public void loadCache() {
        if (!fileCinematicsLoadLock.tryAcquire()) return;
        List<FileCinematic> fileCinematics = new ArrayList<>();

        if (cinematicFolder.exists()) {
            for (File file : cinematicFolder.listFiles()) {
                if (file.getName().endsWith(".json")) {
                    try {
                        JsonObject json = GsonUtils.jsonFromFile(file);
                        FileCinematic cinematic = new FileCinematic(UUID.fromString(json.get("uuid").getAsString()), json.get("name").getAsString());
                        fileCinematics.add(cinematic);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        this.fileCinematicsCache = fileCinematics;
        this.fileCinematicsLoadLock.release();
    }

    /**
     * Creates a {@link Timeline} and adds it to the loaded cinematics.
     *
     * @param name Name of the cinematic
     * @param duration Duration of the cinematic in milliseconds
     * @return The created cinematic
     * @throws NameException If the name is already taken
     */
    public Timeline createCinematic(String name, long duration) {
        if (this.isNameTaken(name)) {
            throw new NameException("Cinematic name already taken");
        }
        Timeline timeline = Timeline.create(name, duration);
        this.loadedTimelines.add(timeline);
        this.loadedCinematicState.put(timeline.getUuid(), ECinematicState.LOADED);
        this.saveCinematic(timeline, null);
        return timeline;
    }

    /**
     * Loads a cinematic from file
     *
     * @param fileName Name of the file
     * @param failedCallback Callback to run when the cinematic fails to load
     * @param doneCallback Callback to run when the cinematic is loaded
     */
    public void loadCinematic(String fileName, @Nullable Consumer<Exception> failedCallback, @Nullable Consumer<Timeline> doneCallback) throws InvalidCinematicException {
        File cinematicFile = new File(cinematicFolder, fileName);
        if (!cinematicFile.exists()) {
            throw new InvalidCinematicException("Cinematic file does not exist");
        }

        PolCinematics.getTaskManager().runAsync((ctx) -> {
            Timeline loadedTimeline;
            try {
                JsonObject cinematicJson = GsonUtils.jsonFromFile(cinematicFile);
                UUID cinematicUUID = UUID.fromString(cinematicJson.get("uuid").getAsString());
                ECinematicState imaginaryState = this.loadedCinematicState.get(cinematicUUID);
                if (imaginaryState != null) {
                    PolCinematics.getTaskManager().run((cc) -> {
                        if (failedCallback != null) {
                            failedCallback.accept(new AlreadyLoadedCinematicException(imaginaryState == ECinematicState.LOADING ? "Cinematic is being loaded already" : "Cinematic is already loaded"));
                        }
                    }).start();
                    return;
                }
                this.loadedCinematicState.put(cinematicUUID, ECinematicState.LOADING);

                loadedTimeline = Timeline.fromJson(cinematicJson);
            } catch (Exception e) {
                PolCinematics.getTaskManager().run((cc) -> {
                    if (failedCallback != null) {
                        failedCallback.accept(new InvalidCinematicException("Cinematic file is invalid"));
                    }
                }).start();
                e.printStackTrace();
                return;
            }

            this.loadedTimelines.add(loadedTimeline);
            this.loadedCinematicState.put(loadedTimeline.getUuid(), ECinematicState.LOADED);

            PolCinematics.getTaskManager().run((cc) -> {
                if (doneCallback != null) {
                    doneCallback.accept(loadedTimeline);
                }
            }).start();
        }).start();
    }

    /**
     * Unload a loaded cinematic
     *
     * @param timeline The {@link Timeline} to unload
     */
    public void unloadCinematic(Timeline timeline) {
        this.loadedCinematicState.put(timeline.getUuid(), ECinematicState.UNLOADING);

        this.loadedTimelines.remove(timeline);
        Packets.unbroadcastCinematic(timeline.getUuid(), PolCinematics.SERVER.getPlayerManager().getPlayerList());
        this.broadcastedCinematics.remove(timeline.getUuid());

        this.selectedCinematics.entrySet().removeIf(entry -> entry.getValue().equals(timeline));
        this.loadedCinematicState.remove(timeline.getUuid());
    }

    /**
     * Save a loaded cinematic.
     *
     * @param timeline The {@link Timeline} to save
     * @param doneCallback Callback to run when the cinematic is saved
     */
    public void saveCinematic(Timeline timeline, @Nullable Runnable doneCallback) {
        if (this.loadedCinematicState.get(timeline.getUuid()) != ECinematicState.LOADED) return;
        this.loadedCinematicState.put(timeline.getUuid(), ECinematicState.SAVING);
        File cinematicFile = new File(cinematicFolder, timeline.getUuid().toString() + ".json");

        PolCinematics.getTaskManager().runAsync((ctx) -> {
            try {
                cinematicFile.createNewFile();
                JsonObject cinematicJson = timeline.toJson();
                GsonUtils.jsonToFile(cinematicJson, cinematicFile);
            } catch (IOException e) {
                throw new RuntimeException("Failed to save cinematic");
            } catch (Exception e) {
                e.printStackTrace();
            }

            this.loadedCinematicState.put(timeline.getUuid(), ECinematicState.LOADED);

            PolCinematics.getTaskManager().run((cc) -> {
                if (doneCallback != null) {
                    doneCallback.run();
                }
            }).start();
        }).start();
    }

    /**
     * Save all loaded cinematics
     */
    public void saveAllCinematics() {
        for (Timeline timeline : loadedTimelines) {
            saveCinematic(timeline, null);
        }
    }

    /**
     * Get a loaded {@link Timeline} by name
     *
     * @param name The name of the cinematic
     * @return The cinematic, or null if not found
     */
    public @Nullable Timeline getCinematic(String name) {
        for (Timeline timeline : loadedTimelines) {
            if (timeline.getName().equalsIgnoreCase(name)) {
                return timeline;
            }
        }
        return null;
    }

    /**
     * Get a loaded {@link Timeline} by {@link UUID}
     *
     * @param uuid The {@link UUID} of the cinematic
     * @return The cinematic, or null if not found
     */
    public @Nullable Timeline getCinematic(UUID uuid) {
        for (Timeline timeline : loadedTimelines) {
            if (timeline.getUuid().equals(uuid)) {
                return timeline;
            }
        }
        return null;
    }

    /**
     * Get a loaded {@link Timeline} by name or {@link UUID}
     *
     * @param nameOrUuid The name or {@link UUID} of the cinematic
     * @return The cinematic, or null if not found
     */
    public @Nullable Timeline resolveCinematic(String nameOrUuid) {
        Timeline timeline = getCinematic(nameOrUuid);
        if (timeline == null) {
            try {
                timeline = getCinematic(UUID.fromString(nameOrUuid));
            } catch (IllegalArgumentException ignored) {
            }
        }
        return timeline;
    }

    /**
     * @return A list of all loaded {@link Timeline}
     */
    public List<Timeline> getLoadedCinematics() {
        return new ArrayList<>(loadedTimelines);
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
     * Get a {@link FileCinematic} by {@link UUID}
     * <br>
     * A {@link FileCinematic} is a cinematic that is not loaded into memory, but exists in the cinematics folder.
     *
     * @param uuid The {@link UUID} of the cinematic
     * @return The cinematic, or null if not found
     */
    public @Nullable FileCinematic getFileCinematic(UUID uuid) {
        for (FileCinematic cinematic : fileCinematicsCache) {
            if (cinematic.uuid().equals(uuid)) {
                return cinematic;
            }
        }
        return null;
    }

    /**
     * Get a {@link FileCinematic} by name or {@link UUID#toString()}
     * <br>
     * A {@link FileCinematic} is a cinematic that is not loaded into memory, but exists in the cinematics folder.
     *
     * @param cinematicName The name of the cinematic or it's UUID strigified.
     * @return The {@link FileCinematic}, or null if not found
     */
    public @Nullable FileCinematic getFileCinematic(String cinematicName) {
        for (FileCinematic cinematic : fileCinematicsCache) {
            if (cinematic.name().equalsIgnoreCase(cinematicName)) {
                return cinematic;
            }
        }
        return null;
    }

    /**
     * Get a {@link FileCinematic} by name or {@link UUID}
     * <br>
     * A {@link FileCinematic} is a cinematic that is not loaded into memory, but exists in the cinematics folder.
     * @param nameOrUUID The name of the cinematic or it's UUID strigified.
     * @return The {@link FileCinematic}, or null if not found
     */
    public @Nullable FileCinematic resolveFileCinematic(String nameOrUUID) {
        FileCinematic cinematic = getFileCinematic(nameOrUUID);
        if (cinematic == null) {
            try {
                cinematic = getFileCinematic(UUID.fromString(nameOrUUID));
            } catch (IllegalArgumentException ignored) {
            }
        }
        return cinematic;
    }

    /**
     * @return list of all {@link FileCinematic}
     */
    public List<FileCinematic> getFileCinematics() {
        return new ArrayList<>(fileCinematicsCache);
    }

    /**
     * Checks if a name for a cinematic is taken. It is case insensitive.
     *
     * @param name The name to check
     * @return true if the name is taken, false otherwise
     */
    public boolean isNameTaken(String name) {
        return this.getFileCinematic(name) != null;
    }

    public boolean isCinematicBroadcasted(Timeline timeline) {
        return this.broadcastedCinematics.contains(timeline.getUuid());
    }

    public void addBroadcastedCinematic(Timeline timeline) {
        this.broadcastedCinematics.add(timeline.getUuid());
    }

    public void removeBroadcastedCinematic(Timeline timeline) {
        this.broadcastedCinematics.remove(timeline.getUuid());
    }

    public void selectCinematic(ServerPlayerEntity player, Timeline timeline) {
        this.selectedCinematics.put(player.getUuid(), timeline);
    }

    public @Nullable Timeline getSelectedCinematic(ServerPlayerEntity player) {
        return this.selectedCinematics.get(player.getUuid());
    }

}
