package ie.corballis.fixtures.core;

public class InvocationContextHolder {

    private static ThreadLocal<InvocationContext> invocationContext = new InheritableThreadLocal<>();

    public static void updateContext(String testMethodName) {
        InvocationContext previousInvocation = initOrGet();

        boolean executingNewTestcase =
            previousInvocation.name == null || !previousInvocation.name.equals(testMethodName);
        if (executingNewTestcase) {
            previousInvocation.reset(testMethodName);
        } else {
            previousInvocation.incrementInvocations();
        }
    }

    private static InvocationContext initOrGet() {
        InvocationContext previousInvocation = invocationContext.get();
        if (previousInvocation == null) {
            previousInvocation = new InvocationContext();
            invocationContext.set(previousInvocation);
        }
        return previousInvocation;
    }

    public static void initTestExecutorThread(Thread testExecutorThread) {
        initOrGet().testExecutorThread = testExecutorThread;
    }

    public static Thread getTestExecutorThread() {
        return initOrGet().testExecutorThread;
    }

    public static String currentSnapshotName() {
        return initOrGet().getName();
    }

    private static class InvocationContext {

        private String name;
        private Thread testExecutorThread;
        private int invocationCount;

        private InvocationContext() {
            incrementInvocations();
        }

        private void incrementInvocations() {
            ++invocationCount;
        }

        private void reset(String testMethodName) {
            invocationCount = 1;
            name = testMethodName;
        }

        private String getName() {
            return name + "-" + invocationCount;
        }
    }

}
