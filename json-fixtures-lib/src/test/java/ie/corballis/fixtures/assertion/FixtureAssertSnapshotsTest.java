package ie.corballis.fixtures.assertion;

import org.apache.commons.io.FileUtils;
import org.fest.assertions.api.Assertions;
import org.junit.ComparisonFailure;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import ie.corballis.fixtures.annotation.Fixture;
import ie.corballis.fixtures.annotation.FixtureAnnotations;
import ie.corballis.fixtures.core.AsyncTester;
import ie.corballis.fixtures.core.BeanFactory;
import ie.corballis.fixtures.core.MyBean;
import ie.corballis.fixtures.io.scanner.FileFixtureScanner;
import ie.corballis.fixtures.io.write.SnapshotFixtureWriter;
import ie.corballis.fixtures.settings.Settings;

import static ie.corballis.fixtures.assertion.FixtureAssert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(MockitoJUnitRunner.class)
public class FixtureAssertSnapshotsTest {

    @Fixture({"fixture1", "fixture2", "fixture3"})
    private MyBean bean;

    @Fixture({"fixture1", "fixture2"})
    private MyBean bean2;

    @Mock
    private SnapshotFixtureWriter snapshotFixtureWriter;

    @Test
    public void toMatchSnapshotInNewThreadShouldFollowMethodInvocationIndex() throws Exception {
        init();

        BeanFactory beanFactory = new BeanFactory();
        beanFactory.init();

        assertThat(beanFactory.getFixtureAsJsonNode("toMatchSnapshotInNewThreadShouldFollowKeepMethodInvocationIndex-1"))
                .isNotNull();
        assertThat(beanFactory.getFixtureAsJsonNode("toMatchSnapshotInNewThreadShouldFollowKeepMethodInvocationIndex-2"))
                .isNotNull();

        assertThat(bean).toMatchSnapshot();

        AsyncTester asyncTester = new AsyncTester(() -> {
            try {
                assertThat(bean2).toMatchSnapshot();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        asyncTester.start();
        asyncTester.verifyNoErrors();
    }

    public void init() throws Exception {
        FixtureAnnotations.initFixtures(this);
    }

    @Test
    public void toMatchSnapshotShouldFailWhenExistingSnapshotChanged() throws Exception {
        init();

        assertThat(bean).toMatchSnapshot();

        try {
            bean.setIntProperty(8);
            assertThat(bean).toMatchSnapshot();
        } catch (ComparisonFailure e) {
            assertFailureMessage(e, "toMatchSnapshotShouldFailWhenExistingSnapshotChanged");
            return;
        }

        Assertions.fail("Fixture assertion should have been failed, ");
    }

    private void assertFailureMessage(ComparisonFailure e, String relativePath) throws URISyntaxException, IOException {
        URI uri = new FileFixtureScanner(getClass(), relativePath).collectResources().get(0).getURI();
        String expectedMessage = FileUtils.readFileToString(new File(uri));
        Assertions.assertThat(e.getMessage().replaceAll("\\r\\n", "\n"))
                .isEqualTo(expectedMessage.replaceAll("\\r\\n", "\n"));
    }

    @Test
    public void toMatchSnapshotShouldNotFailWhenRegenerateIsOn() throws Exception {
        Settings.Builder builder = new Settings.Builder();
        builder.setSnapshotFixtureWriter(snapshotFixtureWriter);
        FixtureAnnotations.initFixtures(this, builder);

        assertThat(bean).toMatchSnapshot();
        bean.setIntProperty(8);
        assertThat(bean).toMatchSnapshot(true);

        verify(snapshotFixtureWriter, times(1)).write(getClass(),
                "toMatchSnapshotShouldNotFailWhenRegenerateIsOn-2",
                bean);
        verifyNoMoreInteractions(snapshotFixtureWriter);
    }

    @Test
    public void toMatchSnapshotWithStrictOrderShouldFailWhenExistingSnapshotChanged() throws Exception {
        init();

        assertThat(bean).toMatchSnapshotWithStrictOrder();

        try {
            bean.getListProperty().remove("element1");
            bean.getListProperty().add("element1");
            assertThat(bean).toMatchSnapshotWithStrictOrder();
        } catch (ComparisonFailure e) {
            assertFailureMessage(e, "toMatchSnapshotWithStrictOrderShouldFailWhenExistingSnapshotChanged");
            return;
        }

        Assertions.fail("Fixture assertion should have been failed, ");
    }

    @Test
    public void toMatchSnapshotWithStrictOrderShouldNotFailWhenRegenerateIsOn() throws Exception {
        Settings.Builder builder = new Settings.Builder();
        builder.setSnapshotFixtureWriter(snapshotFixtureWriter);
        FixtureAnnotations.initFixtures(this, builder);

        assertThat(bean).toMatchSnapshotWithStrictOrder();
        bean.getListProperty().remove("element1");
        bean.getListProperty().add("element1");
        assertThat(bean).toMatchSnapshotWithStrictOrder(true);

        verify(snapshotFixtureWriter, times(1)).write(eq(getClass()),
                eq("toMatchSnapshotWithStrictOrderShouldNotFailWhenRegenerateIsOn-2"),
                eq(bean));
        verifyNoMoreInteractions(snapshotFixtureWriter);
    }

    @Test
    public void toMatchSnapshotExactlyShouldFailWhenExistingSnapshotChanged() throws Exception {
        init();

        assertThat(bean).toMatchSnapshotExactly();

        try {
            assertThat(bean2).toMatchSnapshotExactly();
        } catch (ComparisonFailure e) {
            assertFailureMessage(e, "toMatchSnapshotExactlyShouldFailWhenExistingSnapshotChanged");
            return;
        }

        Assertions.fail("Fixture assertion should have been failed");
    }

    @Test
    public void toMatchSnapshotExactlyShouldNotFailWhenRegenerateIsOn() throws Exception {
        Settings.Builder builder = new Settings.Builder();
        builder.setSnapshotFixtureWriter(snapshotFixtureWriter);
        FixtureAnnotations.initFixtures(this, builder);

        assertThat(bean).toMatchSnapshotWithStrictOrder();
        assertThat(bean2).toMatchSnapshotWithStrictOrder(true);

        verify(snapshotFixtureWriter, times(1)).write(eq(getClass()),
                eq("toMatchSnapshotExactlyShouldNotFailWhenRegenerateIsOn-2"),
                eq(bean2));
        verifyNoMoreInteractions(snapshotFixtureWriter);
    }

    @Test
    public void toMatchSnapshotExactlyWithStrictOrderShouldFailWhenExistingSnapshotChanged() throws Exception {
        init();

        assertThat(bean).toMatchSnapshotExactlyWithStrictOrder();

        try {
            assertThat(bean2).toMatchSnapshotExactlyWithStrictOrder();
        } catch (ComparisonFailure e) {
            assertFailureMessage(e, "toMatchSnapshotExactlyWithStrictOrderShouldFailWhenExistingSnapshotChanged");
            return;
        }

        Assertions.fail("Fixture assertion should have been failed");
    }

    @Test
    public void toMatchSnapshotExactlyWithStrictOrderShouldNotFailWhenRegenerateIsOn() throws Exception {
        Settings.Builder builder = new Settings.Builder();
        builder.setSnapshotFixtureWriter(snapshotFixtureWriter);
        FixtureAnnotations.initFixtures(this, builder);

        assertThat(bean).toMatchSnapshotWithStrictOrder();
        assertThat(bean2).toMatchSnapshotWithStrictOrder(true);

        verify(snapshotFixtureWriter, times(1)).write(eq(getClass()),
                eq("toMatchSnapshotExactlyWithStrictOrderShouldNotFailWhenRegenerateIsOn-2"),
                eq(bean2));
        verifyNoMoreInteractions(snapshotFixtureWriter);
    }

}
