package dev.polv.polcinematics.net;

import dev.architectury.event.events.client.ClientGuiEvent;
import dev.architectury.networking.NetworkManager;
import dev.polv.polcinematics.client.PolCinematicsClient;
import dev.polv.polcinematics.client.players.AudioPlayer;
import dev.polv.polcinematics.client.players.IMediaPlayer;
import dev.polv.polcinematics.client.players.VideoPlayer;
import dev.polv.polcinematics.utils.GsonUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Environment(EnvType.CLIENT)
public class ClientPacketHandler {

    private IMediaPlayer mediaPlayer = null;

    public ClientPacketHandler() {
        ClientGuiEvent.RENDER_HUD.register((matrices, tickDelta) -> {
            if (this.mediaPlayer != null && this.mediaPlayer instanceof VideoPlayer && ((VideoPlayer) this.mediaPlayer).isPlaying()) {
                ((VideoPlayer) this.mediaPlayer).render(matrices, 0, 0, MinecraftClient.getInstance().getWindow().getScaledWidth(), MinecraftClient.getInstance().getWindow().getScaledHeight(), 1f);
            }
        });

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
                Packets.CINEMATIC_UNBROADCAST_PACKET,
                (buf, context) -> {
                    UUID cinematicUuid = buf.readUuid();
                    PolCinematicsClient.getCCM().unloadCinematic(cinematicUuid);
                }
        );

        NetworkManager.registerReceiver(
                NetworkManager.s2c(),
                Packets.CLIENT_CINEMATIC_PLAY_PACKET,
                (buf, context) -> {
                    UUID cinematicUuid = buf.readUuid();
                    boolean paused = buf.readBoolean();
                    long from = buf.readLong();

                    PolCinematicsClient.getCCM().start(cinematicUuid, from, paused);
                }
        );

        NetworkManager.registerReceiver(
                NetworkManager.s2c(),
                Packets.CLIENT_CINEMATIC_GOTO_PACKET,
                (buf, context) -> {
                    UUID cinematicUuid = buf.readUuid();
                    long to = buf.readLong();

                    PolCinematicsClient.getCCM().moveTo(cinematicUuid, to);
                }
        );

        NetworkManager.registerReceiver(
                NetworkManager.s2c(),
                Packets.CLIENT_CINEMATIC_PAUSE_PACKET,
                (buf, context) -> {
                    UUID cinematicUuid = buf.readUuid();

                    PolCinematicsClient.getCCM().pause(cinematicUuid);
                }
        );

        NetworkManager.registerReceiver(
                NetworkManager.s2c(),
                Packets.CLIENT_CINEMATIC_RESUME_PACKET,
                (buf, context) -> {
                    UUID cinematicUuid = buf.readUuid();

                    PolCinematicsClient.getCCM().resume(cinematicUuid);
                }
        );

        NetworkManager.registerReceiver(
                NetworkManager.s2c(),
                Packets.CLIENT_CINEMATIC_STOP_PACKET,
                (buf, context) -> {
                    UUID cinematicUuid = buf.readUuid();

                    PolCinematicsClient.getCCM().stop(cinematicUuid);
                }
        );

        //////////////////// MEDIA PLAYER ////////////////////

        NetworkManager.registerReceiver(
                NetworkManager.s2c(),
                Packets.MEDIAPLAYER_CREATE,
                (buf, context) -> {
                    String url = buf.readString();
                    boolean paused = buf.readBoolean();
                    boolean audio = buf.readBoolean();

                    if (this.mediaPlayer != null) {
                        this.mediaPlayer.stop();
                    }
                    this.mediaPlayer = null;

                    //this.mediaPlayer = audio ? new AudioPlayer(url) : new VideoPlayer(url);
                    MinecraftClient.getInstance().executeSync(() -> {
                        this.mediaPlayer = IMediaPlayer.createPlayer(audio ? AudioPlayer.class : VideoPlayer.class, url);
                        if (!paused) {
                            this.mediaPlayer.play();
                        }
                    });
                }
        );

        NetworkManager.registerReceiver(
                NetworkManager.s2c(),
                Packets.MEDIAPLAYER_RESUME,
                (buf, context) -> {
                    if (this.mediaPlayer != null) {
                        this.mediaPlayer.resume();
                    }
                }
        );

        NetworkManager.registerReceiver(
                NetworkManager.s2c(),
                Packets.MEDIAPLAYER_PAUSE,
                (buf, context) -> {
                    if (this.mediaPlayer != null) {
                        this.mediaPlayer.pause();
                    }
                }
        );

        NetworkManager.registerReceiver(
                NetworkManager.s2c(),
                Packets.MEDIAPLAYER_STOP,
                (buf, context) -> {
                    if (this.mediaPlayer != null) {
                        this.mediaPlayer.stop();
                        this.mediaPlayer = null;
                    }
                }
        );

        NetworkManager.registerReceiver(
                NetworkManager.s2c(),
                Packets.MEDIAPLAYER_SET_TIME,
                (buf, context) -> {
                    if (this.mediaPlayer != null) {
                        this.mediaPlayer.setTime(buf.readLong());
                    }
                }
        );

        NetworkManager.registerReceiver(
                NetworkManager.s2c(),
                Packets.MEDIAPLAYER_SET_VOLUME,
                (buf, context) -> {
                    if (this.mediaPlayer != null) {
                        this.mediaPlayer.setVolume(buf.readFloat());
                    }
                }
        );

        //////////////////////////////////////////////////////
    }

}
