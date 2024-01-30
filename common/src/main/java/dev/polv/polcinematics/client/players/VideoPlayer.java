package dev.polv.polcinematics.client.players;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.polv.polcinematics.PolCinematics;
import dev.polv.polcinematics.utils.render.RenderUtils;
import dev.polv.vlcvideo.api.mediaPlayer.OptimizedMediaPlayer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.*;
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
    private OptimizedMediaPlayer player;

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
        MediaPlayerHandler.getInstance().registerPlayerOnFreeResLoc(this.playerResourceLocation, OptimizedMediaPlayer.class);
        this.player = (OptimizedMediaPlayer) MediaPlayerHandler.getInstance().getMediaPlayer(this.playerResourceLocation);
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

    public OptimizedMediaPlayer getPlayer() {
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

        RenderSystem.disableDepthTest();
        RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);
        RenderSystem.setShaderTexture(0, this.player.getRenderer().getTextureID());
        Tessellator t = Tessellator.getInstance();
        BufferBuilder buffer = t.getBuffer();
        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        buffer.vertex(x, height + y, 0).texture(0.0f, 1.0f).color(255, 255, 255, 255).next();
        buffer.vertex(width + x, height + y, 0).texture(1.0f, 1.0f).color(255, 255, 255, 255).next();
        buffer.vertex(width + x, y, 0).texture(1.0f, 0.0f).color(255, 255, 255, 255).next();
        buffer.vertex(x, y, 0).texture(0.0f, 0.0f).color(255, 255, 255, 255).next();
        t.draw();

        RenderSystem.setShaderTexture(0, 0);
        RenderSystem.enableDepthTest();
    }
}