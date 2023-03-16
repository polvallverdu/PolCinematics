package dev.polv.polcinematics.net;

import dev.polv.polcinematics.client.PolCinematicsClient;
import dev.polv.polcinematics.fluttergui.FlutterGuiManager;
import dev.polv.polcinematics.utils.GsonUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.nio.charset.StandardCharsets;

@Environment(EnvType.CLIENT)
public class ClientPacketHandler {

    public ClientPacketHandler() {
        ClientPlayNetworking.registerGlobalReceiver(Packets.CINEMATIC_BROADCAST_PACKET, (client, handler, buf, responseSender) -> {
            String json = new String(buf.readByteArray(), StandardCharsets.UTF_8);
            PolCinematicsClient.getCCM().loadCinematic(GsonUtils.jsonFromString(json));
        });
        ClientPlayNetworking.registerGlobalReceiver(Packets.CLIENT_CINEMATIC_PLAY_PACKET, (client, handler, buf, responseSender) -> {
            PolCinematicsClient.getCCM().start();
        });
        ClientPlayNetworking.registerGlobalReceiver(Packets.CLIENT_CINEMATIC_STOP_PACKET, (client, handler, buf, responseSender) -> {
            PolCinematicsClient.getCCM().stop();
        });
        ClientPlayNetworking.registerGlobalReceiver(Packets.CLIENT_EDITOR_OPEN, (client, handler, buf, responseSender) -> {
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
        });
    }

}
