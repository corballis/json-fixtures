package ie.corballis.fixtures.assertion.matcher;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;

import static ie.corballis.fixtures.settings.SettingsHolder.settings;

public class NestedObjectMatcher<T> extends BaseMatcher<T> {

    private final Class<T> nestedClass;
    private final Matcher<T> matcher;

    public NestedObjectMatcher(Matcher<T> matcher, Class<T> nestedClass) {
        this.nestedClass = nestedClass;
        this.matcher = matcher;
    }

    @Factory
    public static <T> Matcher<T> nested(Matcher<T> matcher, Class<T> nestedClass) {
        return new NestedObjectMatcher<T>(matcher, nestedClass);
    }

    @Override
    public boolean matches(Object item) {
        T actual = convertTo(item);
        return matcher.matches(actual);
    }

    private T convertTo(Object item) {
        T actual;
        try {
            actual = settings().getObjectMapper().convertValue(item, nestedClass);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Cannot convert actual value to [" +
                                               nestedClass.getSimpleName() +
                                               "] instance." +
                                               " Make sure that the nested entity is an instance of " +
                                               nestedClass.getSimpleName(), e);
        }
        return actual;
    }

    @Override
    public void describeTo(Description description) {
        matcher.describeTo(description);
    }

}
