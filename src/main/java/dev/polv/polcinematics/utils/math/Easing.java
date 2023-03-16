package dev.polv.polcinematics.utils.math;

import java.util.HashMap;
import java.util.function.Function;

public class Easing {

    private Function<Double, Double> callable;
    private int id;

    // Extracted from https://easings.net/

    public static Easing EASE_IN_SINE = new Easing(t -> 1 - Math.cos((t * Math.PI) / 2), 0);
    public static Easing EASE_OUT_SINE = new Easing(t -> Math.sin((t * Math.PI) / 2), 1);
    public static Easing EASE_INOUT_SINE = new Easing(t -> -(Math.cos(Math.PI * t) - 1) / 2, 2);

    public static Easing EASE_IN_QUAD = new Easing(t -> t*t, 3);
    public static Easing EASE_OUT_QUAD = new Easing(t -> 1 - (1 - t) * (1 - t), 4);
    public static Easing EASE_INOUT_QUAD = new Easing(t -> t < 0.5 ? 2 * t * t : 1 - Math.pow(-2 * t + 2, 2) / 2, 5);

    public static Easing EASE_IN_CUBIC = new Easing(t -> t*t*t, 6);
    public static Easing EASE_OUT_CUBIC = new Easing(t -> 1 - Math.pow(1 - t, 3), 7);
    public static Easing EASE_INOUT_CUBIC = new Easing(t -> t < 0.5 ? 4 * t * t * t : 1 - Math.pow(-2 * t + 2, 3) / 2, 8);

    public static Easing EASE_IN_QUART = new Easing(t -> t*t*t*t, 9);
    public static Easing EASE_OUT_QUART = new Easing(t -> 1 - Math.pow(1 - t, 4), 10);
    public static Easing EASE_INOUT_QUART = new Easing(t -> t < 0.5 ? 8 * t * t * t * t : 1 - Math.pow(-2 * t + 2, 4) / 2, 11);

    public static Easing EASE_IN_QUINT = new Easing(t -> t*t*t*t*t, 12);
    public static Easing EASE_OUT_QUINT = new Easing(t -> 1 - Math.pow(1 - t, 5), 13);
    public static Easing EASE_INOUT_QUINT = new Easing(t -> t < 0.5 ? 16 * t * t * t * t * t : 1 - Math.pow(-2 * t + 2, 5) / 2, 14);

    public static Easing EASE_IN_EXPO = new Easing(t -> Math.pow(2, 10 * (t - 1)), 15);
    public static Easing EASE_OUT_EXPO = new Easing(t -> 1 - Math.pow(2, -10 * t), 16);
    public static Easing EASE_INOUT_EXPO = new Easing(t -> t < 0.5 ? Math.pow(2, 20 * t - 10) / 2 : 1 - Math.pow(2, -20 * t + 10) / 2, 17);

    public static Easing EASE_IN_CIRC = new Easing(t -> 1 - Math.sqrt(1 - t * t), 18);
    public static Easing EASE_OUT_CIRC = new Easing(t -> Math.sqrt(1 - (t - 1) * (t - 1)), 19);
    public static Easing EASE_INOUT_CIRC = new Easing(t -> t < 0.5 ? (1 - Math.sqrt(1 - 2 * t * t)) / 2 : (Math.sqrt(1 - 2 * (t - 1) * (t - 1)) + 1) / 2, 20);

    public static Easing LINEAR = new Easing(t -> t, 21);
    public static Easing INSTANT = new Easing(t -> t == 1.0d ? 1d : 0d, 21);

    public static HashMap<String, Easing> getValues() {
        HashMap<String, Easing> values = new HashMap<>();

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

        return values;
    }

    public Easing(Function<Double, Double> callable, int id) {
        this.id = id;
        this.callable = callable;
    }

    /**
     * @param t 0-1
     * @return 0-1
     */
    public double getValue(double t) {
        return callable.apply(t);
    }

    public int getId() {
        return id;
    }

    public static Easing fromId(int id) {
        switch (id) {
            case 0:
                return EASE_IN_SINE;
            case 1:
                return EASE_OUT_SINE;
            case 2:
                return EASE_INOUT_SINE;
            case 3:
                return EASE_IN_QUAD;
            case 4:
                return EASE_OUT_QUAD;
            case 5:
                return EASE_INOUT_QUAD;
            case 6:
                return EASE_IN_CUBIC;
            case 7:
                return EASE_OUT_CUBIC;
            case 8:
                return EASE_INOUT_CUBIC;
            case 9:
                return EASE_IN_QUART;
            case 10:
                return EASE_OUT_QUART;
            case 11:
                return EASE_INOUT_QUART;
            case 12:
                return EASE_IN_QUINT;
            case 13:
                return EASE_OUT_QUINT;
            case 14:
                return EASE_INOUT_QUINT;
            case 15:
                return EASE_IN_EXPO;
            case 16:
                return EASE_OUT_EXPO;
            case 17:
                return EASE_INOUT_EXPO;
            case 18:
                return EASE_IN_CIRC;
            case 19:
                return EASE_OUT_CIRC;
            case 20:
                return EASE_INOUT_CIRC;
            default:
                return EASE_INOUT_CUBIC;
        }
    }
}
