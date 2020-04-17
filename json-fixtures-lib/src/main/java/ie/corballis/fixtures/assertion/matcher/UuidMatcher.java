package ie.corballis.fixtures.assertion.matcher;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import java.util.UUID;

public class UuidMatcher extends BaseMatcher<String> {

    @Override
    public boolean matches(Object item) {
        try {
            UUID.fromString(String.valueOf(item));
        } catch (IllegalArgumentException exception) {
            return false;
        }

        return true;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("UUID value");
    }

    public static Matcher<String> isUuid() {
        return new UuidMatcher();
    }

}
