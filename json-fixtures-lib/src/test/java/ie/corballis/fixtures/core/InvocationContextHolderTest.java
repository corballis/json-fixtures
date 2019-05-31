package ie.corballis.fixtures.core;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class InvocationContextHolderTest {

    @Test
    public void shouldIncrementMethodCallsAndResetOnMethodChange() {
        InvocationContextHolder.updateContext("myMethod");
        assertThat(InvocationContextHolder.currentSnapshotName()).isEqualTo("myMethod-1");
        InvocationContextHolder.updateContext("myMethod");
        assertThat(InvocationContextHolder.currentSnapshotName()).isEqualTo("myMethod-2");
        InvocationContextHolder.updateContext("myMethod");
        assertThat(InvocationContextHolder.currentSnapshotName()).isEqualTo("myMethod-3");
        InvocationContextHolder.updateContext("myNewMethod");
        assertThat(InvocationContextHolder.currentSnapshotName()).isEqualTo("myNewMethod-1");
        InvocationContextHolder.updateContext("myNewMethod");
        assertThat(InvocationContextHolder.currentSnapshotName()).isEqualTo("myNewMethod-2");
        InvocationContextHolder.updateContext("myMethod");
        assertThat(InvocationContextHolder.currentSnapshotName()).isEqualTo("myMethod-1");
    }

    @Test
    public void shouldUseUpdateContextInEachThread() throws InterruptedException {
        InvocationContextHolder.updateContext("myOtherMethod");
        assertThat(InvocationContextHolder.currentSnapshotName()).isEqualTo("myOtherMethod-1");

        AsyncTester asyncTester = new AsyncTester(() -> {
            InvocationContextHolder.updateContext("myOtherMethod");
            assertThat(InvocationContextHolder.currentSnapshotName()).isEqualTo("myOtherMethod-2");
        });
        asyncTester.start();
        asyncTester.verifyNoErrors();
        assertThat(InvocationContextHolder.currentSnapshotName()).isEqualTo("myOtherMethod-2");
    }
}