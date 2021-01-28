package TruckMultithreading;

import java.util.Iterator;
import java.util.concurrent.LinkedBlockingQueue;

public class TrucksQueue {
    private final LinkedBlockingQueue<Truck> queue;

    public TrucksQueue(int size) {
        queue = new LinkedBlockingQueue<>(size);
    }

    public synchronized void addTruck(Truck truck) {
        try {
            queue.put(truck);
            Main.updateQueue();
            notifyAll();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public synchronized void takeTruck(int id) {
        while (queue.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        Truck[] trucks = new Truck[2];
        trucks[0] = queue.poll();
        if (!trucks[0].isBig()) {
            trucks[1] = queue.peek();
            if (trucks[1] != null) {
                if (!trucks[1].isBig()) {
                    queue.poll();
                } else {
                    trucks[1] = null;
                }
            }
        }

        Main.updateQueue();
        Main.updateExcavator(id, trucks);

        notifyAll();
    }

    public synchronized boolean handleTimeout(Truck truck) {
        return queue.remove(truck);
    }

    public synchronized Iterator<Truck> getIterator() {
        return queue.iterator();
    }
}
