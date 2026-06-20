package gui;

import java.awt.*;
import java.time.LocalTime;
import javax.swing.*;
import model.*;
import service.*;
import io.*;

public class MainFrame extends JFrame {
    private User user;
    private ReservationManager rm;
    private CreditManager cm;
    private StatisticsManager sm;
    private DataManager dm;
    private MyReservationsPanel myResPanel;
    private JLabel userLabel;
    private javax.swing.Timer clockTimer;

    public MainFrame(User user, ReservationManager rm, CreditManager cm,
                     StatisticsManager sm, DataManager dm) {
        this.user = user;
        this.rm = rm;
        this.cm = cm;
        this.sm = sm;
        this.dm = dm;

        setTitle("Library Seat Reservation System");
        setSize(880, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                clockTimer.stop();
                dm.saveAll();
                dispose();
                System.exit(0);
            }
        });

        JPanel headerPanel = new JPanel(new BorderLayout());
        String role = user.isAdmin() ? "Admin" : "Student";
        userLabel = new JLabel(role + ": " + user.getName() +
            "  |  Credit: " + user.getCreditScore());
        userLabel.setFont(new Font("Arial", Font.BOLD, 13));
        headerPanel.add(userLabel, BorderLayout.WEST);

        JLabel clockLabel = new JLabel();
        clockLabel.setFont(new Font("Arial", Font.BOLD, 13));
        clockLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 15));
        headerPanel.add(clockLabel, BorderLayout.EAST);

        clockTimer = new javax.swing.Timer(1000, e -> {
            LocalTime t = LocalTime.now();
            clockLabel.setText(String.format("%02d:%02d:%02d",
                t.getHour(), t.getMinute(), t.getSecond()));
        });
        clockTimer.start();
        add(headerPanel, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();

        if (user instanceof Student) {
            SeatMapPanel seatMapPanel = new SeatMapPanel(user, rm, cm);
            tabbedPane.addTab("Seat Map", seatMapPanel);

            myResPanel = new MyReservationsPanel(user, rm, cm);
            myResPanel.setOnRefresh(() -> {
                refreshHeader();
                seatMapPanel.refresh();
            });
            tabbedPane.addTab("My Reservations", myResPanel);

            tabbedPane.addChangeListener(e -> {
                int idx = tabbedPane.getSelectedIndex();
                if (idx >= 0 && "My Reservations".equals(tabbedPane.getTitleAt(idx))) {
                    myResPanel.refreshTable();
                }
            });
        }

        if (user.isAdmin()) {
            AdminPanel adminPanel = new AdminPanel(user, rm, sm, cm);
            tabbedPane.addTab("Admin", adminPanel);

            tabbedPane.addChangeListener(e -> {
                int idx = tabbedPane.getSelectedIndex();
                if (idx >= 0 && "Admin".equals(tabbedPane.getTitleAt(idx))) {
                    adminPanel.refresh();
                }
            });
        }

        add(tabbedPane, BorderLayout.CENTER);
    }

    public void refreshHeader() {
        String role = user.isAdmin() ? "Admin" : "Student";
        userLabel.setText(role + ": " + user.getName() +
            "  |  Credit: " + user.getCreditScore());
    }
}
