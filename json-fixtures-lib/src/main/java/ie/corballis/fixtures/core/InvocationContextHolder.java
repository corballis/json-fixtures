package ie.corballis.fixtures.core;

import java.util.concurrent.atomic.AtomicInteger;

public class InvocationContextHolder {

    private static ThreadLocal<InvocationContext> invocationContext = new ThreadLocal<>();

    public static void updateContext(String testMethodName) {
        InvocationContext previousInvocation = invocationContext.get();
        boolean executingNewTestcase = previousInvocation == null || !previousInvocation.name.equals(testMethodName);
        if (executingNewTestcase) {
            invocationContext.remove();
            invocationContext.set(new InvocationContext(testMethodName));
        } else {
            invocationContext.get().incrementInvocations();
        }
    }

    public static String currentSnapshotName() {
        return invocationContext.get().getName();
    }

    private static class InvocationContext {

        private String name;
        private AtomicInteger invocationCount = new AtomicInteger();

        public InvocationContext(String name) {
            this.name = name;
            incrementInvocations();
        }

        public void incrementInvocations() {
            invocationCount.incrementAndGet();
        }

        public String getName() {
            return name + "-" + invocationCount.get();
        }
    }

}
