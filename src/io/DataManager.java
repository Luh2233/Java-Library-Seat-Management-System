package io;

import java.util.*;
import java.io.*;
import model.*;

public class DataManager {
    private String folder;
    private List<Seat> allSeats;
    private List<User> allUsers;
    private List<Reservation> reservations;

    public DataManager(String folder) {
        this.folder = folder;
        allSeats = new ArrayList<>();
        allUsers = new ArrayList<>();
        reservations = new ArrayList<>();
    }

    public List<Seat> getSeats()          { return allSeats; }
    public List<User> getUsers()          { return allUsers; }
    public List<Reservation> getReservations() { return reservations; }

    public void loadAll() {
        loadSeats();
        loadUsers();
        loadReservations();
    }

    private void loadSeats() {
        try {
            BufferedReader br = new BufferedReader(new FileReader(folder + "/seats.csv"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                String id   = parts[0];
                int floor   = Integer.parseInt(parts[1]);
                String zone = parts[2];
                String type = parts[3];

                Seat seat;
                if (type.equals("quiet")) {
                    seat = new QuietSeat(id, floor, zone);
                } else {
                    seat = new GroupSeat(id, floor, zone);
                }

                if (parts.length >= 5) {
                    seat.setStatus(SeatStatus.valueOf(parts[4]));
                }
                allSeats.add(seat);
            }
            br.close();
        } catch (IOException e) {
            System.out.println("No seats.csv found, starting with empty seat list.");
        }
    }

    private void loadUsers() {
        try {
            BufferedReader br = new BufferedReader(new FileReader(folder + "/users.csv"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                String id    = parts[0];
                String name  = parts[1];
                String pass  = parts[2];
                boolean isAdmin = Boolean.parseBoolean(parts[3]);
                int score    = Integer.parseInt(parts[4]);
                boolean banned = Boolean.parseBoolean(parts[5]);

                User user;
                if (isAdmin) {
                    user = new Admin(id, name, pass);
                } else {
                    user = new Student(id, name, pass, score);
                }
                if (banned) {
                    user.trueBanned();
                }
                allUsers.add(user);
            }
            br.close();
        } catch (IOException e) {
            System.out.println("No users.csv found, starting with empty user list.");
        }
    }

    private void loadReservations() {
        Map<String, User> userMap = new HashMap<>();
        for (User u : allUsers) {
            userMap.put(u.getUserId(), u);
        }
        Map<String, Seat> seatMap = new HashMap<>();
        for (Seat s : allSeats) {
            seatMap.put(s.getSeatId(), s);
        }

        try {
            BufferedReader br = new BufferedReader(
                new FileReader(folder + "/reservations.csv"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                String rId   = parts[0];
                String uId   = parts[1];
                String sId   = parts[2];
                int start    = Integer.parseInt(parts[3]);
                int end      = Integer.parseInt(parts[4]);
                ReservationStatus st = ReservationStatus.valueOf(parts[5]);

                Student student = (Student) userMap.get(uId);
                Seat seat = seatMap.get(sId);

                if (student != null && seat != null) {
                    Reservation r = new Reservation(rId, student, seat, start, end, st);
                    reservations.add(r);
                }
            }
            br.close();

            for (Reservation r : reservations) {
                if (r.getStatus() == ReservationStatus.PENDING) {
                    r.getSeat().setStatus(SeatStatus.RESERVED);
                } else if (r.getStatus() == ReservationStatus.CHECKED_IN) {
                    r.getSeat().setStatus(SeatStatus.OCCUPIED);
                }
            }
        } catch (IOException e) {
            System.out.println("No reservations.csv found, starting empty.");
        }
    }

    public void saveAll() {
        saveSeats();
        saveUsers();
        saveReservations();
    }

    private void saveSeats() {
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(folder + "/seats.csv"));
            for (Seat s : allSeats) {
                String type = (s instanceof QuietSeat) ? "quiet" : "group";
                pw.println(s.getSeatId() + "," + s.getFloor() + "," + s.getZone()
                    + "," + type + "," + s.getStatus());
            }
            pw.close();
        } catch (IOException e) {
            System.out.println("Error saving seats: " + e.getMessage());
        }
    }

    private void saveUsers() {
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(folder + "/users.csv"));
            for (User u : allUsers) {
                pw.println(u.getUserId() + "," + u.getName() + "," + u.getPassword()
                    + "," + u.isAdmin() + "," + u.getCreditScore() + "," + u.isBanned());
            }
            pw.close();
        } catch (IOException e) {
            System.out.println("Error saving users: " + e.getMessage());
        }
    }

    private void saveReservations() {
        try {
            PrintWriter pw = new PrintWriter(
                new FileWriter(folder + "/reservations.csv"));
            for (Reservation r : reservations) {
                pw.println(r.getReservationId() + "," + r.getStudent().getUserId()
                    + "," + r.getSeat().getSeatId() + "," + r.getStartTime()
                    + "," + r.getEndTime() + "," + r.getStatus());
            }
            pw.close();
        } catch (IOException e) {
            System.out.println("Error saving reservations: " + e.getMessage());
        }
    }
}
