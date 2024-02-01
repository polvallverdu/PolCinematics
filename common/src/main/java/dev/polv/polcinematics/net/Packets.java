package dev.polv.polcinematics.net;

import dev.architectury.networking.NetworkManager;
import dev.polv.polcinematics.cinematic.Timeline;
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
    public static Identifier CINEMATIC_UNBROADCAST_PACKET = generatePacketId("cinematic_unbroadcast");
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

    private static Identifier generatePacketId(String name) {
        return new Identifier("polcinematics", name);
    }

    public static void broadcastCinematic(Timeline timeline, List<ServerPlayerEntity> players) {
        PacketByteBuf buf = NetworkUtils.createBuffer();
        buf.writeByteArray(timeline.toJson().toString().getBytes());
        NetworkManager.sendToPlayers(players, CINEMATIC_BROADCAST_PACKET, buf);
    }

    @Environment(EnvType.CLIENT)
    public static void sendCinematicReady(UUID cinematicUuid) {
        PacketByteBuf buf = NetworkUtils.createBuffer();
        buf.writeUuid(cinematicUuid);
        NetworkManager.sendToServer(CLIENT_READY_CINEMATIC_PACKET, buf);
    }

    public static void unbroadcastCinematic(UUID cinematicUuid, List<ServerPlayerEntity> players) {
        PacketByteBuf buf = NetworkUtils.createBuffer();
        buf.writeUuid(cinematicUuid);
        NetworkManager.sendToPlayers(players, CINEMATIC_UNBROADCAST_PACKET, buf);
    }

    public static void sendCinematicPlay(List<ServerPlayerEntity> players, UUID cinematicUuid, boolean paused, long from) {
        PacketByteBuf buf = NetworkUtils.createBuffer();
        buf.writeUuid(cinematicUuid);
        buf.writeBoolean(paused);
        buf.writeLong(from);
        NetworkManager.sendToPlayers(players, CLIENT_CINEMATIC_PLAY_PACKET, buf);
    }

    public static void sendCinematicPause(List<ServerPlayerEntity> players, UUID cinematicUuid) {
        PacketByteBuf buf = NetworkUtils.createBuffer();
        buf.writeUuid(cinematicUuid);
        NetworkManager.sendToPlayers(players, CLIENT_CINEMATIC_PAUSE_PACKET, buf);
    }

    public static void sendCinematicResume(List<ServerPlayerEntity> players, UUID cinematicUuid) {
        PacketByteBuf buf = NetworkUtils.createBuffer();
        buf.writeUuid(cinematicUuid);
        NetworkManager.sendToPlayers(players, CLIENT_CINEMATIC_RESUME_PACKET, buf);
    }

    public static void sendCinematicStop(List<ServerPlayerEntity> players, UUID cinematicUuid) {
        PacketByteBuf buf = NetworkUtils.createBuffer();
        buf.writeUuid(cinematicUuid);
        NetworkManager.sendToPlayers(players, CLIENT_CINEMATIC_STOP_PACKET, buf);
    }

    public static void sendCinematicGoto(List<ServerPlayerEntity> players, UUID cinematicUuid, long to) {
        PacketByteBuf buf = NetworkUtils.createBuffer();
        buf.writeUuid(cinematicUuid);
        buf.writeLong(to);
        NetworkManager.sendToPlayers(players, CLIENT_CINEMATIC_GOTO_PACKET, buf);
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
