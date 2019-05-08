package ie.corballis.fixtures.references;

import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;

public class MapAndSetSample {
    private String string;
    private Map<String, Car> sampleMap = newHashMap();
    private Set<Car> sampleSet = newHashSet();

    public MapAndSetSample(String string, Map<String, Car> sampleMap, Set<Car> sampleSet) {
        this.string = string;
        this.sampleMap = sampleMap;
        this.sampleSet = sampleSet;
    }

    public MapAndSetSample() {
    }

    @Override
    public String toString() {
        return "MapAndSetSample: " +
               "\n\t\tstring = " + string +
               ",\n\t\tsampleMap = " + constructSampleMapString() +
               ",\n\t\tsampleSet = " + constructSampleSetString();
    }

    private String constructSampleMapString() {
        if (sampleMap == null) {
            return null;
        }
        if (sampleMap.isEmpty()) {
            return "empty map";
        }
        StringBuilder sb = new StringBuilder();
        for (Map.Entry entry : sampleMap.entrySet()) {
            sb.append("\n\t\t\t");
            sb.append(entry.getKey());
            sb.append(" -> ");
            sb.append(entry.getValue());
            sb.append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    private String constructSampleSetString() {
        if (sampleSet == null) {
            return null;
        }
        if (sampleSet.isEmpty()) {
            return "empty set";
        }
        StringBuilder sb = new StringBuilder();
        for (Car car : sampleSet) {
            sb.append("\n\t\t\t" + car + ",");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MapAndSetSample that = (MapAndSetSample) o;
        if (string != null ? !string.equals(that.string) : that.string != null) {
            return false;
        }
        if (sampleMap != null ? !sampleMap.equals(that.sampleMap) : that.sampleMap != null) {
            return false;
        }
        return !(sampleSet != null ? !sampleSet.equals(that.sampleSet) : that.sampleSet != null);
    }

    @Override
    public int hashCode() {
        int result = string != null ? string.hashCode() : 0;
        result = 31 * result + (sampleMap != null ? sampleMap.hashCode() : 0);
        result = 31 * result + (sampleSet != null ? sampleSet.hashCode() : 0);
        return result;
    }
}