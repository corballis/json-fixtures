package ie.corballis.fixtures.references;

import com.fasterxml.jackson.core.JsonProcessingException;
import ie.corballis.fixtures.annotation.Fixture;
import ie.corballis.fixtures.annotation.FixtureAnnotations;
import ie.corballis.fixtures.assertion.FixtureAssert;
import ie.corballis.fixtures.core.BeanFactory;
import ie.corballis.fixtures.core.ObjectMapperProvider;
import ie.corballis.fixtures.io.ClassPathFixtureScanner;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;

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

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void init() throws Exception {
        FixtureAnnotations.initFixtures(this);
    }

    @Test
    public void referencesTest() throws JsonProcessingException {
        FixtureAssert.assertThat(person1r).matchesWithStrictOrder("person1");
        FixtureAssert.assertThat(person2r).matchesWithStrictOrder("person2");
        FixtureAssert.assertThat(person3r).matchesWithStrictOrder("person3");
        FixtureAssert.assertThat(person4r).matchesWithStrictOrder("person4");
        FixtureAssert.assertThat(person5r).matchesWithStrictOrder("person5");
    }

    @Test
    public void shouldFailWhenUnknownReferenceFound() throws IOException {
        BeanFactory factory =
            new BeanFactory(ObjectMapperProvider.getObjectMapper(), new ClassPathFixtureScanner());
        factory.init();

        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Fixture reference value detected without existing fixture in " +
                                        "unknownReference->model->#noSuchFixture property. " +
                                        "Every string value starting with # is considered as fixture reference. " +
                                        "If you would like to refer to a fixture, please provide a valid fixture name after #, " +
                                        "otherwise if you have a non-referring string starting with # then change the default prefix in @Fixture annotation");

        factory.create(Car.class, "unknownReference");
    }

    @Test
    public void primitivesForBaseObjectAreNotAllowed() throws IOException {
        BeanFactory factory =
            new BeanFactory(ObjectMapperProvider.getObjectMapper(), new ClassPathFixtureScanner());
        factory.init();

        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("The base object of fixtures must either be a bean, object, map or any collection type." +
                                        " If you need to use primitives add variables into your test class instead. " +
                                        "\"Skoda\" is used as value which is not supported.");

        factory.create(Car.class, "model3");
    }

}