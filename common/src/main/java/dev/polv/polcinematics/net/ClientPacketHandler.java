package dev.polv.polcinematics.net;

import dev.architectury.networking.NetworkManager;
import dev.polv.polcinematics.client.PolCinematicsClient;
import dev.polv.polcinematics.fluttergui.FlutterGuiManager;
import dev.polv.polcinematics.utils.GsonUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.nio.charset.StandardCharsets;

@Environment(EnvType.CLIENT)
public class ClientPacketHandler {

    public ClientPacketHandler() {
        NetworkManager.registerReceiver(
                NetworkManager.s2c(),
                Packets.CINEMATIC_BROADCAST_PACKET,
                (buf, context) -> {
                    String json = new String(buf.readByteArray(), StandardCharsets.UTF_8);
                    PolCinematicsClient.getCCM().loadCinematic(GsonUtils.jsonFromString(json));
                }
        );

        NetworkManager.registerReceiver(
                NetworkManager.s2c(),
                Packets.CLIENT_CINEMATIC_PLAY_PACKET,
                (buf, context) -> {
                    boolean paused = buf.readBoolean();
                    long from = buf.readLong();
                    if (from != 0) {
                        PolCinematicsClient.getCCM().startFrom(from, paused);
                    } else {
                        PolCinematicsClient.getCCM().start(paused);
                    }
                }
        );

        NetworkManager.registerReceiver(
                NetworkManager.s2c(),
                Packets.CLIENT_CINEMATIC_GOTO_PACKET,
                (buf, context) -> PolCinematicsClient.getCCM().moveTo(buf.readLong())
        );

        NetworkManager.registerReceiver(
                NetworkManager.s2c(),
                Packets.CLIENT_CINEMATIC_PAUSE_PACKET,
                (buf, context) -> PolCinematicsClient.getCCM().pause()
        );

        NetworkManager.registerReceiver(
                NetworkManager.s2c(),
                Packets.CLIENT_CINEMATIC_RESUME_PACKET,
                (buf, context) -> PolCinematicsClient.getCCM().resume()
        );

        NetworkManager.registerReceiver(
                NetworkManager.s2c(),
                Packets.CLIENT_CINEMATIC_STOP_PACKET,
                (buf, context) -> PolCinematicsClient.getCCM().stop()
        );

        NetworkManager.registerReceiver(
                NetworkManager.s2c(),
                Packets.CLIENT_EDITOR_OPEN,
                (buf, context) -> {
                    MinecraftClient.getInstance().player.sendMessage(Text.of("Opening editor..."));
                    try {
                        int port = buf.readInt();
                        String password = buf.readString();
                        String serverAddress = MinecraftClient.getInstance().getCurrentServerEntry().address;
                        FlutterGuiManager.executeProgram("ws://" + serverAddress + ":" + port + "/", password);
                    } catch (Exception e) {
                        e.printStackTrace();
                        MinecraftClient.getInstance().player.sendMessage(Text.of("There was an error opening the editor."));
                    }
                }
        );
    }

}
