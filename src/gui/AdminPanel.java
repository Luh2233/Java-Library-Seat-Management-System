package gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.*;
import java.util.List;
import model.*;
import service.*;

public class AdminPanel extends JPanel {
    private ReservationManager rm;
    private StatisticsManager sm;
    private CreditManager cm;
    private JTable statsTable;
    private DefaultTableModel statsModel;
    private JTable mgmtTable;
    private DefaultTableModel mgmtModel;
    private JLabel occupancyLabel;
    private BarChartPanel barChart;
    private PieChartPanel pieChart;
    private JLabel statusLabel;

    public AdminPanel(User user, ReservationManager rm, StatisticsManager sm,
                       CreditManager cm) {
        this.rm = rm;
        this.sm = sm;
        this.cm = cm;
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(3, 3, 3, 3);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        JButton refreshBtn = new JButton("Refresh Stats");
        refreshBtn.addActionListener(e -> refreshStats());
        topPanel.add(refreshBtn);
        occupancyLabel = new JLabel("Overall Occupancy Rate: --");
        occupancyLabel.setFont(new Font("Arial", Font.BOLD, 14));
        topPanel.add(occupancyLabel);
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(topPanel, gbc);

        JPanel chartPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        barChart = new BarChartPanel(new HashMap<>(), "Popular Seats");
        chartPanel.add(barChart);
        pieChart = new PieChartPanel(new HashMap<>(), "Zone Distribution");
        chartPanel.add(pieChart);
        gbc.gridy = 1;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        add(chartPanel, gbc);

        String[] statsCols = {"Seat", "Total Reservations", "Avg Duration(h)", "Peak Hour"};
        statsModel = new DefaultTableModel(statsCols, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        statsTable = new JTable(statsModel);
        statsTable.setRowHeight(22);
        JScrollPane statsScroll = new JScrollPane(statsTable);
        statsScroll.setPreferredSize(new Dimension(600, 95));
        gbc.gridy = 2;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(statsScroll, gbc);

        JPanel mgmtPanel = new JPanel(new BorderLayout(6, 6));
        mgmtPanel.setBorder(BorderFactory.createTitledBorder("Manage Reservations"));
        String[] mgmtCols = {"ResID", "User", "Seat", "Start", "End", "Status"};
        mgmtModel = new DefaultTableModel(mgmtCols, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        mgmtTable = new JTable(mgmtModel);
        mgmtTable.setRowHeight(22);
        JScrollPane mgmtScroll = new JScrollPane(mgmtTable);
        mgmtScroll.setPreferredSize(new Dimension(600, 120));
        mgmtPanel.add(mgmtScroll, BorderLayout.CENTER);

        JPanel mgmtBottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 3));
        JButton cancelBtn = new JButton("Cancel Selected");
        cancelBtn.addActionListener(e -> doCancel());
        mgmtBottom.add(cancelBtn);

        JButton overdueBtn = new JButton("Cancel Overdue");
        overdueBtn.addActionListener(e -> doCancelOverdue());
        mgmtBottom.add(overdueBtn);
        JButton refreshMgmtBtn = new JButton("Refresh");
        refreshMgmtBtn.addActionListener(e -> refreshMgmt());
        mgmtBottom.add(refreshMgmtBtn);
        statusLabel = new JLabel(" ");
        statusLabel.setForeground(Color.BLUE);
        mgmtBottom.add(statusLabel);
        mgmtPanel.add(mgmtBottom, BorderLayout.SOUTH);

        gbc.gridy = 3;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(mgmtPanel, gbc);

        refreshStats();
        refreshMgmt();
    }

    public void refresh() {
        refreshStats();
        refreshMgmt();
    }

    private void refreshStats() {
        double rate = sm.getOccupancyRate();
        occupancyLabel.setText(String.format("Overall Occupancy Rate: %.0f%%",
            rate * 100));

        statsModel.setRowCount(0);
        Map<Seat, Integer> countMap = new HashMap<>();
        Map<Seat, Integer> durMap = new HashMap<>();
        Map<Seat, Map<Integer, Integer>> seatPeakMap = new HashMap<>();

        List<Reservation> allRes = rm.getAllReservations();
        for (Reservation r : allRes) {
            if (r.getStatus() == ReservationStatus.CANCELLED) continue;
            Seat seat = r.getSeat();
            countMap.put(seat, countMap.getOrDefault(seat, 0) + 1);
            durMap.put(seat, durMap.getOrDefault(seat, 0) + (r.getEndTime() - r.getStartTime()));
            seatPeakMap.putIfAbsent(seat, new HashMap<>());
            Map<Integer, Integer> hourMap = seatPeakMap.get(seat);
            int h = r.getStartTime();
            hourMap.put(h, hourMap.getOrDefault(h, 0) + 1);
        }

        Map<String, Integer> zoneCount = new HashMap<>();

        for (Map.Entry<Seat, Integer> entry : countMap.entrySet()) {
            Seat seat = entry.getKey();
            int total = entry.getValue();
            int totalDur = durMap.getOrDefault(seat, 0);
            double avgDur = (double) totalDur / total;

            int peakHour = seatPeakMap.get(seat).entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey).orElse(0);

            statsModel.addRow(new Object[]{
                seat.getSeatId(), total,
                String.format("%.1f", avgDur),
                peakHour + ":00"
            });

            String zone = seat.getZone();
            zoneCount.put(zone, zoneCount.getOrDefault(zone, 0) + total);
        }

        Map<String, Double> barData = new HashMap<>();
        int maxCount = countMap.values().stream().max(Integer::compareTo).orElse(1);
        for (Map.Entry<Seat, Integer> entry : countMap.entrySet()) {
            barData.put(entry.getKey().getSeatId(), (double) entry.getValue());
        }
        barChart.updateData(barData);
        pieChart.updateData(zoneCount);

        statusLabel.setText("Statistics refreshed. " + allRes.size() + " total reservations.");
    }

    private void refreshMgmt() {
        mgmtModel.setRowCount(0);
        for (Reservation r : rm.getAllReservations()) {
            mgmtModel.addRow(new Object[]{
                r.getReservationId(),
                r.getStudent().getName(),
                r.getSeat().getSeatId(),
                r.getStartTime() + ":00",
                r.getEndTime() + ":00",
                r.getStatus()
            });
        }
    }

    private void doCancel() {
        int row = mgmtTable.getSelectedRow();
        if (row < 0) {
            statusLabel.setText("Please select a reservation in the manage table.");
            return;
        }
        String id = (String) mgmtModel.getValueAt(row, 0);
        ReservationStatus status = (ReservationStatus) mgmtModel.getValueAt(row, 5);
        if (status == ReservationStatus.CHECKED_IN || status == ReservationStatus.COMPLETED) {
            statusLabel.setText("Cannot cancel a checked-in or completed reservation.");
            return;
        }
        rm.cancelReservation(id);
        statusLabel.setText("Reservation " + id + " cancelled by admin.");
        refreshMgmt();
        refreshStats();
    }

    private void doCancelOverdue() {
        java.time.LocalTime t = java.time.LocalTime.now();
        int nowMin = t.getHour() * 60 + t.getMinute();
        int count = 0;
        for (Reservation r : rm.getAllReservations()) {
            boolean overdue = r.getStatus() == ReservationStatus.CHECKED_IN
                && nowMin > r.getEndTime() * 60 + 10;
            boolean noShow = r.getStatus() == ReservationStatus.PENDING
                && nowMin > r.getStartTime() * 60 + 30;
            if (overdue || noShow) {
                rm.forceCancel(r.getReservationId());
                cm.applyPenalty(r.getStudent());
                count++;
            }
        }
        if (count == 0) {
            statusLabel.setText("No overdue or no-show reservations found.");
        } else {
            statusLabel.setText(count + " overdue/no-show cancelled. -10 penalty applied.");
        }
        refreshMgmt();
        refreshStats();
    }
}
