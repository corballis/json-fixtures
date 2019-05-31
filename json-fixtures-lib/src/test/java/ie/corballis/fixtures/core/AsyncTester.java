package ie.corballis.fixtures.core;

public class AsyncTester {

    private Thread thread;
    private AssertionError error;

    public AsyncTester(final Runnable runnable) {
        thread = new Thread(() -> {
            try {
                runnable.run();
            } catch (AssertionError e) {
                error = e;
            }
        });
    }

    public void start() {
        thread.start();
    }

    public void verifyNoErrors() throws InterruptedException {
        thread.join();
        if (error != null) {
            throw error;
        }
    }
}