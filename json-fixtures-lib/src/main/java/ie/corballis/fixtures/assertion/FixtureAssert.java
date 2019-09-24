package ie.corballis.fixtures.assertion;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.fest.assertions.api.AbstractAssert;
import org.fest.assertions.api.Assertions;
import org.hamcrest.MatcherAssert;
import uk.co.datumedge.hamcrest.json.SameJSONAs;

import java.io.IOException;
import java.util.*;

import static com.google.common.collect.Lists.newArrayList;
import static ie.corballis.fixtures.assertion.MatchingMode.*;
import static ie.corballis.fixtures.assertion.PropertyMatchers.empty;
import static ie.corballis.fixtures.settings.SettingsHolder.settings;
import static ie.corballis.fixtures.util.JsonUtils.visitElements;
import static ie.corballis.fixtures.util.StringUtils.unifyLineEndings;

public class FixtureAssert extends AbstractAssert<FixtureAssert, Object> {

    public FixtureAssert(Object actual) {
        super(actual, FixtureAssert.class);
    }

    public static FixtureAssert assertThat(Object actual) {
        return new FixtureAssert(actual);
    }

    public FixtureAssert matches(String... fixtures) throws JsonProcessingException {
        return matches(empty(), fixtures);
    }

    public FixtureAssert matches(PropertyMatchers matchers, String... fixtures) throws JsonProcessingException {
        assertJSON(MATCHES, matchers, fixtures);
        return this;
    }

    private void assertJSON(MatchingMode matchingMode, PropertyMatchers matchers, String... fixtures) throws
                                                                                                      JsonProcessingException {
        isNotNull();
        SameJSONAs<String> expected = matchingMode.getJsonMatcher(settings().getBeanFactory().createAsString(fixtures));

        if (matchers.isEmpty()) {
            assertThatExpectedMatchesWithActual(expected,
                                                settings().getObjectMapper().writeValueAsString(actual),
                                                fixtures);
        } else {
            compareWithoutOverriddenProperties(matchingMode, matchers, fixtures);
            verifyOverriddenPropertiesOnly(matchers);
        }
    }

    private void assertThatExpectedMatchesWithActual(SameJSONAs<String> expected,
                                                     String actualJson,
                                                     String[] fixtures) throws JsonProcessingException {
        try {
            MatcherAssert.assertThat(actualJson, expected);
        } catch (AssertionError assertionError) {
            failAndPrettyPrintError(assertionError, fixtures);
        }
    }

    private void failAndPrettyPrintError(AssertionError assertionError, String[] fixtures) throws
                                                                                           JsonProcessingException {
        String actualPrettyString = unifyLineEndings(settings().getObjectMapper().writerWithDefaultPrettyPrinter()
                                                               .writeValueAsString(actual));
        String expectedPrettyString = unifyLineEndings(settings().getBeanFactory().createAsString(true, fixtures));
        System.err.print(assertionError.getMessage());
        Assertions.assertThat(actualPrettyString).isEqualTo(expectedPrettyString);
    }

    private void compareWithoutOverriddenProperties(MatchingMode matchingMode,
                                                    PropertyMatchers matchers,
                                                    String[] fixtures) throws JsonProcessingException {
        Object clearedActual = removeOverriddenProperties(this.actual, matchers);
        Object clearedExpected = removeOverriddenProperties(settings().getBeanFactory()
                                                                      .create(getTypeToConvert(this.actual), fixtures),
                                                            matchers);

        String clearedActualString = settings().getObjectMapper().writeValueAsString(clearedActual);
        SameJSONAs<String> expectedMatcher =
            matchingMode.getJsonMatcher(settings().getObjectMapper().writeValueAsString(clearedExpected));

        assertThatExpectedMatchesWithActual(expectedMatcher, clearedActualString, fixtures);
    }

    private Object removeOverriddenProperties(Object entity, PropertyMatchers matchers) {
        Set<String> properties = matchers.getProperties();

        if (!matchers.isEmpty()) {
            Object newObject = settings().getObjectMapper().convertValue(entity, getTypeToConvert(entity));
            for (String property : properties) {
                List<String> parts = newArrayList(property.split("\\."));
                JsonNode updatedNode = settings().getObjectMapper().convertValue(newObject, JsonNode.class);
                newObject =
                    visitElements(updatedNode, newObject, new Stack<>(), new PropertyMatcherFilteringVisitor(parts));
            }
            return newObject;
        }

        return entity;
    }

    private Class<?> getTypeToConvert(Object entity) {
        return (entity.getClass().isArray() || entity instanceof Collection) ? List.class : Map.class;
    }

    private void verifyOverriddenPropertiesOnly(PropertyMatchers matchers) {
        for (String property : matchers.getProperties()) {
            List<String> parts = newArrayList(property.split("\\."));
            JsonNode actual = settings().getObjectMapper().convertValue(this.actual, JsonNode.class);
            visitElements(actual, new PropertyMatcherAssertingVisitor(parts, matchers));
        }
    }

    public FixtureAssert matchesWithStrictOrder(String... fixtures) throws JsonProcessingException {
        return matchesWithStrictOrder(empty(), fixtures);
    }

    public FixtureAssert matchesWithStrictOrder(PropertyMatchers matchers, String... fixtures) throws
                                                                                               JsonProcessingException {
        assertJSON(MATCHES_WITH_STRICT_ORDER, matchers, fixtures);
        return this;
    }

    public FixtureAssert matchesExactly(String... fixtures) throws JsonProcessingException {
        return matchesExactly(empty(), fixtures);
    }

    public FixtureAssert matchesExactly(PropertyMatchers matchers, String... fixtures) throws JsonProcessingException {
        assertJSON(MATCHES_EXACTLY, matchers, fixtures);
        return this;
    }

    public FixtureAssert matchesExactlyWithStrictOrder(String... fixtures) throws JsonProcessingException {
        return matchesExactlyWithStrictOrder(empty(), fixtures);
    }

    public FixtureAssert matchesExactlyWithStrictOrder(PropertyMatchers propertyMatchers, String... fixtures) throws
                                                                                                              JsonProcessingException {
        assertJSON(MATCHES_EXACTLY_WITH_STRICT_ORDER, propertyMatchers, fixtures);
        return this;
    }

    public FixtureAssert toMatchSnapshot() throws IOException {
        return toMatchSnapshot(false);
    }

    public FixtureAssert toMatchSnapshot(boolean regenerateFixture) throws IOException {
        return toMatchSnapshot(regenerateFixture, empty());
    }

    public FixtureAssert toMatchSnapshot(boolean regenerateFixture, PropertyMatchers propertyMatchers) throws
                                                                                                       IOException {
        return toMatchSnapshot(MATCHES, regenerateFixture, propertyMatchers);
    }

    private FixtureAssert toMatchSnapshot(MatchingMode matchingMode,
                                          boolean regenerateFixture,
                                          PropertyMatchers propertyMatchers) throws IOException {
        if (!settings().getSnapshotGenerator()
                       .createOrUpdateFixture(removeOverriddenProperties(actual, propertyMatchers),
                                              regenerateFixture)) {
            assertJSON(matchingMode,
                       propertyMatchers,
                       settings().getSnapshotGenerator().getCurrentSnapshotFixtureName());
        }

        return this;
    }

    public FixtureAssert toMatchSnapshot(PropertyMatchers propertyMatchers) throws IOException {
        return toMatchSnapshot(false, propertyMatchers);
    }

    public FixtureAssert toMatchSnapshotWithStrictOrder() throws IOException {
        return toMatchSnapshotWithStrictOrder(false, empty());
    }

    public FixtureAssert toMatchSnapshotWithStrictOrder(boolean regenerateFixture,
                                                        PropertyMatchers propertyMatchers) throws IOException {
        return toMatchSnapshot(MATCHES_WITH_STRICT_ORDER, regenerateFixture, propertyMatchers);
    }

    public FixtureAssert toMatchSnapshotWithStrictOrder(PropertyMatchers propertyMatchers) throws IOException {
        return toMatchSnapshotWithStrictOrder(false, propertyMatchers);
    }

    public FixtureAssert toMatchSnapshotWithStrictOrder(boolean regenerateFixture) throws IOException {
        return toMatchSnapshotWithStrictOrder(false, empty());
    }

    public FixtureAssert toMatchSnapshotExactly() throws IOException {
        return toMatchSnapshotExactly(false);
    }

    public FixtureAssert toMatchSnapshotExactly(boolean regenerateFixture) throws IOException {
        return toMatchSnapshotExactly(regenerateFixture, empty());
    }

    public FixtureAssert toMatchSnapshotExactly(boolean regenerateFixture, PropertyMatchers propertyMatchers) throws
                                                                                                              IOException {
        return toMatchSnapshot(MATCHES_EXACTLY, regenerateFixture, propertyMatchers);
    }

    public FixtureAssert toMatchSnapshotExactly(PropertyMatchers propertyMatchers) throws IOException {
        return toMatchSnapshotExactly(false, propertyMatchers);
    }

    public FixtureAssert toMatchSnapshotExactlyWithStrictOrder() throws IOException {
        return toMatchSnapshotExactlyWithStrictOrder(false);
    }

    public FixtureAssert toMatchSnapshotExactlyWithStrictOrder(boolean regenerateFixture) throws IOException {
        return toMatchSnapshotExactlyWithStrictOrder(regenerateFixture, empty());
    }

    public FixtureAssert toMatchSnapshotExactlyWithStrictOrder(boolean regenerateFixture,
                                                               PropertyMatchers propertyMatchers) throws IOException {
        return toMatchSnapshot(MATCHES_EXACTLY_WITH_STRICT_ORDER, regenerateFixture, propertyMatchers);
    }

    public FixtureAssert toMatchSnapshotExactlyWithStrictOrder(PropertyMatchers propertyMatchers) throws IOException {
        return toMatchSnapshotExactlyWithStrictOrder(false, propertyMatchers);
    }

}