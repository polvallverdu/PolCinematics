package dev.polv.polcinematics.client.players;

public class DummyPlayer implements IMediaPlayer{

    public DummyPlayer(Object... args) {

    }

    @Override
    public void play() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void setVolume(float volume) {

    }

    @Override
    public float getVolumeFloat() {
        return 0;
    }

    @Override
    public int getVolume() {
        return 0;
    }

    @Override
    public void setTime(long newTime) {

    }
}
