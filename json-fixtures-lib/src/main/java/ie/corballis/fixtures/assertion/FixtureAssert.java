package ie.corballis.fixtures.assertion;

import com.fasterxml.jackson.core.JsonProcessingException;
import ie.corballis.fixtures.core.BeanFactory;
import ie.corballis.fixtures.core.ObjectMapperProvider;
import ie.corballis.fixtures.io.ClassPathFixtureScanner;
import org.fest.assertions.api.AbstractAssert;
import org.hamcrest.MatcherAssert;

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

    public FixtureAssert matches(String... fixtures) throws JsonProcessingException {
        isNotNull();

        MatcherAssert.assertThat(ObjectMapperProvider.getObjectMapper().writeValueAsString(actual),
                                 sameJSONAs(beanFactory.createAsString(fixtures)).allowingAnyArrayOrdering()
                                         .allowingExtraUnexpectedFields());

        return this;
    }

    public FixtureAssert matchesWithStrictOrder(String... fixtures) throws JsonProcessingException {
        isNotNull();

        MatcherAssert.assertThat(ObjectMapperProvider.getObjectMapper().writeValueAsString(actual),
                                 sameJSONAs(beanFactory.createAsString(fixtures)).allowingExtraUnexpectedFields());

        return this;
    }

    public FixtureAssert matchesExactly(String... fixtures) throws JsonProcessingException {
        isNotNull();

        MatcherAssert.assertThat(ObjectMapperProvider.getObjectMapper().writeValueAsString(actual),
                                 sameJSONAs(beanFactory.createAsString(fixtures)).allowingAnyArrayOrdering());

        return this;
    }

    public FixtureAssert matchesExactlyWithStrictOrder(String... fixtures) throws JsonProcessingException {
        isNotNull();

        MatcherAssert.assertThat(ObjectMapperProvider.getObjectMapper().writeValueAsString(actual),
                                 sameJSONAs(beanFactory.createAsString(fixtures)));

        return this;
    }
}
