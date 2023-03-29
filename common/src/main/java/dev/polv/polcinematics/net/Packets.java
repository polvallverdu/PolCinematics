package dev.polv.polcinematics.net;

import dev.architectury.networking.NetworkManager;
import dev.polv.polcinematics.cinematic.Cinematic;
import dev.polv.polcinematics.fluttergui.FlutterGuiManager;
import dev.polv.polcinematics.utils.NetworkUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.UUID;

public class Packets {

    public static Identifier CINEMATIC_BROADCAST_PACKET = generatePacketId("cinematic_broadcast");
    public static Identifier CLIENT_READY_CINEMATIC_PACKET = generatePacketId("cinematic_ready");


    public static Identifier CLIENT_CINEMATIC_PLAY_PACKET = generatePacketId("cinematic_play");
    public static Identifier CLIENT_CINEMATIC_STOP_PACKET = generatePacketId("cinematic_stop");
    public static Identifier CLIENT_CINEMATIC_PAUSE_PACKET = generatePacketId("cinematic_pause");
    public static Identifier CLIENT_CINEMATIC_RESUME_PACKET = generatePacketId("cinematic_resume");
    public static Identifier CLIENT_CINEMATIC_GOTO_PACKET = generatePacketId("cinematic_goto");


    public static Identifier MEDIAPLAYER_CREATE = generatePacketId("mediaplayer_create");
    public static Identifier MEDIAPLAYER_PAUSE = generatePacketId("mediaplayer_pause");
    public static Identifier MEDIAPLAYER_RESUME = generatePacketId("mediaplayer_resume");
    public static Identifier MEDIAPLAYER_STOP = generatePacketId("mediaplayer_stop");
    public static Identifier MEDIAPLAYER_SET_VOLUME = generatePacketId("mediaplayer_set_volume");
    public static Identifier MEDIAPLAYER_SET_TIME = generatePacketId("mediaplayer_set_time");


    public static Identifier CLIENT_EDITOR_OPEN = generatePacketId("editor_open");

    private static Identifier generatePacketId(String name) {
        return new Identifier("polcinematics", name);
    }

    public static void broadcastCinematic(Cinematic cinematic, List<ServerPlayerEntity> players) {
        PacketByteBuf buf = NetworkUtils.createBuffer();
        buf.writeByteArray(cinematic.toJson().toString().getBytes());
        NetworkManager.sendToPlayers(players, CINEMATIC_BROADCAST_PACKET, buf);
    }

    @Environment(EnvType.CLIENT)
    public static void sendCinematicReady() {
        NetworkManager.sendToServer(CLIENT_READY_CINEMATIC_PACKET, NetworkUtils.EMPTY_BUFFER);
    }

    public static void sendCinematicPlay(List<ServerPlayerEntity> players, boolean paused, long from) {
        PacketByteBuf buf = NetworkUtils.createBuffer();
        buf.writeBoolean(paused);
        buf.writeLong(from);
        NetworkManager.sendToPlayers(players, CLIENT_CINEMATIC_PLAY_PACKET, buf);
    }

    public static void sendCinematicPause(List<ServerPlayerEntity> players) {
        NetworkManager.sendToPlayers(players, CLIENT_CINEMATIC_PAUSE_PACKET, NetworkUtils.EMPTY_BUFFER);
    }

    public static void sendCinematicResume(List<ServerPlayerEntity> players) {
        NetworkManager.sendToPlayers(players, CLIENT_CINEMATIC_RESUME_PACKET, NetworkUtils.EMPTY_BUFFER);
    }

    public static void sendCinematicStop(List<ServerPlayerEntity> players) {
        NetworkManager.sendToPlayers(players, CLIENT_CINEMATIC_STOP_PACKET, NetworkUtils.EMPTY_BUFFER);
    }

    public static void sendCinematicGoto(List<ServerPlayerEntity> players, long to) {
        PacketByteBuf buf = NetworkUtils.createBuffer();
        buf.writeLong(to);
        NetworkManager.sendToPlayers(players, CLIENT_CINEMATIC_GOTO_PACKET, buf);
    }

    public static void sendOpenServer(ServerPlayerEntity player) {
        PacketByteBuf buf = NetworkUtils.createBuffer();
        buf.writeInt(FlutterGuiManager.INSTANCE.getPort());
        String password = UUID.randomUUID().toString().replaceAll("-", "");
        FlutterGuiManager.INSTANCE.playerPasswords.put(password, player.getUuid());
        buf.writeString(password);
        NetworkManager.sendToPlayer(player, CLIENT_EDITOR_OPEN, buf);
    }

    public static void sendMediaPlayerCreate(List<ServerPlayerEntity> players, String url, boolean paused, boolean audioOnly) {
        PacketByteBuf buf = NetworkUtils.createBuffer();
        buf.writeString(url);
        buf.writeBoolean(paused);
        buf.writeBoolean(audioOnly);
        NetworkManager.sendToPlayers(players, MEDIAPLAYER_CREATE, buf);
    }

    public static void sendMediaPlayerState(List<ServerPlayerEntity> players, boolean state) {
        PacketByteBuf buf = NetworkUtils.createBuffer();
        NetworkManager.sendToPlayers(players, state ? MEDIAPLAYER_RESUME : MEDIAPLAYER_PAUSE, buf);
    }

    public static void sendMediaPlayerStop(List<ServerPlayerEntity> players) {
        NetworkManager.sendToPlayers(players, MEDIAPLAYER_STOP, NetworkUtils.EMPTY_BUFFER);
    }

    public static void sendMediaPlayerSetVolume(List<ServerPlayerEntity> players, float volume) {
        PacketByteBuf buf = NetworkUtils.createBuffer();
        buf.writeFloat(volume);
        NetworkManager.sendToPlayers(players, MEDIAPLAYER_SET_VOLUME, buf);
    }

    public static void sendMediaPlayerSetTime(List<ServerPlayerEntity> players, long time) {
        PacketByteBuf buf = NetworkUtils.createBuffer();
        buf.writeLong(time);
        NetworkManager.sendToPlayers(players, MEDIAPLAYER_SET_TIME, buf);
    }

}
