package ie.corballis.fixtures.assertion;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import ie.corballis.fixtures.core.BeanFactory;
import ie.corballis.fixtures.core.InvocationContextHolder;
import ie.corballis.fixtures.io.ClassPathFixtureScanner;
import ie.corballis.fixtures.io.write.SnapshotGenerator;
import org.fest.assertions.api.AbstractAssert;
import org.fest.assertions.api.Assertions;
import org.hamcrest.MatcherAssert;
import uk.co.datumedge.hamcrest.json.SameJSONAs;

import java.io.IOException;

import static ie.corballis.fixtures.core.ObjectMapperProvider.getObjectMapper;
import static ie.corballis.fixtures.util.ClassUtils.getTestClass;
import static ie.corballis.fixtures.util.ClassUtils.getTestMethodName;
import static uk.co.datumedge.hamcrest.json.SameJSONAs.sameJSONAs;

public class FixtureAssert extends AbstractAssert<FixtureAssert, Object> {

    private static final BeanFactory beanFactory;
    private static final SnapshotGenerator snapshotGenerator;

    static {
        beanFactory = new BeanFactory(getObjectMapper(), new ClassPathFixtureScanner());
        beanFactory.initSilent();
        snapshotGenerator = new SnapshotGenerator(getObjectMapper());
    }

    public FixtureAssert(Object actual) {
        super(actual, FixtureAssert.class);
    }

    public static FixtureAssert assertThat(Object actual) {
        return new FixtureAssert(actual);
    }

    public FixtureAssert matches(String... fixtures) throws JsonProcessingException {
        assertJSON(sameJSONAs(beanFactory.createAsString(fixtures)).allowingAnyArrayOrdering()
                                                                   .allowingExtraUnexpectedFields(), fixtures);
        return this;
    }

    private void assertJSON(SameJSONAs<? super String> expected, String... fixtures) throws JsonProcessingException {
        isNotNull();
        try {
            MatcherAssert.assertThat(getObjectMapper().writeValueAsString(actual), expected);
        } catch (AssertionError assertionError) {
            String actualPrettyString =
                unifyLineEndings(getObjectMapper().writer().withDefaultPrettyPrinter().writeValueAsString(actual));
            String expectedPrettyString = unifyLineEndings(beanFactory.createAsString(true, fixtures));
            System.err.print(assertionError.getMessage());
            Assertions.assertThat(actualPrettyString).isEqualTo(expectedPrettyString);
        }
    }

    // changes the Windows CR LF line endings to Unix LF type in a string
    // so that the pretty strings are formatted uniformly, independently of the OS platform
    private String unifyLineEndings(String s) {
        return s.replaceAll("\\r\\n", "\\\n");
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

    public FixtureAssert toMatchSnapshot() throws IOException {
        String testMethodName = getTestMethodName();
        InvocationContextHolder.updateContext(testMethodName);
        String fixtureName = InvocationContextHolder.currentSnapshotName();
        JsonNode snapshotFixture = beanFactory.getFixtureAsJsonNode(fixtureName).orElse(null);
        if (snapshotFixture == null) {
            createOrUpdateFixture(fixtureName, actual);
        } else {
            assertJSON(sameJSONAs(beanFactory.createAsString(false, snapshotFixture)), fixtureName);
        }
        return this;
    }

    private void createOrUpdateFixture(String fixtureName, Object actual) throws IOException {
        Class testClass = getTestClass();
        snapshotGenerator.write(testClass, fixtureName, actual);
    }

}