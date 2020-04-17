package ie.corballis.fixtures.assertion.matcher;

import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.fest.assertions.api.Assertions.assertThat;

public class UuidMatcherTest {

    private UuidMatcher matcher;

    @Before
    public void setUp() throws Exception {
        matcher = new UuidMatcher();
    }

    @Test
    public void isUuidShouldMatch() {
        assertThat(matcher.matches("892d2942-4253-43dd-8c82-81fe6b2b9db5")).isTrue();
        assertThat(matcher.matches(UUID.randomUUID().toString())).isTrue();
    }

    @Test
    public void isUuidShouldNotMatch() {
        assertThat(matcher.matches("892d2942-4253")).isFalse();
        assertThat(matcher.matches("not a uuid")).isFalse();
        assertThat(matcher.matches(null)).isFalse();
    }

}
