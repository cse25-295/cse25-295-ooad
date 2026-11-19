package bank.model;

/** Interface for accounts that earn monthly interest. */
public interface InterestBearing extends Transactable {
    double getBalance();
    double getMonthlyRate();

    default double calculateInterest() {
        return Math.round(getBalance() * getMonthlyRate() * 100) / 100.0;
    }

    default double payInterest() {
        double interest = calculateInterest();
        if (interest > 0) deposit(interest, "Monthly interest");
        return interest;
    }
}
