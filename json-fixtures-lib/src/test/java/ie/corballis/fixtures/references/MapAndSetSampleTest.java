package ie.corballis.fixtures.references;

import ie.corballis.fixtures.annotation.Fixture;
import ie.corballis.fixtures.annotation.FixtureAnnotations;
import org.junit.Test;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;
import static org.junit.Assert.assertEquals;

public class MapAndSetSampleTest {
    @Fixture
    private Map<String, MapAndSetSample> map;

    @Test
    public void testMapAndSet() throws Exception {
        FixtureAnnotations.initFixtures(this);

        for (Map.Entry entry : map.entrySet()) {
            System.out.println("Map entry:");
            System.out.println("\tKey: " + entry.getKey());
            System.out.println("\tValue: " + entry.getValue());
        }

        Map<String, Car> sampleMap1 = newHashMap();
        sampleMap1.put("car1", new Car("unknownModel1"));
        sampleMap1.put("car2", new Car("unknownModel2"));
        sampleMap1.put("car3", new Car("unknownModel3"));
        MapAndSetSample value1 = new MapAndSetSample("#noReferenceString1", sampleMap1, new HashSet<Car>());

        Map<String, Car> sampleMap2 = newHashMap();
        sampleMap2.put("car4", new Car("unknownModel4"));
        sampleMap2.put("car5", new Car("unknownModel5"));
        Set<Car> sampleSet = newHashSet();
        sampleSet.add(new Car("unknownModel1"));
        sampleSet.add(new Car("Model1"));
        sampleSet.add(new Car("unknownModel2"));
        sampleSet.add(new Car("Model2"));
        sampleSet.add(new Car("unknownModel3"));
        sampleSet.add(new Car("Model3"));
        sampleSet.add(new Car("unknownModel4"));
        sampleSet.add(new Car("Model4"));
        sampleSet.add(new Car("unknownModel5"));
        sampleSet.add(new Car("Model5"));
        MapAndSetSample value2 = new MapAndSetSample("#noReferenceString2", sampleMap2, sampleSet);

        Map<String, MapAndSetSample> expected = newHashMap();
        expected.put("firstMap", value1);
        expected.put("secondMap", value2);

        assertEquals(expected, map);
    }
}