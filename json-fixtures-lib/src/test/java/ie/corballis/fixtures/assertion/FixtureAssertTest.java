package ie.corballis.fixtures.assertion;

import ie.corballis.fixtures.annotation.Fixture;
import ie.corballis.fixtures.annotation.FixtureAnnotations;
import ie.corballis.fixtures.core.MyBean;
import org.fest.assertions.api.Assertions;
import org.junit.Before;
import org.junit.Test;

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
        } catch (AssertionError e) {
            Assertions.assertThat(e.getMessage()).isEqualTo(
                    "\n" +
                            "Expected: \"{\\\"stringProperty\\\":\\\"property\\\",\\\"intProperty\\\":1}\"\n" +
                            "     but: field stringProperty was \"property2\" instead of \"property\"");
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
        } catch (AssertionError e) {
            Assertions.assertThat(e.getMessage()).isEqualTo(
                    "\n" +
                            "Expected: \"{\\\"stringProperty\\\":\\\"property2\\\",\\\"intProperty\\\":1,\\\"listProperty\\\":[\\\"element1\\\",\\\"element2\\\",\\\"element3\\\"]}\"\n" +
                            "     but: field listProperty[0] was \"element2\" instead of \"element1\" and field listProperty[1] was \"element1\" instead of \"element2\"");
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
        } catch (AssertionError e) {
            Assertions.assertThat(e.getMessage()).isEqualTo(
                    "\n" +
                            "Expected: \"{\\\"stringProperty\\\":\\\"property2\\\"}\"\n" +
                            "     but: \n" +
                            "Unexpected: intProperty\n" +
                            " ; \n" +
                            "Unexpected: listProperty\n" +
                            " ; \n" +
                            "Unexpected: nested\n");
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
        } catch (AssertionError e) {
            Assertions.assertThat(e.getMessage()).isEqualTo(
                    "\n" +
                            "Expected: \"{\\\"stringProperty\\\":\\\"property2\\\"}\"\n" +
                            "     but: \n" +
                            "Unexpected: intProperty\n" +
                            " ; \n" +
                            "Unexpected: listProperty\n" +
                            " ; \n" +
                            "Unexpected: nested\n");
        }
    }

    @Test
    public void matchesExactlyWithStrictOrder_doesNotAllowAnyOrdering() throws Exception {
        try {
            bean3.setListProperty(newArrayList("element2", "element1", "element3"));
            FixtureAssert.assertThat(bean3).matchesExactlyWithStrictOrder("fixture1", "fixture2", "fixture3", "fixture5");
        } catch (AssertionError e) {
            Assertions.assertThat(e.getMessage()).isEqualTo(
                    "\n" +
                            "Expected: \"{\\\"stringProperty\\\":\\\"property2\\\",\\\"intProperty\\\":1,\\\"listProperty\\\":[\\\"element1\\\",\\\"element2\\\",\\\"element3\\\"],\\\"nested\\\":{\\\"prop1\\\":\\\"value\\\"}}\"\n" +
                            "     but: field listProperty[0] was \"element2\" instead of \"element1\" and field listProperty[1] was \"element1\" instead of \"element2\"");
        }
    }

    @Test
    public void matchesExactlyWithStrictOrder_matches() throws Exception {
        FixtureAssert.assertThat(bean3).matchesExactlyWithStrictOrder("fixture1", "fixture2", "fixture3", "fixture5");
    }
}
