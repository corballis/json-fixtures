package ie.corballis.fixtures.assertion;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import ie.corballis.fixtures.annotation.Fixture;
import ie.corballis.fixtures.annotation.FixtureAnnotations;
import ie.corballis.fixtures.references.Entity;
import ie.corballis.fixtures.references.Owner;
import ie.corballis.fixtures.references.Person;
import ie.corballis.fixtures.settings.Settings;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.core.IsAnything;
import org.hamcrest.text.IsEmptyString;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static ie.corballis.fixtures.assertion.PropertyMatchers.overriddenMatchers;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

public class PropertyMatchersTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    @Fixture
    private Person person1;
    @Fixture
    private Owner ownerForMatchers;
    @Fixture
    private Map testMap;

    @Before
    public void setUp() throws Exception {
        ObjectMapper objectMapper = Settings.Builder.defaultObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        FixtureAnnotations.initFixtures(this, new Settings.Builder().setObjectMapper(objectMapper));
    }

    @Test
    public void multipleNestedProperties() throws IOException {
        FixtureAssert.assertThat(testMap)
                     .matchesExactlyWithStrictOrder(overriddenMatchers("b.a", anything(), "x.a.b", equalTo(1)),
                                                    "testMapExpected");
    }

    @Test
    public void toMatchSnapshotShouldNotWriteOverriddenProperties() throws IOException {
        FixtureAssert.assertThat(person1).toMatchSnapshotExactlyWithStrictOrder(overriddenMatchers("age", anything()));
    }

    @Test
    public void customMatcherShouldBeAccepted() throws Exception {
        AtomicBoolean matcherCalled = new AtomicBoolean();

        FixtureAssert.assertThat(new Entity(10, LocalDateTime.now()))
                     .matchesExactly(overriddenMatchers("createdAt", new BaseMatcher<LocalDateTime>() {

                         @Override
                         public boolean matches(Object item) {
                             matcherCalled.set(true);
                             LocalDateTime actual = LocalDateTime.parse(String.valueOf(item));

                             LocalDateTime before = actual.minusMinutes(1);
                             LocalDateTime after = actual.plusMinutes(1);
                             return actual.isBefore(after) && actual.isAfter(before);
                         }

                         @Override
                         public void describeTo(Description description) {
                         }
                     }), "customMatcherShouldBeAccepted-result");

        assertThat(matcherCalled.get()).isTrue();
    }

    @Test
    public void shouldFailWhenMatcherFails() throws Exception {
        expectedException.expectMessage("Property 'age' did not match with the PropertyMatcher you provided.");
        expectedException.expectMessage("Expected: <20>");
        expectedException.expectMessage("but: was <1>");
        FixtureAssert.assertThat(person1).matchesExactly(overriddenMatchers("age", equalTo(20)), "person1");
    }

    @Test
    public void shouldNotFailWhenMatcherDoesNotExistForProperty() throws Exception {
        FixtureAssert.assertThat(person1).matchesExactly(overriddenMatchers("dog.x", equalTo(20)), "person1");
    }

    @Test
    public void propertyMatcherArgumentsShouldAcceptVarArgs() {
        PropertyMatchers matchers = overriddenMatchers("x", anything());
        assertThat(matchers.getProperties()).containsExactly("x");
        assertThat(matchers.getMatcher("x")).isExactlyInstanceOf(IsAnything.class);

        matchers = overriddenMatchers("x", anything(), "y", isEmptyString());
        assertThat(matchers.getProperties()).containsOnly("x", "y");
        assertThat(matchers.getMatcher("x")).isExactlyInstanceOf(IsAnything.class);
        assertThat(matchers.getMatcher("y")).isExactlyInstanceOf(IsEmptyString.class);

        matchers = overriddenMatchers("x", anything(), "y", isEmptyString(), "z", anything());
        assertThat(matchers.getProperties()).containsOnly("x", "y", "z");
        assertThat(matchers.getMatcher("x")).isExactlyInstanceOf(IsAnything.class);
        assertThat(matchers.getMatcher("y")).isExactlyInstanceOf(IsEmptyString.class);
        assertThat(matchers.getMatcher("z")).isExactlyInstanceOf(IsAnything.class);

        matchers = overriddenMatchers("x", anything(), "y", isEmptyString(), "z", anything(), "a", isEmptyString());
        assertThat(matchers.getProperties()).containsOnly("x", "y", "z", "a");
        assertThat(matchers.getMatcher("x")).isExactlyInstanceOf(IsAnything.class);
        assertThat(matchers.getMatcher("y")).isExactlyInstanceOf(IsEmptyString.class);
        assertThat(matchers.getMatcher("z")).isExactlyInstanceOf(IsAnything.class);
        assertThat(matchers.getMatcher("a")).isExactlyInstanceOf(IsEmptyString.class);

        matchers = overriddenMatchers("x",
                                      anything(),
                                      "y",
                                      isEmptyString(),
                                      "z",
                                      anything(),
                                      "a",
                                      isEmptyString(),
                                      "b",
                                      isEmptyString());
        assertThat(matchers.getProperties()).containsOnly("x", "y", "z", "a", "b");
        assertThat(matchers.getMatcher("x")).isExactlyInstanceOf(IsAnything.class);
        assertThat(matchers.getMatcher("y")).isExactlyInstanceOf(IsEmptyString.class);
        assertThat(matchers.getMatcher("z")).isExactlyInstanceOf(IsAnything.class);
        assertThat(matchers.getMatcher("a")).isExactlyInstanceOf(IsEmptyString.class);
        assertThat(matchers.getMatcher("b")).isExactlyInstanceOf(IsEmptyString.class);

        matchers = overriddenMatchers("x",
                                      anything(),
                                      "y",
                                      isEmptyString(),
                                      "z",
                                      anything(),
                                      "a",
                                      isEmptyString(),
                                      "b",
                                      isEmptyString(),
                                      "c",
                                      anything());
        assertThat(matchers.getProperties()).containsOnly("x", "y", "z", "a", "b", "c");
        assertThat(matchers.getMatcher("x")).isExactlyInstanceOf(IsAnything.class);
        assertThat(matchers.getMatcher("y")).isExactlyInstanceOf(IsEmptyString.class);
        assertThat(matchers.getMatcher("z")).isExactlyInstanceOf(IsAnything.class);
        assertThat(matchers.getMatcher("a")).isExactlyInstanceOf(IsEmptyString.class);
        assertThat(matchers.getMatcher("b")).isExactlyInstanceOf(IsEmptyString.class);
        assertThat(matchers.getMatcher("c")).isExactlyInstanceOf(IsAnything.class);

    }

    @Test
    public void propertyMatcherArgumentsShouldBeValid() {
        expectedException.expectMessage(
            "Matchers are not defined correctly, you must set the matchers after the property definition. " +
            "e.g.: \"id\", Matchers.any(), \"createdAt\", Matchers.any()");

        overriddenMatchers("x",
                           anything(),
                           "y",
                           isEmptyString(),
                           "z",
                           anything(),
                           "a",
                           isEmptyString(),
                           "b",
                           isEmptyString(),
                           anything(),
                           "c");
    }

    @Test
    public void matchesShouldAcceptMatchers() throws Exception {
        FixtureAssert.assertThat(person1)
                     .matches(overriddenMatchers("age", equalTo(1)), "matchesShouldAcceptMatchers-1");
    }

    @Test
    public void matchesWithStrictOrderShouldAcceptMatchers() throws JsonProcessingException {
        FixtureAssert.assertThat(testMap)
                     .matchesWithStrictOrder(overriddenMatchers("a", equalTo("a1")),
                                             "matchesWithStrictOrderShouldAcceptMatchers-result");
    }

    @Test
    public void toMatchSnapshotShouldAcceptMatchers() throws IOException {
        FixtureAssert.assertThat(person1).toMatchSnapshot(overriddenMatchers("age", equalTo(1)));
    }

    @Test
    public void toMatchSnapshotWithStrictOrderShouldAcceptMatchers() throws IOException {
        FixtureAssert.assertThat(testMap).toMatchSnapshotWithStrictOrder(overriddenMatchers("a", equalTo("a1")));
    }
}