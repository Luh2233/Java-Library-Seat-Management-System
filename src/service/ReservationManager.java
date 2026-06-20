package service;
import java.util.*;
import model.*;

// Manages reservation: check-in, cancel, complete
public class ReservationManager {
    private List<Reservation> reservations = new ArrayList<>();
    private List<Seat> allSeats = new ArrayList<>();

    public void setReservations(List<Reservation> list) {
        this.reservations = list;
    }

    public void setAllSeats(List<Seat> seats) {
        this.allSeats = seats;
    }

    public void addReservation(Reservation r) {
        reservations.add(r);
    }

    public void cancelReservation(String id) {
        for (Reservation r : reservations) {
            if (r.getReservationId().equals(id)) {
                if (r.getStatus() == ReservationStatus.CHECKED_IN|| r.getStatus() == ReservationStatus.COMPLETED) {
                    return;
                }
                r.setStatus(ReservationStatus.CANCELLED);
                r.getSeat().setStatus(SeatStatus.AVAILABLE);
                break;
            }
        }
    }

    public void forceCancel(String id) {
        for (Reservation r : reservations) {
            if (r.getReservationId().equals(id)) {
                r.setStatus(ReservationStatus.CANCELLED);
                r.getSeat().setStatus(SeatStatus.AVAILABLE);
                break;
            }
        }
    }

    public void completeReservation(String id) {
        for (Reservation r : reservations) {
            if (r.getReservationId().equals(id)) {
                if (r.getStatus() != ReservationStatus.CHECKED_IN) {
                    return;
                }
                r.setStatus(ReservationStatus.COMPLETED);
                r.getSeat().setStatus(SeatStatus.AVAILABLE);
                break;
            }
        }
    }

    public void checkIn(String id) {
        for (Reservation r : reservations) {
            if (r.getReservationId().equals(id)) {
                r.setStatus(ReservationStatus.CHECKED_IN);
                r.getSeat().setStatus(SeatStatus.OCCUPIED);
                break;
            }
        }
    }

    public List<Reservation> getReservation(Student s) {
        List<Reservation> result = new ArrayList<>();
        for (Reservation r : reservations) {
            if (r.getStudent().equals(s)) {
                result.add(r);
            }
        }
        return result;
    }

    public List<Reservation> getAllReservations() {
        return reservations;
    }

    public List<Seat> searchByFloor(int floor) {
        List<Seat> result = new ArrayList<>();
        for (Seat s : allSeats) {
            if (s.getFloor() == floor) {
                result.add(s);
            }
        }
        return result;
    }

    public List<Seat> searchByZone(String zone) {
        List<Seat> result = new ArrayList<>();
        for (Seat s : allSeats) {
            if (s.getZone().equals(zone)) {
                result.add(s);
            }
        }
        return result;
    }

    public Reservation findById(String id) {
        for (Reservation r : reservations) {
            if (r.getReservationId().equals(id)) {
                return r;
            }
        }
        return null;
    }

    public boolean hasActiveReservation(Student s) {
        for (Reservation r : reservations) {
            if (r.getStudent().equals(s) &&(r.getStatus() == ReservationStatus.PENDING ||r.getStatus() == ReservationStatus.CHECKED_IN)) {
                return true;
            }
        }
        return false;
    }
}
