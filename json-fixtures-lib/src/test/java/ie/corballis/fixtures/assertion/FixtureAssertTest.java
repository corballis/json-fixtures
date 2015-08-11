package ie.corballis.fixtures.assertion;

import ie.corballis.fixtures.annotation.Fixture;
import ie.corballis.fixtures.annotation.FixtureAnnotations;
import ie.corballis.fixtures.core.MyBean;
import org.apache.commons.io.FileUtils;
import org.fest.assertions.api.Assertions;
import org.junit.Before;
import org.junit.ComparisonFailure;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static com.google.common.collect.Lists.newArrayList;

public class FixtureAssertTest {

    @Fixture({"fixture1", "fixture2"})
    private MyBean bean;

    @Fixture({"fixture1", "fixture2", "fixture3"})
    private MyBean bean2;

    @Fixture({"fixture1", "fixture2", "fixture3", "fixture5"})
    private MyBean bean3;

    @Before
    public void setUp() throws Exception {
        FixtureAnnotations.initFixtures(this);
    }

    @Test
    public void matches_exactMatch() throws Exception {
        FixtureAssert.assertThat(bean).matches("fixture1", "fixture2");
    }

    @Test
    public void matches_allowsAnyOrdering() throws Exception {
        bean.setListProperty(newArrayList("element2", "element1", "element3"));
        FixtureAssert.assertThat(bean).matches("fixture1", "fixture2", "fixture3");
    }

    @Test
    public void matches_partialMatch() throws Exception {
        FixtureAssert.assertThat(bean).matches("fixture2");
    }

    @Test
    public void matches_mismatch() throws Exception {
        try {
            FixtureAssert.assertThat(bean).matches("fixture1");
            Assertions.fail("Should have failed");
        } catch (ComparisonFailure e) {
            System.out.println(e.getMessage());
            assertFailureMessage(e, "expectedMessage1");
        }
    }

    @Test
    public void matchesWithStrictOrder_exactMatch() throws Exception {
        FixtureAssert.assertThat(bean2).matchesWithStrictOrder("fixture1", "fixture2", "fixture3");
    }

    @Test
    public void matchesWithStrictOrder_doesNotAllowAnyOrdering() throws Exception {
        try {
            bean.setListProperty(newArrayList("element2", "element1", "element3"));
            FixtureAssert.assertThat(bean).matchesWithStrictOrder("fixture1", "fixture2", "fixture3");
        } catch (ComparisonFailure e) {
            assertFailureMessage(e, "expectedMessage2");
        }
    }

    @Test
    public void matchesWithStrictOrder_partialMatch() throws Exception {
        FixtureAssert.assertThat(bean2).matchesWithStrictOrder("fixture2");
    }

    @Test
    public void matchesExactly_mismatch() throws Exception {
        try {
            FixtureAssert.assertThat(bean).matchesExactly("fixture2");
            Assertions.fail("Should have failed");
        } catch (ComparisonFailure e) {
            assertFailureMessage(e, "expectedMessage3");
        }
    }

    @Test
    public void matchesExactly_allowsAnyOrdering() throws Exception {
        bean3.setListProperty(newArrayList("element2", "element1", "element3"));
        FixtureAssert.assertThat(bean3).matchesExactly("fixture1", "fixture2", "fixture3", "fixture5");
    }

    @Test
    public void matchesExactly_matches() throws Exception {
        FixtureAssert.assertThat(bean3).matchesExactly("fixture1", "fixture2", "fixture3", "fixture5");
    }

    @Test
    public void matchesExactlyWithStrictOrder_mismatch() throws Exception {
        try {
            FixtureAssert.assertThat(bean).matchesExactlyWithStrictOrder("fixture2");
            Assertions.fail("Should have failed");
        } catch (ComparisonFailure e) {
            assertFailureMessage(e, "expectedMessage3");
        }
    }

    @Test
    public void matchesExactlyWithStrictOrder_doesNotAllowAnyOrdering() throws URISyntaxException, IOException {
        try {
            bean3.setListProperty(newArrayList("element2", "element1", "element3"));
            FixtureAssert.assertThat(bean3)
                         .matchesExactlyWithStrictOrder("fixture1", "fixture2", "fixture3", "fixture5");
        } catch (ComparisonFailure e) {
            assertFailureMessage(e, "expectedMessage4");
        }
    }

    @Test
    public void matchesExactlyWithStrictOrder_matches() throws Exception {
        FixtureAssert.assertThat(bean3).matchesExactlyWithStrictOrder("fixture1", "fixture2", "fixture3", "fixture5");
    }

    private void assertFailureMessage(ComparisonFailure e, String relativePath) throws URISyntaxException,
                                                                                        IOException {
        URI uri = getClass().getClassLoader().getResource(relativePath).toURI();
        String expectedMessage = FileUtils.readFileToString(new File(uri));
        Assertions.assertThat(e.getMessage()).isEqualTo(expectedMessage);
    }
}