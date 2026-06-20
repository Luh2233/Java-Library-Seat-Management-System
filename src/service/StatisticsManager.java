package service;
import java.util.*;
import model.*;

// computes occupancy rate, peak hours, and seat popularity
public class StatisticsManager {
    private List<Reservation> reservations;
    private List<Seat> allSeats;

    public StatisticsManager(List<Reservation> reservations, List<Seat> allSeats) {
        this.reservations = reservations;
        this.allSeats = allSeats;
    }

    public List<Seat> getMostPopularSeats() {
        Map<Seat, Integer> countMap = new HashMap<>();
        for (Reservation r : reservations) {
            Seat seat = r.getSeat();
            countMap.put(seat, countMap.getOrDefault(seat, 0) + 1);
        }
        List<Map.Entry<Seat, Integer>> sorted = new ArrayList<>(countMap.entrySet());
        sorted.sort((a, b) -> b.getValue() - a.getValue());

        List<Seat> result = new ArrayList<>();
        for (Map.Entry<Seat, Integer> entry : sorted) {
            result.add(entry.getKey());
        }
        return result;
    }

    public Map<Integer, Integer> getPeakHours() {
        Map<Integer, Integer> hourCount = new HashMap<>();
        for (Reservation r : reservations) {
            int hour = r.getStartTime();
            hourCount.put(hour, hourCount.getOrDefault(hour, 0) + 1);
        }
        return hourCount;
    }

    public int getTotalReservations(Seat seat) {
        int count = 0;
        for (Reservation r : reservations) {
            if (r.getSeat().equals(seat)) {
                count++;
            }
        }
        return count;
    }

    public double getAvgDuration(Seat seat) {
        int totalHours = 0;
        int count = 0;
        for (Reservation r : reservations) {
            if (r.getSeat().equals(seat)) {
                totalHours += (r.getEndTime() - r.getStartTime());
                count++;
            }
        }
        if (count == 0) { 
        	return 0.0;
        }
        return (double) totalHours / count;
    }

    public double getOccupancyRate() {
        if (allSeats == null || allSeats.isEmpty()) 
        	return 0.0;
        int occupied = 0;
        for (Seat seat : allSeats) {
            SeatStatus s = seat.getStatus();
            if (s == SeatStatus.RESERVED||s == SeatStatus.OCCUPIED) {
                occupied++;
            }
        }
        return (double) occupied / allSeats.size();
    }
}
