package exception;

// student lacks enough credit to perform an action
public class InsufficientCreditException extends Exception {
    public InsufficientCreditException(String str) {
        super(str);
    }
}
