package ie.corballis.fixtures.assertion.matcher;

import org.junit.Test;

import java.time.ZonedDateTime;

import static org.fest.assertions.api.Assertions.assertThat;

public class ZonedDateTimeEqualsInUtcMatcherTest {

    private ZonedDateTimeEqualsInUtcMatcher matcher;

    @Test
    public void shouldCompareInUtc() {
        matcher = new ZonedDateTimeEqualsInUtcMatcher(ZonedDateTime.parse("2020-04-17T08:06:03+02:00"));
        assertThat(matcher.matches(ZonedDateTime.parse("2020-04-17T06:06:03Z"))).isTrue();
        assertThat(matcher.matches(ZonedDateTime.parse("2020-04-17T09:06:03+03:00"))).isTrue();
        assertThat(new ZonedDateTimeEqualsInUtcMatcher(null).matches(null)).isTrue();
    }

    @Test
    public void shouldNotMatch() {
        matcher = new ZonedDateTimeEqualsInUtcMatcher(ZonedDateTime.parse("2020-04-17T08:06:03+02:00"));
        assertThat(matcher.matches(ZonedDateTime.parse("2020-04-17T06:07:03Z"))).isFalse();
        assertThat(matcher.matches(ZonedDateTime.parse("2020-04-17T10:06:03+03:00"))).isFalse();
        assertThat(matcher.matches(ZonedDateTime.parse("2021-04-17T06:06:03Z"))).isFalse();
        assertThat(matcher.matches(ZonedDateTime.parse("2020-03-17T06:06:03Z"))).isFalse();
        assertThat(matcher.matches(ZonedDateTime.parse("2020-04-14T06:06:03Z"))).isFalse();
        assertThat(matcher.matches(null)).isFalse();
    }

    @Test
    public void shouldMatchInMillisecondPrecision() {
        matcher = new ZonedDateTimeEqualsInUtcMatcher(ZonedDateTime.parse("2020-04-17T06:18:28.956087Z"));
        assertThat(matcher.matches(ZonedDateTime.parse("2020-04-17T06:18:28.957087Z"))).isFalse();
        assertThat(matcher.matches(ZonedDateTime.parse("2020-04-17T06:18:28.956999Z"))).isTrue();
    }

}
