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
    public void shouldUseDifferentContextInEachThread() throws InterruptedException {
        Thread t1 = new Thread() {
            @Override
            public void run() {
                InvocationContextHolder.updateContext("myMethod");

                Thread t2 = new Thread() {
                    @Override
                    public void run() {
                        InvocationContextHolder.updateContext("myMethod");
                        assertThat(InvocationContextHolder.currentSnapshotName()).isEqualTo("myMethod-1");
                    }
                };
                t2.start();
                try {
                    t2.join();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                assertThat(InvocationContextHolder.currentSnapshotName()).isEqualTo("myMethod-1");
            }
        };
        t1.start();
        t1.join();
    }
}