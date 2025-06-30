package lang;

public class Value {
    private final Object value;

    public Value(Object value) {
        this.value = value;
    }

    public Integer asInt() {
        return (Integer) value;
    }

    public Float asFloat() {
        return (Float) value;
    }

    public Boolean asBoolean() {
        return (Boolean) value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
