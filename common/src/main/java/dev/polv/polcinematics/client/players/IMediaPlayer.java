package dev.polv.polcinematics.client.players;

import dev.architectury.platform.Platform;
import dev.polv.polcinematics.client.ClientModules;
import dev.polv.polcinematics.exception.MissingModuleException;

public interface IMediaPlayer {

    static IMediaPlayer createPlayer(Class<? extends IMediaPlayer> mediaPlayer, String mediaPath) {
        if (!Platform.isModLoaded("fancyvideo_api")) {
            new MissingModuleException(ClientModules.MEDIA_PLAYER).printStackTrace();
            return new DummyPlayer();
        }

        try {
            return (IMediaPlayer) mediaPlayer.getConstructor(String.class).newInstance(mediaPath);
        } catch (Exception e) {
            e.printStackTrace();
            return new DummyPlayer();
        }
    }

    void play();
    void pause();
    void resume();
    void stop();

    /**
     * @param volume volume as an int between 0 and 100
     */
    default void setVolume(int volume) {
        this.setVolume((float) volume / 100.0F);
    }

    /**
     * @param volume volume as a float between 0.0 and 1.0
     */
    void setVolume(float volume);

    /**
     * @return volume as a float between 0.0 and 1.0
     */
    float getVolumeFloat();

    /**
     * @return volume as an int between 0 and 100
     */
    int getVolume();

    /**
     * @param newTime time in milliseconds
     */
    void setTime(long newTime);


}
