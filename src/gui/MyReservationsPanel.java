package gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.time.LocalTime;
import java.util.List;
import model.*;
import service.*;

public class MyReservationsPanel extends JPanel {
    private Student student;
    private ReservationManager rm;
    private CreditManager cm;
    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel creditLabel;
    private JLabel statusLabel;
    private Runnable onRefresh;

    public MyReservationsPanel(User user, ReservationManager rm, CreditManager cm) {
        this.student = (Student) user;
        this.rm = rm;
        this.cm = cm;
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        creditLabel = new JLabel("Credit Score: " + student.getCreditScore());
        creditLabel.setFont(new Font("Arial", Font.BOLD, 14));
        topPanel.add(creditLabel);
        add(topPanel, BorderLayout.NORTH);

        String[] cols = {"ResID", "Seat", "Start", "End", "Status"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(22);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        JButton checkInBtn = new JButton("Check In");
        checkInBtn.addActionListener(e -> doCheckIn());
        bottomPanel.add(checkInBtn);

        JButton completeBtn = new JButton("Complete");
        completeBtn.addActionListener(e -> doComplete());
        bottomPanel.add(completeBtn);

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(e -> doCancel());
        bottomPanel.add(cancelBtn);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> refreshTable());
        bottomPanel.add(refreshBtn);

        statusLabel = new JLabel(" ");
        statusLabel.setForeground(Color.BLUE);
        bottomPanel.add(statusLabel);

        add(bottomPanel, BorderLayout.SOUTH);

        refreshTable();
    }

    public void setOnRefresh(Runnable r) {
        this.onRefresh = r;
    }

    public void refreshTable() {
        tableModel.setRowCount(0);
        creditLabel.setText("Credit Score: " + student.getCreditScore());

        List<Reservation> list = rm.getReservation(student);
        for (Reservation r : list) {
            tableModel.addRow(new Object[]{
                r.getReservationId(),
                r.getSeat().getSeatId(),
                r.getStartTime() + ":00",
                r.getEndTime() + ":00",
                r.getStatus()
            });
        }
        statusLabel.setText(list.size() + " reservation(s) found.");
        if (onRefresh != null) onRefresh.run();
    }

    private void doCheckIn() {
        int row = table.getSelectedRow();
        if (row < 0) {
            statusLabel.setText("Please select a reservation first.");
            return;
        }
        String id = (String) tableModel.getValueAt(row, 0);
        ReservationStatus status = (ReservationStatus) tableModel.getValueAt(row, 4);
        if (status != ReservationStatus.PENDING) {
            statusLabel.setText("Only PENDING reservations can check in.");
            return;
        }
        Reservation r = rm.findById(id);
        LocalTime t = LocalTime.now();
        int nowMin = t.getHour() * 60 + t.getMinute();
        int startMin = r.getStartTime() * 60;

        if (nowMin < startMin) {
            statusLabel.setText("Too early. Check-in opens at " + r.getStartTime() + ":00.");
            return;
        }
        if (nowMin > startMin + 30) {
            statusLabel.setText("Check-in window closed (>30min past start).");
            return;
        }

        rm.checkIn(id);
        statusLabel.setText("Checked in successfully.");
        refreshTable();
    }

    private void doCancel() {
        int row = table.getSelectedRow();
        if (row < 0) {
            statusLabel.setText("Please select a reservation first.");
            return;
        }
        String id = (String) tableModel.getValueAt(row, 0);
        ReservationStatus status = (ReservationStatus) tableModel.getValueAt(row, 4);
        if (status == ReservationStatus.CHECKED_IN || status == ReservationStatus.COMPLETED) {
            statusLabel.setText("Cannot cancel a checked-in or completed reservation.");
            return;
        }
        Reservation r = rm.findById(id);
        if (r != null && status == ReservationStatus.PENDING) {
            LocalTime t = LocalTime.now();
            int nowMin = t.getHour() * 60 + t.getMinute();
            if (nowMin > r.getStartTime() * 60) {
                cm.applyPenalty(student);
                statusLabel.setText("Late cancel! -10 penalty.");
                rm.cancelReservation(id);
                refreshTable();
                return;
            }
        }
        rm.cancelReservation(id);
        statusLabel.setText("Reservation " + id + " cancelled.");
        refreshTable();
    }

    private void doComplete() {
        int row = table.getSelectedRow();
        if (row < 0) {
            statusLabel.setText("Please select a reservation first.");
            return;
        }
        String id = (String) tableModel.getValueAt(row, 0);
        ReservationStatus status = (ReservationStatus) tableModel.getValueAt(row, 4);
        if (status != ReservationStatus.CHECKED_IN) {
            statusLabel.setText("Only CHECKED_IN reservations can be completed.");
            return;
        }
        Reservation r = rm.findById(id);
        LocalTime t = LocalTime.now();
        int nowMin = t.getHour() * 60 + t.getMinute();
        int endMin = r.getEndTime() * 60;
        rm.completeReservation(id);
        if (nowMin > endMin + 10) {
            cm.applyPenalty(student);
            statusLabel.setText(String.format("Overdue! End: %02d:%02d, Now: %02d:%02d. -10.",
                r.getEndTime(), 0, t.getHour(), t.getMinute()));
        } else if (nowMin <= endMin) {
            cm.applyReward(student);
            statusLabel.setText("On time. +5.");
        } else {
            statusLabel.setText("Slightly late (<10min). Seat released.");
        }
        refreshTable();
    }
}
