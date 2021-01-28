package TruckMultithreading;

public class Dispatcher extends Thread {
    private static int nextTruckId = 0;
    private final boolean big;
    private final TrucksQueue queue;
    private int trucksToSend;

    public Dispatcher(TrucksQueue queue, boolean big, int trucksToSend) {
        super("TrucksSender");
        this.big = big;
        this.trucksToSend = trucksToSend;
        this.queue = queue;
    }

    public static void resetNextId() {
        nextTruckId = 0;
    }

    @Override
    public void run() {
        while (!isInterrupted() && trucksToSend > 0) {
            trucksToSend--;
            queue.addTruck(new Truck(nextTruckId++, big, queue));

            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                interrupt();
            }
        }
    }
}
