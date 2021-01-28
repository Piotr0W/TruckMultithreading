package TruckMultithreading;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicLong;

public class Time extends Thread {
    private final AtomicLong currentTime = new AtomicLong(720);
    private final HashMap<Long, ArrayList<Truck>> timeouts = new HashMap<>();

    public Time() {
        super("Time");
    }

    public void addTimeout(Long time, Truck truck) {
        if (!timeouts.containsKey(time)) {
            ArrayList<Truck> newTimeouts = new ArrayList<>();
            newTimeouts.add(truck);
            timeouts.put(time, newTimeouts);
        } else {
            timeouts.get(time).add(truck);
        }
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            long c = currentTime.incrementAndGet();
            Main.updateTime(c);

            if (timeouts.containsKey(c)) {
                for (Truck s : timeouts.get(c)) {
                    s.leave();
                }
                timeouts.remove(c);
            }

            try {
                Thread.sleep(333);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public long getCurrentTime() {
        return currentTime.get();
    }
}
