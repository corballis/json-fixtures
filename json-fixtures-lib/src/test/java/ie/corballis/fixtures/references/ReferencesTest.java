package ie.corballis.fixtures.references;

import com.fasterxml.jackson.core.JsonProcessingException;
import ie.corballis.fixtures.annotation.Fixture;
import ie.corballis.fixtures.annotation.FixtureAnnotations;
import ie.corballis.fixtures.assertion.FixtureAssert;
import org.junit.Before;
import org.junit.Test;

public class ReferencesTest {
    // fixtures of file "references.fixtures.json", with the same content as file "expected.fixtures.json",
    // but with most values given with references (e.g. "#car1")
    @Fixture
    private Person person1r;
    @Fixture({"person2r1", "person2r2"})
    private Person person2r;
    @Fixture
    private Person person3r;
    @Fixture
    private Person person4r;
    @Fixture
    private Person person5r;
    @Fixture
    private Person circularr;

    @Before
    public void init() throws Exception {
        FixtureAnnotations.initFixtures(this);
    }

    @Test
    public void referencesTest() throws JsonProcessingException {
        System.out.println(person1r);
        System.out.println(person2r);
        System.out.println(person3r);
        System.out.println(person4r);
        System.out.println(person5r);
        System.out.println(circularr);
        FixtureAssert.assertThat(person1r).matchesWithStrictOrder("person1");
        FixtureAssert.assertThat(person2r).matchesWithStrictOrder("person2");
        FixtureAssert.assertThat(person3r).matchesWithStrictOrder("person3");
        FixtureAssert.assertThat(person4r).matchesWithStrictOrder("person4");
        FixtureAssert.assertThat(person5r).matchesWithStrictOrder("person5");
        FixtureAssert.assertThat(circularr).matchesWithStrictOrder("circular");
    }
}