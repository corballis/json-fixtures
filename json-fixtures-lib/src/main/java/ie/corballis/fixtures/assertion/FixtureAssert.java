package ie.corballis.fixtures.assertion;

import com.fasterxml.jackson.core.JsonProcessingException;
import ie.corballis.fixtures.core.BeanFactory;
import ie.corballis.fixtures.io.ClassPathFixtureScanner;
import ie.corballis.fixtures.snapshot.SnapshotGenerator;
import org.fest.assertions.api.AbstractAssert;
import org.fest.assertions.api.Assertions;
import org.hamcrest.MatcherAssert;
import uk.co.datumedge.hamcrest.json.SameJSONAs;

import java.io.IOException;
import java.util.function.Function;

import static ie.corballis.fixtures.settings.SettingsHolder.settings;
import static uk.co.datumedge.hamcrest.json.SameJSONAs.sameJSONAs;

public class FixtureAssert extends AbstractAssert<FixtureAssert, Object> {

    private static final BeanFactory beanFactory;
    private static final SnapshotGenerator snapshotGenerator;

    static {
        ClassPathFixtureScanner scanner = new ClassPathFixtureScanner();
        beanFactory = new BeanFactory(settings().getObjectMapper(), scanner);
        beanFactory.initSilent();
        snapshotGenerator = new SnapshotGenerator(beanFactory, scanner);
    }

    public FixtureAssert(Object actual) {
        super(actual, FixtureAssert.class);
    }

    public static FixtureAssert assertThat(Object actual) {
        return new FixtureAssert(actual);
    }

    public FixtureAssert matches(String... fixtures) throws JsonProcessingException {
        assertJSON(getExpectedJson(beanFactory.createAsString(fixtures)), fixtures);
        return this;
    }

    private void assertJSON(SameJSONAs<? super String> expected, String... fixtures) throws JsonProcessingException {
        isNotNull();
        try {
            MatcherAssert.assertThat(settings().getObjectMapper().writeValueAsString(actual), expected);
        } catch (AssertionError assertionError) {
            String actualPrettyString = unifyLineEndings(settings().getObjectMapper()
                                                                   .writer()
                                                                   .withDefaultPrettyPrinter()
                                                                   .writeValueAsString(actual));
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

    private SameJSONAs<? super String> getExpectedJson(String expectedJson) {
        return sameJSONAs(expectedJson).allowingAnyArrayOrdering().allowingExtraUnexpectedFields();
    }

    public FixtureAssert matchesWithStrictOrder(String... fixtures) throws JsonProcessingException {
        assertJSON(getExpectedJsonWithStrictOrder(beanFactory.createAsString(fixtures)), fixtures);
        return this;
    }

    private SameJSONAs<? super String> getExpectedJsonWithStrictOrder(String expectedJson) {
        return sameJSONAs(expectedJson).allowingExtraUnexpectedFields();
    }

    public FixtureAssert matchesExactly(String... fixtures) throws JsonProcessingException {
        assertJSON(getExpectedJsonWhichMatchesExactly(beanFactory.createAsString(fixtures)), fixtures);
        return this;
    }

    private SameJSONAs<? super String> getExpectedJsonWhichMatchesExactly(String expectedJson) {
        return sameJSONAs(expectedJson).allowingAnyArrayOrdering();
    }

    public FixtureAssert matchesExactlyWithStrictOrder(String... fixtures) throws JsonProcessingException {
        assertJSON(getExpectedJsonWhichMatchesExactlyWithStrictOrder(beanFactory.createAsString(fixtures)), fixtures);
        return this;
    }

    private SameJSONAs<? super String> getExpectedJsonWhichMatchesExactlyWithStrictOrder(String expectedJson) {
        return sameJSONAs(expectedJson);
    }

    public FixtureAssert toMatchSnapshot() throws IOException {
        return toMatchSnapshot(false);
    }

    public FixtureAssert toMatchSnapshot(boolean regenerateFixture) throws IOException {
        return toMatchSnapshot(this::getExpectedJson, regenerateFixture);
    }

    private FixtureAssert toMatchSnapshot(Function<String, SameJSONAs<? super String>> getSameJsonAs,
                                          boolean regenerateFixture) throws IOException {
        if (!snapshotGenerator.createOrUpdateFixture(actual, regenerateFixture)) {
            String expectedJson = beanFactory.createAsString(false, snapshotGenerator.getSnapshotFixtureNode());
            assertJSON(getSameJsonAs.apply(expectedJson), snapshotGenerator.getCurrentSnapshotFixtureName());
        }

        return this;
    }

    public FixtureAssert toMatchSnapshotWithStrictOrder() throws IOException {
        return toMatchSnapshotWithStrictOrder(false);
    }

    public FixtureAssert toMatchSnapshotWithStrictOrder(boolean regenerateFixture) throws IOException {
        return toMatchSnapshot(this::getExpectedJsonWithStrictOrder, regenerateFixture);
    }

    public FixtureAssert toMatchSnapshotExactly() throws IOException {
        return toMatchSnapshotExactly(false);
    }

    public FixtureAssert toMatchSnapshotExactly(boolean regenerateFixture) throws IOException {
        return toMatchSnapshot(this::getExpectedJsonWhichMatchesExactly, regenerateFixture);
    }

    public FixtureAssert toMatchSnapshotExactlyWithStrictOrder() throws IOException {
        return toMatchSnapshotExactlyWithStrictOrder(false);
    }

    public FixtureAssert toMatchSnapshotExactlyWithStrictOrder(boolean regenerateFixture) throws IOException {
        return toMatchSnapshot(this::getExpectedJsonWhichMatchesExactlyWithStrictOrder, regenerateFixture);
    }

}