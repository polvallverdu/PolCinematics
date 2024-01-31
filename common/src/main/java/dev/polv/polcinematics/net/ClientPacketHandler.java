package dev.polv.polcinematics.net;

import dev.architectury.event.events.client.ClientGuiEvent;
import dev.architectury.networking.NetworkManager;
import dev.polv.polcinematics.PolCinematics;
import dev.polv.polcinematics.client.PolCinematicsClient;
import dev.polv.polcinematics.utils.GsonUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Environment(EnvType.CLIENT)
public class ClientPacketHandler {

    public ClientPacketHandler() {
        NetworkManager.registerReceiver(
                NetworkManager.s2c(),
                Packets.CINEMATIC_BROADCAST_PACKET,
                (buf, context) -> {
                    String json = new String(buf.readByteArray(), StandardCharsets.UTF_8);

                    PolCinematics.getTaskManager().run(cc -> {
                        PolCinematicsClient.getCCM().loadCinematic(GsonUtils.jsonFromString(json));
                    }).start();
                }
        );

        NetworkManager.registerReceiver(
                NetworkManager.s2c(),
                Packets.CINEMATIC_UNBROADCAST_PACKET,
                (buf, context) -> {
                    UUID cinematicUuid = buf.readUuid();
                    PolCinematics.getTaskManager().run(cc -> {
                        PolCinematicsClient.getCCM().unloadCinematic(cinematicUuid);
                    }).start();
                }
        );

        NetworkManager.registerReceiver(
                NetworkManager.s2c(),
                Packets.CLIENT_CINEMATIC_PLAY_PACKET,
                (buf, context) -> {
                    UUID cinematicUuid = buf.readUuid();
                    boolean paused = buf.readBoolean();
                    long from = buf.readLong();

                    PolCinematics.getTaskManager().run(cc -> {
                        PolCinematicsClient.getCCM().start(cinematicUuid, from, paused);
                    }).start();
                }
        );

        NetworkManager.registerReceiver(
                NetworkManager.s2c(),
                Packets.CLIENT_CINEMATIC_GOTO_PACKET,
                (buf, context) -> {
                    UUID cinematicUuid = buf.readUuid();
                    long to = buf.readLong();

                    PolCinematics.getTaskManager().run(cc -> {
                        PolCinematicsClient.getCCM().moveTo(cinematicUuid, to);
                    }).start();
                }
        );

        NetworkManager.registerReceiver(
                NetworkManager.s2c(),
                Packets.CLIENT_CINEMATIC_PAUSE_PACKET,
                (buf, context) -> {
                    UUID cinematicUuid = buf.readUuid();

                    PolCinematics.getTaskManager().run(cc -> {
                        PolCinematicsClient.getCCM().pause(cinematicUuid);
                    }).start();
                }
        );

        NetworkManager.registerReceiver(
                NetworkManager.s2c(),
                Packets.CLIENT_CINEMATIC_RESUME_PACKET,
                (buf, context) -> {
                    UUID cinematicUuid = buf.readUuid();

                    PolCinematics.getTaskManager().run(cc -> {
                        PolCinematicsClient.getCCM().resume(cinematicUuid);
                    }).start();
                }
        );

        NetworkManager.registerReceiver(
                NetworkManager.s2c(),
                Packets.CLIENT_CINEMATIC_STOP_PACKET,
                (buf, context) -> {
                    UUID cinematicUuid = buf.readUuid();

                    PolCinematics.getTaskManager().run(cc -> {
                        PolCinematicsClient.getCCM().stop(cinematicUuid);
                    }).start();
                }
        );

    }

}
