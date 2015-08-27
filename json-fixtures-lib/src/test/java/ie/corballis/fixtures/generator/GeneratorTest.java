package ie.corballis.fixtures.generator;

import ie.corballis.fixtures.annotation.Fixture;
import ie.corballis.fixtures.annotation.FixtureAnnotations;
import org.fest.assertions.api.Assertions;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasEntry;

public class GeneratorTest {
    private static final String folder = System.getProperty("java.io.tmpdir");
    private static final String fileNamePrefix = "sample1";
    private static final String fixtureName = "sampleFixture1";
    private static final boolean append = true;

    @Test
    public void generateMapFromBeanDirectly() throws Exception {
        DefaultFixtureGenerator defaultFixtureGenerator = new DefaultFixtureGenerator();
        Map<String, Object> map = defaultFixtureGenerator.generateMapFromBeanDirectly(SampleClassCollections.class);
        testMap(map);
    }

    private void testMap(Map<String, Object> map) {
        Assertions.assertThat(map).isNotNull();
        MatcherAssert.assertThat(map, hasEntry("doubleField", (Object) (-3.12)));
        MatcherAssert.assertThat(map, hasEntry("StringFieldInitialized", (Object) "xxx"));
        MatcherAssert.assertThat(map,
                                 hasEntry("WrapperArrayFieldSpecified",
                                          (Object) newArrayList(null, null, null, null, null)));
        MatcherAssert.assertThat(map,
                                 hasEntry("WrapperArrayFieldInitialized",
                                          (Object) newArrayList(-0.23, 5.01, 99999.0, 35.674)));
        MatcherAssert.assertThat(map,
                                 hasEntry("primitiveArrayFieldInitialized", (Object) newArrayList(true, false, false)));
        MatcherAssert.assertThat(map,
                                 hasEntry("ArrayListFieldInitialized", (Object) newArrayList("jdh", "gvgv", "hh")));
        MatcherAssert.assertThat(map,
                                 hasEntry("StringArrayFieldInitialized", (Object) newArrayList("sjdhbh", "hdbhb")));
    }

    @Test
    public void fixtureFileWritingTest() {
        try {
            Map<String, Object> objectAsMap =
                new DefaultFixtureGenerator().generateMapFromBeanDirectly(SampleClassCollections.class);
            new DefaultFileSystemWriter().writeOut(folder, fileNamePrefix, fixtureName, objectAsMap, append);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Fixture("sampleFixture1")
    private SampleClassCollections testField;

    @Test
    public void rereadingGeneratedFixture() throws Exception {
        FixtureAnnotations.initFixtures(this);
        DefaultFixtureGenerator defaultFixtureGenerator = new DefaultFixtureGenerator();
        SampleClassCollections obj =
            (SampleClassCollections) defaultFixtureGenerator.createBeanInstance(SampleClassCollections.class);
        Assertions.assertThat(testField).isNotNull();
        // the date and date array fields won't be the same because 'new Date()' always returns the actual date and time
        assertThat(testField).isEqualToIgnoringGivenFields(obj, "DateFieldDefault", "DateArrayFieldInitialized");
    }
}