package service;
import model.*;

// manage credit score rewards (+5), penalties (-10), and ban
public class CreditManager {
    public void applyPenalty(Student s) {
        s.addCreditScore(-10);
        if (s.getCreditScore() < 60) {
            s.trueBanned();
        }
    }

    public void applyReward(Student s) {
        s.addCreditScore(5);
        if (s.getCreditScore()>= 60) {
            s.falseBanned();
        }
    }

    public boolean isBanned(Student s) {
        return s.isBanned() || s.getCreditScore() < 60;
    }

    public void validateCredit(Student s) throws exception.InsufficientCreditException {
        if (isBanned(s)) {
            throw new exception.InsufficientCreditException(
                "Credit too low: " + s.getCreditScore() + ". Action denied.");
        }
    }

    public void unban(Student s) {
        s.falseBanned();
        s.addCreditScore(60 - s.getCreditScore());
    }
}
