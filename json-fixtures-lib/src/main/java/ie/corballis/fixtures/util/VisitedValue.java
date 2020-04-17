package ie.corballis.fixtures.util;

public class VisitedValue {

    private final Object value;
    private final boolean appendToResult;

    public VisitedValue(Object value, boolean appendToResult) {
        this.value = value;
        this.appendToResult = appendToResult;
    }

    public static VisitedValue skipValue() {
        return new VisitedValue(null, false);
    }

    public static VisitedValue valueOf(Object value) {
        return new VisitedValue(value, true);
    }

    public Object getValue() {
        return value;
    }

    public boolean isAppendToResult() {
        return appendToResult;
    }

}
