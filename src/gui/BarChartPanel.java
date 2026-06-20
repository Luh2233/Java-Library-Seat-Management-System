package gui;

import java.awt.*;
import javax.swing.*;
import java.util.*;

public class BarChartPanel extends JPanel {
    private Map<String, Double> data;
    private String title;

    public BarChartPanel(Map<String, Double> data, String title) {
        this.data = data;
        this.title = title;
        setPreferredSize(new Dimension(300, 200));
        setBackground(Color.WHITE);
    }

    public void updateData(Map<String, Double> data) {
        this.data = data;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        int w = getWidth();
        int h = getHeight();

        if (data == null || data.isEmpty()) {
            g2.setColor(Color.GRAY);
            g2.setFont(new Font("Arial", Font.PLAIN, 12));
            String msg = "No data - click Refresh Stats";
            int msgW = g2.getFontMetrics().stringWidth(msg);
            g2.drawString(msg, (w - msgW) / 2, h / 2);
            return;
        }

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
        int left = 60;
        int right = 30;
        int top = 30;
        int bottom = 40;
        int chartW = w - left - right;
        int chartH = h - top - bottom;

        g2.setFont(new Font("Arial", Font.BOLD, 11));
        g2.setColor(Color.BLACK);
        FontMetrics fm = g2.getFontMetrics();
        int titleW = fm.stringWidth(title);
        g2.drawString(title, (w - titleW) / 2, 18);

        if (data.isEmpty()) return;

        int n = data.size();
        double maxVal = 1.0;
        for (double v : data.values()) {
            if (v > maxVal) maxVal = v;
        }
        if (maxVal == 0) maxVal = 1.0;

        int barH = Math.min(30, chartH / n - 4);
        int gap = Math.max(2, (chartH - n * barH) / (n + 1));

        g2.setFont(new Font("Arial", Font.PLAIN, 10));
        int y = top + gap;
        int i = 0;
        Color[] colors = {new Color(46, 116, 181), new Color(31, 73, 125),
                          new Color(84, 130, 53), new Color(192, 80, 77)};

        java.util.List<Map.Entry<String, Double>> sorted = new java.util.ArrayList<>(data.entrySet());
        sorted.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        for (Map.Entry<String, Double> entry : sorted) {
            String label = entry.getKey();
            double val = entry.getValue();

            int barW = (int) (val / maxVal * chartW);

            g2.setColor(colors[i % colors.length]);
            g2.fillRect(left, y, barW, barH);

            g2.setColor(Color.BLACK);
            g2.drawString(label, 5, y + barH / 2 + 4);

            String pctStr = String.format("%.0f", val);
            g2.drawString(pctStr, left + barW + 3, y + barH / 2 + 4);

            y += barH + gap;
            i++;
        }
    }
}
