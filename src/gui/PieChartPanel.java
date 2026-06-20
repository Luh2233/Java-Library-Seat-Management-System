package gui;

import java.awt.*;
import javax.swing.*;
import java.util.Map;

public class PieChartPanel extends JPanel {
    private Map<String, Integer> data;
    private String title;

    public PieChartPanel(Map<String, Integer> data, String title) {
        this.data = data;
        this.title = title;
        setPreferredSize(new Dimension(250, 200));
        setBackground(Color.WHITE);
    }

    public void updateData(Map<String, Integer> data) {
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

        g2.setFont(new Font("Arial", Font.BOLD, 11));
        g2.setColor(Color.BLACK);
        FontMetrics fm = g2.getFontMetrics();
        int titleW = fm.stringWidth(title);
        g2.drawString(title, (w - titleW) / 2, 18);

        int total = 0;
        for (int v : data.values()) total += v;
        if (total == 0) return;

        int cx = w / 2;
        int cy = h / 2 + 10;
        int r = Math.min(w, h) / 2 - 30;

        Color[] colors = {new Color(46, 116, 181), new Color(84, 130, 53),
                          new Color(192, 80, 77), new Color(255, 192, 0),
                          new Color(128, 0, 128), new Color(0, 128, 128)};

        int startAngle = 0;
        int i = 0;
        int legendX = 10;
        int legendY = h - 20 * data.size() - 10;

        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            int val = entry.getValue();
            int angle = (int) (360.0 * val / total);

            g2.setColor(colors[i % colors.length]);
            g2.fillArc(cx - r, cy - r, r * 2, r * 2, startAngle, angle);

            g2.fillRect(legendX, legendY + i * 18, 12, 12);
            g2.setColor(Color.BLACK);
            double pct = 100.0 * val / total;
            g2.setFont(new Font("Arial", Font.PLAIN, 10));
            g2.drawString(String.format("%s: %.0f%%", entry.getKey(), pct),
                          legendX + 16, legendY + i * 18 + 10);

            startAngle += angle;
            i++;
        }
    }
}
