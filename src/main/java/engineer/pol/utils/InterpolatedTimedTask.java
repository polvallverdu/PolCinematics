package engineer.pol.utils;

import java.time.Duration;

public class InterpolatedTimedTask extends TimedTask {

    private LegacySplineInterpolator interpolator;

    public InterpolatedTimedTask(Duration duration, boolean loop) {
        super(duration, loop);
        this.interpolator = new LegacySplineInterpolator();
    }

    public void setInterpolator(LegacySplineInterpolator interpolator) {
        this.interpolator = interpolator;
    }

    public LegacySplineInterpolator getInterpolator() {
        return interpolator;
    }

    @Override
    public double getCurveRelative() {
        double fraction = super.getCurveRelative();
        // get interpolated value from 0-1
        return this.getInterpolator().interpolate(fraction);
    }
}
