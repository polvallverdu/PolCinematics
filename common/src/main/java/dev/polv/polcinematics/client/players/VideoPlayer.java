package dev.polv.polcinematics.client.players;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.polv.polcinematics.PolCinematics;
import dev.polv.polcinematics.utils.render.RenderUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import dev.polv.vlcvideo.api.DynamicResourceLocation;
import dev.polv.vlcvideo.api.MediaPlayerHandler;
import dev.polv.vlcvideo.api.mediaPlayer.SimpleMediaPlayer;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

@Environment(EnvType.CLIENT)
public class VideoPlayer implements IMediaPlayer {

    private String mediaPath;
    private SimpleMediaPlayer player;

    private DynamicResourceLocation playerResourceLocation;
    private Identifier lastFrame;

    private boolean playing = false;
    private float volume = 1f;

    public VideoPlayer(String mediaPath) {
        this.changeMediaPath(mediaPath);
    }

    public void changeMediaPath(String newPath) {
        this.mediaPath = newPath;
        this.playerResourceLocation = new DynamicResourceLocation(PolCinematics.MOD_ID, "video/" + this.mediaPath.hashCode());
        MediaPlayerHandler.getInstance().registerPlayerOnFreeResLoc(this.playerResourceLocation, SimpleMediaPlayer.class);
        this.player = (SimpleMediaPlayer) MediaPlayerHandler.getInstance().getMediaPlayer(this.playerResourceLocation);
        this.player.api().media().prepare(this.mediaPath);
        this.player.api().audio().setVolume(this.getVolume());
    }

    @Override
    public void setTime(long time) {
        if (this.player != null) {
            this.player.api().controls().setTime(time);
        }
    }

    public boolean canPlay() {
        try {
            Dimension d = this.getVideoDimension();
            if (d != null) {
                return true;
            }
        } catch (Exception e) {}
        return false;
    }

    @Nullable
    public Dimension getVideoDimension() {
        if (this.player != null) {
            return this.player.api().video().videoDimension();
        }
        return null;
    }

    public SimpleMediaPlayer getPlayer() {
        return this.player;
    }

    @Override
    public void play() {
        if (this.playing) return;
        this.playing = true;
        this.player.api().controls().play();
    }

    @Override
    public void pause() {
        if (!this.playing) return;
        this.player.api().controls().pause();
    }

    @Override
    public void resume() {
        if (!this.playing) return;
        this.player.api().controls().play();
    }

    @Override
    public int getVolume() {
        return (int) (this.getVolumeFloat() * 100);
    }

    @Override
    public void setVolume(float volume) {
        this.volume = volume;
        this.player.api().audio().setVolume(this.getVolume());
    }

    @Override
    public float getVolumeFloat() {
        return (this.volume * this.volume);
    }

    public void setLooping(boolean b) {
        if (this.player != null) {
            this.player.api().controls().setRepeat(b);
        }
    }

    public boolean isLooping() {
        if (this.player != null) {
            return this.player.api().controls().getRepeat();
        }
        return false;
    }

    public boolean isPlaying() {
        return this.playing;
    }

    @Override
    public void stop() {
        if (this.player != null) {
            this.playing = false;
            this.player.api().controls().stop();
            // MediaPlayerHandler.getInstance().flagPlayerRemoval(this.playerResourceLocation); TODO: DEBUG WHY DOES THIS CREATE A CALLSTACK ERROR WITH PRE EVENT.
        }
    }

    public void render(MatrixStack matrix, int x, int y, int width, int height, float alpha) {
        if (!this.isPlaying() || this.player == null) return;

        this.lastFrame = this.player.renderToResourceLocation();

        // Get the video dimension
        Dimension d = this.getVideoDimension();
        if (d == null) return;
        int videoWidth = d.width;
        int videoHeight = d.height;

        // Get minecraft window dimension
        int windowWidth = MinecraftClient.getInstance().getWindow().getScaledWidth();
        int windowHeight = MinecraftClient.getInstance().getWindow().getScaledHeight();

        //RenderUtils.renderBlackScreen(matrix, 1);
        RenderUtils.bindTexture(this.lastFrame);
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
        DrawableHelper.drawTexture(matrix, x, y, 0, 0, 0, width, height, width, height);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();
    }
}