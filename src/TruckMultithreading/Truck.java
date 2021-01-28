package TruckMultithreading;

public class Truck {
    private final int id;
    private final long timeout;
    private final boolean big;
    private final TrucksQueue queue;

    public Truck(int id, boolean big, TrucksQueue queue) {
        this.id = id;
        this.big = big;
        this.queue = queue;
        this.timeout = Main.currentTime() + 30;
        Main.addTimeout(timeout, this);
    }

    public void leave() {
        if (queue.handleTimeout(this)) {
            Main.updateQueue();
        }
    }

    public boolean isBig() {
        return big;
    }

    public long getId() {
        return id;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        if (big) {
            s.insert(0, "<font color=red>");
        } else {
            s.insert(0, "<font color=white>");
        }
        s.append(id).append("</font>");

        if (timeout - Main.currentTime() < 15) {
            s.insert(0, "<span style='text-decoration: line-through;'>").append("</span>");
        }

        return s.toString();
    }
}