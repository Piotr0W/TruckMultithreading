package TruckMultithreading;

public class Excavator extends Thread {
    private final TrucksQueue queue;
    private final int id;

    public Excavator(TrucksQueue queue, int id) {
        super("Excavator");
        this.queue = queue;
        this.id = id;
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            long c = Main.currentTime();
            if (c % 1440 < 780 || c % 1440 > 840) {
                queue.takeTruck(id);

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    interrupt();
                }
            }
        }
    }
}