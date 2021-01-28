package TruckMultithreading;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;

public class Main extends JFrame {
    private static final JLabel timer = new JLabel("00:00");
    private static final JTextPane queueLabel = new JTextPane();
    private static final JTextField excavatorsAmount = new JTextField("5");
    private static final JTextField smallTrucksAmount = new JTextField("100");
    private static final JTextField bigTrucksAmount = new JTextField("60");
    private static final JButton startButton = new JButton("Start");
    private static final JButton stopButton = new JButton("Stop");
    private static final String[] excavatorsColors = {
            "#ffffff", "#eeeeee", "#dddddd", "#cccccc", "#bbbbbb", "#aaaaaa", "#999999", "#888888"
    };
    private static JTextPane[] excavatorsLabel;
    private static Time time;
    private static TrucksQueue queue;
    private static Dispatcher smallTrucksDispatcher, bigTrucksDispatcher;
    private static Excavator[] excavators;
    private final ActionListener startListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            int smallTrucks = Integer.parseInt(smallTrucksAmount.getText());
            int bigTrucks = Integer.parseInt(bigTrucksAmount.getText());
            excavators = new Excavator[Integer.parseInt(excavatorsAmount.getText())];

            Dispatcher.resetNextId();

            startButton.setEnabled(false);
            stopButton.setEnabled(true);

            if (excavatorsLabel != null) {
                for (JTextPane text : excavatorsLabel) {
                    text.getParent().remove(text);
                }
            }
            repaint();

            int height = Math.min(Math.max(500 / excavators.length - 10, 20), 100);
            int yOffset = 300 - (excavators.length * (height + 5)) / 2;

            excavatorsLabel = new JTextPane[excavators.length];
            for (int i = 0; i < excavators.length; i++) {
                excavatorsLabel[i] = new JTextPane();
                excavatorsLabel[i].setBackground(Color.decode(excavatorsColors[i % excavatorsColors.length]));
                excavatorsLabel[i].setLocation(10, yOffset + (i * (height + 5)));
                excavatorsLabel[i].setSize(200, height);
                excavatorsLabel[i].setEditable(false);
                add(excavatorsLabel[i]);
            }

            time = new Time();
            time.start();

            queue = new TrucksQueue(smallTrucks + bigTrucks);

            smallTrucksDispatcher = new Dispatcher(queue, false, smallTrucks);
            bigTrucksDispatcher = new Dispatcher(queue, true, bigTrucks);

            smallTrucksDispatcher.start();
            bigTrucksDispatcher.start();

            for (int i = 0; i < excavators.length; i++) {
                excavators[i] = new Excavator(queue, i);
                excavators[i].start();
            }
        }
    };
    private final ActionListener stopListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            startButton.setEnabled(true);
            stopButton.setEnabled(false);

            for (Excavator excavator : excavators) {
                excavator.interrupt();
            }

            smallTrucksDispatcher.interrupt();
            bigTrucksDispatcher.interrupt();

            time.interrupt();
        }
    };

    public Main() {
        setTitle("Koparki");
        setSize(1000, 600);
        setResizable(false);
        setVisible(true);
        setLayout(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setBackground(Color.DARK_GRAY);

        Border border = BorderFactory.createLineBorder(Color.WHITE);
        Color inputBackground = Color.decode("#333333");

        JLabel excavatorsLabel = new JLabel("Koparki:");
        excavatorsLabel.setLocation(10, 10);
        excavatorsLabel.setSize(50, 30);
        excavatorsLabel.setForeground(Color.WHITE);

        excavatorsAmount.setLocation(70, 10);
        excavatorsAmount.setSize(40, 30);
        excavatorsAmount.setBackground(inputBackground);
        excavatorsAmount.setBorder(border);
        excavatorsAmount.setForeground(Color.WHITE);
        excavatorsAmount.setHorizontalAlignment(JTextField.CENTER);

        JLabel smallTrucksLabel = new JLabel("Małe samochody:");
        smallTrucksLabel.setLocation(140, 10);
        smallTrucksLabel.setSize(100, 30);
        smallTrucksLabel.setForeground(Color.WHITE);

        smallTrucksAmount.setLocation(250, 10);
        smallTrucksAmount.setSize(40, 30);
        smallTrucksAmount.setBackground(inputBackground);
        smallTrucksAmount.setBorder(border);
        smallTrucksAmount.setForeground(Color.WHITE);
        smallTrucksAmount.setHorizontalAlignment(JTextField.CENTER);

        JLabel bigTrucksLabel = new JLabel("Duże samochody:");
        bigTrucksLabel.setLocation(320, 10);
        bigTrucksLabel.setSize(100, 30);
        bigTrucksLabel.setForeground(Color.WHITE);

        bigTrucksAmount.setLocation(430, 10);
        bigTrucksAmount.setSize(40, 30);
        bigTrucksAmount.setBackground(inputBackground);
        bigTrucksAmount.setBorder(border);
        bigTrucksAmount.setForeground(Color.WHITE);
        bigTrucksAmount.setHorizontalAlignment(JTextField.CENTER);

        timer.setLocation(940, 10);
        timer.setSize(50, 30);
        timer.setForeground(Color.WHITE);

        startButton.setLocation(720, 10);
        startButton.setSize(99, 30);
        startButton.addActionListener(startListener);

        stopButton.setLocation(820, 10);
        stopButton.setSize(99, 30);
        stopButton.setEnabled(false);
        stopButton.addActionListener(stopListener);

        queueLabel.setContentType("text/html");
        queueLabel.setLocation(250, 170);
        queueLabel.setSize(700, 200);
        queueLabel.setBackground(inputBackground);

        add(excavatorsLabel);
        add(excavatorsAmount);
        add(smallTrucksLabel);
        add(smallTrucksAmount);
        add(bigTrucksLabel);
        add(bigTrucksAmount);
        add(timer);
        add(startButton);
        add(stopButton);
        add(queueLabel);

        repaint();
    }

    public static void main(String[] args) {
        new Main();
    }

    public synchronized static void updateQueue() {
        StringBuilder queueText = new StringBuilder();
        queueText.append("<html><body style='width: 700px; height: 200px;'>");
        Iterator<Truck> iterator = queue.getIterator();
        while (iterator.hasNext()) {
            queueText.append(iterator.next().toString()).append(" ");
        }
        queueLabel.setText(queueText.append("</body></html>").toString());
    }

    public synchronized static void updateExcavator(int id, Truck[] trucks) {
        StringBuilder text = new StringBuilder("[");
        text.append(trucks[0].getId()).append((trucks[0].isBig() ? ", duży]" : ", mały]"));
        if (trucks.length > 1 && trucks[1] != null) {
            text.append(", [").append(trucks[1].getId()).append((trucks[1].isBig() ? ", duży]" : ", mały]"));
        }
        excavatorsLabel[id].setText(text.toString());
    }

    public static long currentTime() {
        return time.getCurrentTime();
    }

    public static void addTimeout(long time, Truck truck) {
        Main.time.addTimeout(time, truck);
    }

    public synchronized static void updateTime(long timestamp) {
        timer.setText(String.format("%02d:%02d", (timestamp / 60) % 24, timestamp % 60));
    }
}
