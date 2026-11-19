package bank.model;

/** Interface implemented by Account: anything that can receive deposits and allow withdrawals. */
public interface Transactable {
    void deposit(double amount);                    // overloaded
    void deposit(double amount, String type);       // overloaded
    void withdraw(double amount);
    double getBalance();
}
