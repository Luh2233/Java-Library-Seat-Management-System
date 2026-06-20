package main;

import model.*;
import service.*;
import io.*;
import gui.*;

public class Main {
    public static void main(String[] args) {
        String dataPath;
        try {
            java.nio.file.Path binDir = java.nio.file.Paths
                .get(Main.class.getProtectionDomain().getCodeSource()
                    .getLocation().toURI());
            dataPath = binDir.resolve("../data").normalize().toString();
        } catch (Exception e) {
            dataPath = "data";
        }

        DataManager dm = new DataManager(dataPath);
        dm.loadAll();

        ReservationManager rm = new ReservationManager();
        rm.setAllSeats(dm.getSeats());
        rm.setReservations(dm.getReservations());

        CreditManager cm = new CreditManager();

        StatisticsManager sm = new StatisticsManager(
            dm.getReservations(), dm.getSeats());

        LoginDialog login = new LoginDialog(dm.getUsers());
        login.setVisible(true);

        User user = login.getLoggedInUser();
        if (user == null) {
            System.exit(0);
        }

        MainFrame frame = new MainFrame(user, rm, cm, sm, dm);
        frame.setVisible(true);
    }
}
