package engineer.pol.client.players;

import com.mojang.blaze3d.systems.RenderSystem;
import engineer.pol.PolCinematics;
import engineer.pol.cinematic.compositions.overlay.DEPRECATEDOBERLAY;
import engineer.pol.cinematic.compositions.overlay.EOverlayType;
import engineer.pol.utils.render.RenderUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import nick1st.fancyvideo.api.DynamicResourceLocation;
import nick1st.fancyvideo.api.MediaPlayerHandler;
import nick1st.fancyvideo.api.mediaPlayer.SimpleMediaPlayer;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

public class DEPRECATEDVIDEOPLAYER extends DEPRECATEDOBERLAY {

    private String mediaPath;
    private SimpleMediaPlayer player;

    private DynamicResourceLocation playerResourceLocation;
    private Identifier lastFrame;

    private boolean playing = false;
    private float volume = 1f;

    public DEPRECATEDVIDEOPLAYER(String mediaPath) {
        super(EOverlayType.VIDEO_OVERLAY);
        this.mediaPath = mediaPath;
        this.playerResourceLocation = new DynamicResourceLocation(PolCinematics.MODID, "video/" + this.mediaPath.hashCode());

        MediaPlayerHandler.getInstance().registerPlayerOnFreeResLoc(this.playerResourceLocation, SimpleMediaPlayer.class);
        this.player = (SimpleMediaPlayer) MediaPlayerHandler.getInstance().getMediaPlayer(this.playerResourceLocation);
        this.player.api().media().prepare(this.mediaPath);
        this.player.api().audio().setVolume(this.getVolume());
    }

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

    public void restart() {
        this.setTime(0L);
    }

    public void play() {
        if (this.playing) return;
        this.playing = true;
        this.player.api().controls().play();
    }

    public void pause() {
        if (!this.playing) return;
        this.playing = false;
        this.player.api().controls().pause();
    }

    public int getVolume() {
        return (int) ((this.volume * this.volume) * 100);
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

    public void stop() {
        if (this.player != null) {
            this.playing = false;
            this.player.api().controls().stop();
        }
    }

    @Override
    public void render(MatrixStack matrix, int x, int y, int width, int height, double alpha, long time) {
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
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        DrawableHelper.drawTexture(matrix, x, y, 0, 0, 0, width, height, width, height);
        RenderSystem.disableBlend();
    }
}
