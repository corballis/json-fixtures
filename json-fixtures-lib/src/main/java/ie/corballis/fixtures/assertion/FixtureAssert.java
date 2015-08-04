package ie.corballis.fixtures.assertion;

import com.fasterxml.jackson.core.JsonProcessingException;
import ie.corballis.fixtures.core.BeanFactory;
import ie.corballis.fixtures.core.ObjectMapperProvider;
import ie.corballis.fixtures.io.ClassPathFixtureScanner;
import org.fest.assertions.api.AbstractAssert;
import org.fest.assertions.api.Assertions;
import org.hamcrest.MatcherAssert;
import uk.co.datumedge.hamcrest.json.SameJSONAs;

import java.io.IOException;

import static uk.co.datumedge.hamcrest.json.SameJSONAs.sameJSONAs;

public class FixtureAssert extends AbstractAssert<FixtureAssert, Object> {

    private static final BeanFactory beanFactory;

    static {
        beanFactory = new BeanFactory(ObjectMapperProvider.getObjectMapper(), new ClassPathFixtureScanner());
        try {
            beanFactory.init();
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public FixtureAssert(Object actual) {
        super(actual, FixtureAssert.class);
    }

    public static FixtureAssert assertThat(Object actual) {
        return new FixtureAssert(actual);
    }

    private void assertJSON(SameJSONAs<? super String> expected, String... fixtures) throws JsonProcessingException {
        isNotNull();
        try {
            MatcherAssert.assertThat(ObjectMapperProvider.getObjectMapper().writeValueAsString(actual), expected);
        } catch(AssertionError assertionError){
            String actualPrettyString = ObjectMapperProvider.getObjectMapper().writer().withDefaultPrettyPrinter()
                    .writeValueAsString(actual);
            String expectedPrettyString = beanFactory.createAsString(true, fixtures);
            System.err.print(assertionError.getMessage());
            Assertions.assertThat(actualPrettyString).isEqualTo(expectedPrettyString);
        }
    }

    public FixtureAssert matches(String... fixtures) throws JsonProcessingException {
        assertJSON(sameJSONAs(beanFactory.createAsString(fixtures)).allowingAnyArrayOrdering()
                .allowingExtraUnexpectedFields(), fixtures);
        return this;
    }

    public FixtureAssert matchesWithStrictOrder(String... fixtures) throws JsonProcessingException {
        assertJSON(sameJSONAs(beanFactory.createAsString(fixtures)).allowingExtraUnexpectedFields(), fixtures);
        return this;
    }

    public FixtureAssert matchesExactly(String... fixtures) throws JsonProcessingException {
        assertJSON(sameJSONAs(beanFactory.createAsString(fixtures)).allowingAnyArrayOrdering(), fixtures);
        return this;
    }

    public FixtureAssert matchesExactlyWithStrictOrder(String... fixtures) throws JsonProcessingException {
        assertJSON(sameJSONAs(beanFactory.createAsString(fixtures)), fixtures);
        return this;
    }
}
