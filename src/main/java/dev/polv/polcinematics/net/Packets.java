package dev.polv.polcinematics.net;

import dev.polv.polcinematics.fluttergui.FlutterGuiManager;
import dev.polv.polcinematics.cinematic.Cinematic;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
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



    public static Identifier CLIENT_EDITOR_OPEN = generatePacketId("editor_open");

    private static Identifier generatePacketId(String name) {
        return new Identifier("polcinematics", name);
    }

    public static void broadcastCinematic(Cinematic cinematic, List<ServerPlayerEntity> players) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeByteArray(cinematic.toJson().toString().getBytes());

        players.forEach(p -> {
            ServerPlayNetworking.send(p, CINEMATIC_BROADCAST_PACKET, buf);
        });
    }

    @Environment(EnvType.CLIENT)
    public static void sendCinematicReady() {
        ClientPlayNetworking.send(CLIENT_READY_CINEMATIC_PACKET, PacketByteBufs.empty());
    }

    public static void sendCinematicPlay(List<ServerPlayerEntity> players) {
        players.forEach(p -> {
            ServerPlayNetworking.send(p, CLIENT_CINEMATIC_PLAY_PACKET, PacketByteBufs.empty());
        });
    }

    public static void sendCinematicStop(List<ServerPlayerEntity> players) {
        players.forEach(p -> {
            ServerPlayNetworking.send(p, CLIENT_CINEMATIC_STOP_PACKET, PacketByteBufs.empty());
        });
    }

    public static void sendOpenServer(ServerPlayerEntity player) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(FlutterGuiManager.INSTANCE.getPort());
        String password = UUID.randomUUID().toString().replaceAll("-", "");
        FlutterGuiManager.INSTANCE.playerPasswords.put(password, player.getUuid());
        buf.writeString(password);
        ServerPlayNetworking.send(player, CLIENT_EDITOR_OPEN, buf);
    }

}
