package dev.polv.polcinematics.commands.groups;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.EntitySelectorReader;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;
import java.util.UUID;

public class PlayerGroup {

    private final UUID uuid;
    private final String name;
    private String selector;

    private PlayerGroup(UUID uuid, String name, String selector) {
        this.uuid = uuid;
        this.name = name;
        this.selector = selector;
    }

    protected static PlayerGroup create(String name, String selector) {
        return new PlayerGroup(UUID.randomUUID(), name, selector);
    }

    public List<ServerPlayerEntity> getPlayers(ServerCommandSource source) {
        EntitySelector selector;
        try {
            selector = new EntitySelectorReader(new StringReader(this.selector)).read();
        } catch (CommandSyntaxException e) {
            throw new IllegalArgumentException("Invalid selector");
        }

        try {
            return selector.getPlayers(source);
        } catch (CommandSyntaxException e) {
            throw new IllegalArgumentException("Invalid selector");
        }
    }

    public static PlayerGroup fromJson(JsonObject json) {
        return new PlayerGroup(UUID.fromString(json.get("uuid").getAsString()), json.get("name").getAsString(), json.get("selector").getAsString());
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("uuid", uuid.toString());
        json.addProperty("name", name);
        json.addProperty("selector", selector);
        return json;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public String getSelector() {
        return selector;
    }

    public void setSelector(String selector) {
        this.selector = selector;
    }
}
