package messenger.event;

public class Test {
    public static void main(String[] args) {
        TestThread testThread = new TestThread();

        testThread.start();
        try {
            System.out.println("going to sleep");
            Thread.sleep(10 * 1000);
            System.out.println("woke up from sleep and going to interrupt");
            testThread.interrupt();
            System.out.println("Interrupted");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
