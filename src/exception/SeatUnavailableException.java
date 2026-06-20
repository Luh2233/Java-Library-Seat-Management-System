package exception;

// seat is already reserved or occupied
public class SeatUnavailableException extends Exception {
    public SeatUnavailableException(String str) {
        super(str);
    }
}
