package engineer.pol.cinematic.timeline.core;

public class CompositionProperty {

    private final String name;
    private final Double minValue;
    private final Double maxValue;

    public CompositionProperty(String name) {
        this(name, null, null);
    }

    public CompositionProperty(String name, Double minValue, Double maxValue) {
        this.name = name;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    public String getName() {
        return name;
    }

    public Double getMinValue() {
        return minValue;
    }

    public Double getMaxValue() {
        return maxValue;
    }

    public boolean hasMinValue() {
        return minValue != null;
    }

    public boolean hasMaxValue() {
        return maxValue != null;
    }

}
