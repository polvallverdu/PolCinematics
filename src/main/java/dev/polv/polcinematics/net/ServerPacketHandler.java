package dev.polv.polcinematics.net;

import dev.polv.polcinematics.PolCinematics;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class ServerPacketHandler {

    public ServerPacketHandler() {
        ServerPlayNetworking.registerGlobalReceiver(Packets.CLIENT_READY_CINEMATIC_PACKET, (server, player, handler, buf, responseSender) -> {
            PolCinematics.CINEMATICS_MANAGER.onPlayerReady(player);
        });
    }

}
