package ie.corballis.fixtures.assertion.matcher;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import static java.time.ZoneOffset.UTC;

public class ZonedDateTimeEqualsInUtcMatcher extends BaseMatcher<ZonedDateTime> {

    private final LocalDateTime expected;

    public ZonedDateTimeEqualsInUtcMatcher(ZonedDateTime expected) {
        this.expected = toUtcLocal(expected);
    }

    private LocalDateTime toUtcLocal(ZonedDateTime item) {
        return item == null ? null : LocalDateTime.ofInstant(item.toInstant(), UTC).truncatedTo(ChronoUnit.MILLIS);
    }

    @Override
    public boolean matches(Object item) {
        ZonedDateTime actualWithZone = item == null ? null : ZonedDateTime.parse(String.valueOf(item));
        LocalDateTime actual = toUtcLocal(actualWithZone);
        return Objects.equals(actual, expected);
    }

    @Override
    public void describeTo(Description description) {
        description.appendValue(expected);
    }

    public static Matcher<ZonedDateTime> zonedDateTimeEquals(ZonedDateTime expected) {
        return new ZonedDateTimeEqualsInUtcMatcher(expected);
    }

}
