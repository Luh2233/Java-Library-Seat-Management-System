package gui;

import java.awt.*;
import javax.swing.*;
import java.util.List;
import model.*;
import service.*;

public class SeatMapPanel extends JPanel {
    private Student student;
    private ReservationManager rm;
    private CreditManager cm;
    private JPanel seatGrid;
    private JLabel selectedLabel;
    private JComboBox<String> startBox;
    private JComboBox<String> endBox;
    private JComboBox<String> floorBox;
    private JComboBox<String> zoneBox;
    private JButton reserveBtn;
    private JLabel statusLabel;
    private Seat selectedSeat;
    private List<Seat> currentSeats;

    public SeatMapPanel(User user, ReservationManager rm, CreditManager cm) {
        this.student = (Student) user;
        this.rm = rm;
        this.cm = cm;
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        topPanel.add(new JLabel("Floor:"));
        floorBox = new JComboBox<>(new String[]{"1", "2", "All"});
        topPanel.add(floorBox);

        topPanel.add(new JLabel("Zone:"));
        zoneBox = new JComboBox<>(new String[]{"All", "Quiet Zone", "Group Zone"});
        topPanel.add(zoneBox);

        JButton searchBtn = new JButton("Search");
        searchBtn.addActionListener(e -> doSearch());
        topPanel.add(searchBtn);

        add(topPanel, BorderLayout.NORTH);

        seatGrid = new JPanel();
        seatGrid.setBorder(BorderFactory.createTitledBorder("Seats"));
        add(new JScrollPane(seatGrid), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        selectedLabel = new JLabel("No seat selected");
        bottomPanel.add(selectedLabel);

        bottomPanel.add(new JLabel("Start:"));
        String[] hours = {"8","9","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24"};
        startBox = new JComboBox<>(hours);
        bottomPanel.add(startBox);

        bottomPanel.add(new JLabel("End:"));
        endBox = new JComboBox<>(hours);
        endBox.setSelectedIndex(2);
        bottomPanel.add(endBox);

        reserveBtn = new JButton("Reserve");
        reserveBtn.setEnabled(false);
        reserveBtn.addActionListener(e -> doReserve());
        bottomPanel.add(reserveBtn);

        statusLabel = new JLabel("Select a floor and zone, then click Search.");
        statusLabel.setForeground(Color.BLUE);
        bottomPanel.add(statusLabel);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void doSearch() {
        String floorStr = (String) floorBox.getSelectedItem();
        String zoneStr = (String) zoneBox.getSelectedItem();

        if (floorStr.equals("All")) {
            currentSeats = new java.util.ArrayList<>();
            currentSeats.addAll(rm.searchByFloor(1));
            currentSeats.addAll(rm.searchByFloor(2));
        } else {
            int floor = Integer.parseInt(floorStr);
            currentSeats = rm.searchByFloor(floor);
        }

        if (!zoneStr.equals("All")) {
            currentSeats.removeIf(s -> !s.getZone().equals(zoneStr));
        }

        refreshGrid();
        selectedSeat = null;
        selectedLabel.setText("No seat selected");
        reserveBtn.setEnabled(false);
    }

    public void refresh() {
        if (currentSeats != null && !currentSeats.isEmpty()) {
            refreshGrid();
        }
    }

    private void refreshGrid() {
        seatGrid.removeAll();
        if (currentSeats == null || currentSeats.isEmpty()) {
            statusLabel.setText("No seats found.");
            seatGrid.revalidate();
            seatGrid.repaint();
            return;
        }

        int cols = 4;
        int rows = (int) Math.ceil((double) currentSeats.size() / cols);
        seatGrid.setLayout(new GridLayout(rows, cols, 6, 6));

        for (Seat seat : currentSeats) {
            JButton btn = new JButton(seat.display());
            btn.setFont(new Font("Arial", Font.PLAIN, 10));

            SeatStatus st = seat.getStatus();
            if (st == SeatStatus.AVAILABLE) {
                btn.setBackground(new Color(144, 238, 144));
            } else if (st == SeatStatus.MAINTENANCE) {
                btn.setBackground(Color.LIGHT_GRAY);
            } else {
                btn.setBackground(new Color(255, 182, 193));
            }

            btn.addActionListener(e -> {
                selectedSeat = seat;
                selectedLabel.setText("Selected: " + seat.getSeatId());
                reserveBtn.setEnabled(seat.getStatus() == SeatStatus.AVAILABLE);
            });
            seatGrid.add(btn);
        }

        seatGrid.revalidate();
        seatGrid.repaint();
        statusLabel.setText(currentSeats.size() + " seat(s) displayed.");
    }

    private void doReserve() {
        if (selectedSeat == null) return;

        if (rm.hasActiveReservation(student)) {
            statusLabel.setText("You already have an active reservation.");
            return;
        }

        int start = Integer.parseInt((String) startBox.getSelectedItem());
        int end = Integer.parseInt((String) endBox.getSelectedItem());

        if (end <= start) {
            statusLabel.setText("End time must be after start time.");
            return;
        }

        java.time.LocalTime t = java.time.LocalTime.now();
        if (start * 60 < t.getHour() * 60 + t.getMinute()) {
            statusLabel.setText("Cannot reserve a past time slot.");
            return;
        }

        try {
            cm.validateCredit(student);
            selectedSeat.reserve();
        } catch (exception.InsufficientCreditException e) {
            statusLabel.setText(e.getMessage());
            return;
        } catch (exception.SeatUnavailableException e) {
            statusLabel.setText(e.getMessage());
            return;
        }

        String rId = "R" + System.currentTimeMillis() % 100000;
        Reservation r = new Reservation(rId, student, selectedSeat,
            start, end, ReservationStatus.PENDING);
        rm.addReservation(r);

        statusLabel.setText("Seat " + selectedSeat.getSeatId() +
            " reserved from " + start + ":00 to " + end + ":00.");
        refreshGrid();
        reserveBtn.setEnabled(false);
        selectedLabel.setText("No seat selected");
    }
}
