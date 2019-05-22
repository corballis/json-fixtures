package ie.corballis.fixtures.assertion;

import ie.corballis.fixtures.annotation.Fixture;
import ie.corballis.fixtures.annotation.FixtureAnnotations;
import ie.corballis.fixtures.core.MyBean;
import ie.corballis.fixtures.io.write.SnapshotFixtureWriter;
import ie.corballis.fixtures.settings.Settings;
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

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class FixtureAssertSnapshotsTest {

    @Fixture({"fixture1", "fixture2", "fixture3"})
    private MyBean bean;

    @Fixture({"fixture1", "fixture2"})
    private MyBean bean2;

    @Mock
    private SnapshotFixtureWriter snapshotFixtureWriter;

    @Test
    public void toMatchSnapshotShouldFailWhenExistingSnapshotChanged() throws Exception {
        init();

        FixtureAssert.assertThat(bean).toMatchSnapshot();

        try {
            bean.setIntProperty(8);
            FixtureAssert.assertThat(bean).toMatchSnapshot();
        } catch (ComparisonFailure e) {
            assertFailureMessage(e, "toMatchSnapshotShouldFailWhenExistingSnapshotChanged");
            return;
        }

        Assertions.fail("Fixture assertion should have been failed, ");
    }

    public void init() throws Exception {
        FixtureAnnotations.initFixtures(this, createSettings());
    }

    private Settings.Builder createSettings() {
        return new Settings.Builder();
    }

    private void assertFailureMessage(ComparisonFailure e, String relativePath) throws URISyntaxException, IOException {
        URI uri = getClass().getClassLoader().getResource(relativePath).toURI();
        String expectedMessage = FileUtils.readFileToString(new File(uri));
        Assertions.assertThat(e.getMessage().replaceAll("\\r\\n", "\n"))
                  .isEqualTo(expectedMessage.replaceAll("\\r\\n", "\n"));
    }

    @Test
    public void toMatchSnapshotShouldNotFailWhenRegenerateIsOn() throws Exception {
        Settings.Builder builder = new Settings.Builder();
        builder.setSnapshotFixtureWriter(snapshotFixtureWriter);
        FixtureAnnotations.initFixtures(this, builder);

        FixtureAssert.assertThat(bean).toMatchSnapshot();
        bean.setIntProperty(8);
        FixtureAssert.assertThat(bean).toMatchSnapshot(true);

        verify(snapshotFixtureWriter, times(1)).write(getClass(),
                                                      "toMatchSnapshotShouldNotFailWhenRegenerateIsOn-2",
                                                      bean);
        verifyNoMoreInteractions(snapshotFixtureWriter);
    }

    @Test
    public void toMatchSnapshotWithStrictOrderShouldFailWhenExistingSnapshotChanged() throws Exception {
        init();

        FixtureAssert.assertThat(bean).toMatchSnapshotWithStrictOrder();

        try {
            bean.getListProperty().remove("element1");
            bean.getListProperty().add("element1");
            FixtureAssert.assertThat(bean).toMatchSnapshotWithStrictOrder();
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

        FixtureAssert.assertThat(bean).toMatchSnapshotWithStrictOrder();
        bean.getListProperty().remove("element1");
        bean.getListProperty().add("element1");
        FixtureAssert.assertThat(bean).toMatchSnapshotWithStrictOrder(true);

        verify(snapshotFixtureWriter, times(1)).write(eq(getClass()),
                                                      eq("toMatchSnapshotWithStrictOrderShouldNotFailWhenRegenerateIsOn-2"),
                                                      eq(bean));
        verifyNoMoreInteractions(snapshotFixtureWriter);
    }

    @Test
    public void toMatchSnapshotExactlyShouldFailWhenExistingSnapshotChanged() throws Exception {
        init();

        FixtureAssert.assertThat(bean).toMatchSnapshotExactly();

        try {
            FixtureAssert.assertThat(bean2).toMatchSnapshotExactly();
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

        FixtureAssert.assertThat(bean).toMatchSnapshotWithStrictOrder();
        FixtureAssert.assertThat(bean2).toMatchSnapshotWithStrictOrder(true);

        verify(snapshotFixtureWriter, times(1)).write(eq(getClass()),
                                                      eq("toMatchSnapshotExactlyShouldNotFailWhenRegenerateIsOn-2"),
                                                      eq(bean2));
        verifyNoMoreInteractions(snapshotFixtureWriter);
    }

    @Test
    public void toMatchSnapshotExactlyWithStrictOrderShouldFailWhenExistingSnapshotChanged() throws Exception {
        init();

        FixtureAssert.assertThat(bean).toMatchSnapshotExactlyWithStrictOrder();

        try {
            FixtureAssert.assertThat(bean2).toMatchSnapshotExactlyWithStrictOrder();
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

        FixtureAssert.assertThat(bean).toMatchSnapshotWithStrictOrder();
        FixtureAssert.assertThat(bean2).toMatchSnapshotWithStrictOrder(true);

        verify(snapshotFixtureWriter, times(1)).write(eq(getClass()),
                                                      eq("toMatchSnapshotExactlyWithStrictOrderShouldNotFailWhenRegenerateIsOn-2"),
                                                      eq(bean2));
        verifyNoMoreInteractions(snapshotFixtureWriter);
    }

}