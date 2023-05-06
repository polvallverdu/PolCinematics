package dev.polv.polcinematics.net;

import dev.architectury.networking.NetworkManager;

public class ServerPacketHandler {

    public ServerPacketHandler() {
        NetworkManager.registerReceiver(NetworkManager.c2s(), Packets.CLIENT_READY_CINEMATIC_PACKET, (buf, context) -> {
            //PolCinematics.CINEMATICS_MANAGER.onPlayerReady(context.getPlayer());         // TODO: Implement player ready check
        });
    }

}
