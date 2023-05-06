package dev.polv.polcinematics.groups;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.architectury.platform.Platform;
import dev.polv.polcinematics.PolCinematics;
import dev.polv.polcinematics.exception.NameException;
import dev.polv.polcinematics.utils.GsonUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Semaphore;

public class GroupManager {

    private final File groupDataFile;

    private final List<PlayerGroup> groups;
    private final static Semaphore savingLock = new Semaphore(1);

    public GroupManager() {
        Path dataPath = Platform.getConfigFolder().resolve("polcinematics/data");
        groupDataFile = new File(dataPath.toFile(), "groups.json");

        if (!groupDataFile.exists()) {
            dataPath.toFile().mkdirs();
            try {
                groupDataFile.createNewFile();
            } catch (IOException e) {
                System.out.println("Failed to create groups.json file");
                e.printStackTrace();
            }
        }

        this.groups = Collections.synchronizedList(new ArrayList<>());

        this.loadFromJson();
    }

    private void loadFromJson() {
        JsonObject json;
        try {
            json = GsonUtils.jsonFromFile(groupDataFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        json.get("groups").getAsJsonArray().forEach(element -> {
            groups.add(PlayerGroup.fromJson(element.getAsJsonObject()));
        });
    }

    public void save() {
        if (!savingLock.tryAcquire()) {
            return;
        }

        PolCinematics.getTaskManager().runAsync((ctx) -> {
            JsonObject json = new JsonObject();
            JsonArray groups = new JsonArray();
            this.groups.stream().map(PlayerGroup::toJson).forEach(groups::add);
            json.add("groups", groups);

            try {
                GsonUtils.jsonToFile(json, groupDataFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                savingLock.release();
            }
        }).start();
    }

    public PlayerGroup createGroup(String name, String selector) throws NameException {
        for (PlayerGroup group : this.getGroups()) {
            if (group.getName().equalsIgnoreCase(name.toLowerCase())) throw new NameException(name);
        }

        PlayerGroup group = PlayerGroup.create(name, selector);
        groups.add(group);
        save();
        return group;
    }

    public void deleteGroup(PlayerGroup group) {
        groups.remove(group);
        save();
    }

    public PlayerGroup getGroup(String name) {
        return groups.stream().filter(group -> group.getName().equals(name)).findFirst().orElse(null);
    }

    public PlayerGroup getGroup(UUID uuid) {
        return groups.stream().filter(group -> group.getUuid().equals(uuid)).findFirst().orElse(null);
    }

    public PlayerGroup resolveGroup(String nameOrUUID) {
        PlayerGroup group = getGroup(nameOrUUID);
        if (group == null) {
            try {
                group = getGroup(UUID.fromString(nameOrUUID));
            } catch (IllegalArgumentException ignored) {
            }
        }
        return group;
    }

    public List<PlayerGroup> getGroups() {
        return new ArrayList<>(groups);
    }

}
