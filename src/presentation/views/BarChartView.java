package src.presentation.views;

import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.*;

public class BarChartView extends JPanel {
    private final JPanel barPanel;
    private final JPanel labelPanel;

    private final List<Bar> bars = new ArrayList<>();

    // Misc settings
    private static final int defaultSize = 200;
    private static final int barGap = 10;
    private static final int barWidth = 50;

    public BarChartView() {
        setBorder(new EmptyBorder(0, 0, 0, 0));
        setLayout(new BorderLayout());

        barPanel = new JPanel(new GridLayout(1, 0, barGap, 0));
        labelPanel = new JPanel(new GridLayout(1, 0, barGap, 0));

        add(barPanel, BorderLayout.CENTER);
        add(labelPanel, BorderLayout.PAGE_END);

        setSize(new Dimension(defaultSize, defaultSize));
    }

    public void addBar(String label, int value, Color color) {
        Bar bar = new Bar(label, value, color);
        bars.add(bar);
    }

    public void layoutHistogram() {
        barPanel.removeAll();
        labelPanel.removeAll();

        int maxValue = 0;

        for (Bar bar: bars) {
            maxValue = Math.max(maxValue, bar.getValue());
        }

        for (Bar bar: bars) {
            JLabel label = new JLabel(bar.getValue() + "");
            label.setHorizontalTextPosition(JLabel.CENTER);
            label.setHorizontalAlignment(JLabel.CENTER);
            label.setVerticalTextPosition(JLabel.TOP);
            label.setVerticalAlignment(JLabel.BOTTOM);

            final int barHeight = (bar.getValue() * getHeight()) / maxValue;
            label.setIcon(new ColorIcon(bar.getColor(), barWidth, barHeight));
            barPanel.add(label);

            JLabel barLabel = new JLabel(bar.getLabel());
            barLabel.setHorizontalAlignment(JLabel.CENTER);
            labelPanel.add(barLabel);
        }

        revalidate();
    }

    private static class Bar {
        private final String label;
        private final int value;
        private final Color color;

        public Bar(String label, int value, Color color) {
            this.label = label;
            this.value = value;
            this.color = color;
        }

        public String getLabel() {
            return label;
        }

        public int getValue() {
            return value;
        }

        public Color getColor() {
            return color;
        }
    }

    private static class ColorIcon implements Icon {
        private final Color color;
        private final int width;
        private final int height;

        // Settings
        private static final int arcWidth = 10;
        private static final int arcHeight = 10;

        public ColorIcon(Color color, int width, int height) {
            this.color = color;
            this.width = width;
            this.height = height;
        }

        @Override
        public int getIconWidth() {
            return width;
        }

        @Override
        public int getIconHeight() {
            return height;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            g.setColor(color);
            g.fillRoundRect(x, y, width, height, arcWidth, arcHeight);
        }
    }

    // TODO: remove this, i coded this only for testing purposes
    public static void main(String[] args) {
        BarChartView panel = new BarChartView();
        panel.addBar("EASY", 8, new Color(187, 255, 194));
        panel.addBar("MEDIUM", 10, new Color(255, 247, 174));
        panel.addBar("HARD", 5, new Color(255, 191, 191));
        panel.addBar("EXTREME", 1, new Color(187, 185, 253));
        panel.layoutHistogram();

        JFrame frame = new JFrame("Histogram Panel");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(panel);
        frame.setLocationByPlatform(true);
        frame.pack();
        frame.setVisible(true);
    }
}
