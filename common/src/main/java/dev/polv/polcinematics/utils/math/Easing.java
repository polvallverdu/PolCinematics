package dev.polv.polcinematics.utils.math;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class Easing {

    private Function<Double, Double> callable;
    private String name;

    public Easing(Function<Double, Double> callable) {
        this.callable = callable;
        this.name = "custom";
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    // Extracted from https://easings.net/

    public static Easing EASE_IN_SINE = new Easing(t -> 1 - Math.cos((t * Math.PI) / 2));
    public static Easing EASE_OUT_SINE = new Easing(t -> Math.sin((t * Math.PI) / 2));
    public static Easing EASE_INOUT_SINE = new Easing(t -> -(Math.cos(Math.PI * t) - 1) / 2);

    public static Easing EASE_IN_QUAD = new Easing(t -> t*t);
    public static Easing EASE_OUT_QUAD = new Easing(t -> 1 - (1 - t) * (1 - t));
    public static Easing EASE_INOUT_QUAD = new Easing(t -> t < 0.5 ? 2 * t * t : 1 - Math.pow(-2 * t + 2, 2) / 2);

    public static Easing EASE_IN_CUBIC = new Easing(t -> t*t*t);
    public static Easing EASE_OUT_CUBIC = new Easing(t -> 1 - Math.pow(1 - t, 3));
    public static Easing EASE_INOUT_CUBIC = new Easing(t -> t < 0.5 ? 4 * t * t * t : 1 - Math.pow(-2 * t + 2, 3) / 2);

    public static Easing EASE_IN_QUART = new Easing(t -> t*t*t*t);
    public static Easing EASE_OUT_QUART = new Easing(t -> 1 - Math.pow(1 - t, 4));
    public static Easing EASE_INOUT_QUART = new Easing(t -> t < 0.5 ? 8 * t * t * t * t : 1 - Math.pow(-2 * t + 2, 4) / 2);

    public static Easing EASE_IN_QUINT = new Easing(t -> t*t*t*t*t);
    public static Easing EASE_OUT_QUINT = new Easing(t -> 1 - Math.pow(1 - t, 5));
    public static Easing EASE_INOUT_QUINT = new Easing(t -> t < 0.5 ? 16 * t * t * t * t * t : 1 - Math.pow(-2 * t + 2, 5) / 2);

    public static Easing EASE_IN_EXPO = new Easing(t -> Math.pow(2, 10 * (t - 1)));
    public static Easing EASE_OUT_EXPO = new Easing(t -> 1 - Math.pow(2, -10 * t));
    public static Easing EASE_INOUT_EXPO = new Easing(t -> t < 0.5 ? Math.pow(2, 20 * t - 10) / 2 : 1 - Math.pow(2, -20 * t + 10) / 2);

    public static Easing EASE_IN_CIRC = new Easing(t -> 1 - Math.sqrt(1 - t * t));
    public static Easing EASE_OUT_CIRC = new Easing(t -> Math.sqrt(1 - (t - 1) * (t - 1)));
    public static Easing EASE_INOUT_CIRC = new Easing(t -> t < 0.5 ? (1 - Math.sqrt(1 - 2 * t * t)) / 2 : (Math.sqrt(1 - 2 * (t - 1) * (t - 1)) + 1) / 2);

    public static Easing LINEAR = new Easing(t -> t);
    public static Easing INSTANT = new Easing(t -> t == 1.0d ? 1d : 0d);
    private static final HashMap<String, Easing> values;

    static {
        values = new HashMap<>();

        values.put("EASE_IN_SINE", EASE_IN_SINE);
        values.put("EASE_OUT_SINE", EASE_OUT_SINE);
        values.put("EASE_INOUT_SINE", EASE_INOUT_SINE);

        values.put("EASE_IN_QUAD", EASE_IN_QUAD);
        values.put("EASE_OUT_QUAD", EASE_OUT_QUAD);
        values.put("EASE_INOUT_QUAD", EASE_INOUT_QUAD);

        values.put("EASE_IN_CUBIC", EASE_IN_CUBIC);
        values.put("EASE_OUT_CUBIC", EASE_OUT_CUBIC);
        values.put("EASE_INOUT_CUBIC", EASE_INOUT_CUBIC);

        values.put("EASE_IN_QUART", EASE_IN_QUART);
        values.put("EASE_OUT_QUART", EASE_OUT_QUART);
        values.put("EASE_INOUT_QUART", EASE_INOUT_QUART);

        values.put("EASE_IN_QUINT", EASE_IN_QUINT);
        values.put("EASE_OUT_QUINT", EASE_OUT_QUINT);
        values.put("EASE_INOUT_QUINT", EASE_INOUT_QUINT);

        values.put("EASE_IN_EXPO", EASE_IN_EXPO);
        values.put("EASE_OUT_EXPO", EASE_OUT_EXPO);
        values.put("EASE_INOUT_EXPO", EASE_INOUT_EXPO);

        values.put("EASE_IN_CIRC", EASE_IN_CIRC);
        values.put("EASE_OUT_CIRC", EASE_OUT_CIRC);
        values.put("EASE_INOUT_CIRC", EASE_INOUT_CIRC);

        values.put("LINEAR", LINEAR);
        values.put("INSTANT", INSTANT);

        values.forEach((name, easing) -> easing.setName(name));
    }

    public static HashMap<String, Easing> getValues() {
        return values;
    }

    public static String getName(Easing easing) {
        for (Map.Entry<String, Easing> entry : values.entrySet()) {
            if (entry.getValue() == easing) {
                return entry.getKey();
            }
        }
        return "";
    }

    /**
     * @param t 0-1
     * @return 0-1
     */
    public double getValue(double t) {
        return callable.apply(t);
    }

    /**
     * @param name Name of the {@link Easing}
     * @return The {@link Easing} or null if not found
     */
    public static @Nullable Easing fromName(String name) {
        return values.get(name);
    }

}
