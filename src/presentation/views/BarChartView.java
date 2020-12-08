package src.presentation.views;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.*;

public class BarChartView extends JPanel {
    private int histogramHeight = 200;
    private int barWidth = 50;
    private int barGap = 10;

    private JPanel barPanel;
    private JPanel labelPanel;

    private List<Bar> bars = new ArrayList<Bar>();

    public BarChartView() {
        setBorder(new EmptyBorder(0, 0, 0, 0));
        setLayout(new BorderLayout());

        barPanel = new JPanel(new GridLayout(1, 0, barGap, 0));
        add(barPanel, BorderLayout.CENTER);

        labelPanel = new JPanel(new GridLayout(1, 0, barGap, 0));
        add(labelPanel, BorderLayout.PAGE_END);
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
            int barHeight = (bar.getValue() * histogramHeight) / maxValue;
            Icon icon = new ColorIcon(bar.getColor(), barWidth, barHeight);
            label.setIcon(icon);
            barPanel.add(label);

            JLabel barLabel = new JLabel(bar.getLabel());
            barLabel.setHorizontalAlignment(JLabel.CENTER);
            labelPanel.add(barLabel);
        }
    }

    private class Bar {
        private String label;
        private int value;
        private Color color;

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

    private class ColorIcon implements Icon {
        private Color color;
        private int width;
        private int height;

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
            g.fillRoundRect(x, y, width, height, 10, 10);
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
