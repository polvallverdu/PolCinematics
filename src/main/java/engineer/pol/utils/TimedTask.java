package engineer.pol.utils;

import java.time.Duration;

public class TimedTask {

    private Duration duration;
    private long finishTime;
    private boolean loop;
    private boolean running;
    private boolean reverse;

    public TimedTask(Duration duration, boolean loop) {
        this.duration = duration;
        this.setFinishTime();
        this.loop = loop;
        this.running = false;
    }

    void setFinishTime() {
        System.out.println("Current time: " + System.currentTimeMillis() + " Duration: " + duration.toMillis() + " Finish time: " + (System.currentTimeMillis() + duration.toMillis()));
        this.finishTime = System.currentTimeMillis() + duration.toMillis();
    }

    public boolean isFinished() {
        return System.currentTimeMillis() >= finishTime;
    }

    public boolean isRunning() {
        return running;
    }

    private void start() {
        this.running = true;
        this.setFinishTime();
    }

    public void startReverse() {
        this.reverse = true;
        this.start();
    }

    public void startNormal() {
        this.reverse = false;
        this.start();
    }

    public void stop() {
        this.running = false;
    }

    public long getTick() {
        long currentTime = System.currentTimeMillis();
        long tick = (finishTime - currentTime);
        if (tick < 0) {
            tick = 0;
            if (loop && this.running) {
                this.setFinishTime();
            } else {
                stop();
            }
        }

        return reverse ? (duration.toMillis() - tick) : tick;
    }

    public void setReverse(boolean reverse) {
        this.reverse = reverse;
    }

    public double getCurveRelative() {
        return this.getTick() / (double) duration.toMillis();
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
        if (this.running) {
            this.setFinishTime();
        }
    }

    public Duration getDuration() {
        return duration;
    }

    public long getFinishTime() {
        return finishTime;
    }

    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    public boolean isLoop() {
        return loop;
    }

    public boolean isReverse() {
        return reverse;
    }
}
