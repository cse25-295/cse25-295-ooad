package bank.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Abstract Account: shared state and behaviour. Encapsulated fields. */
public abstract class Account implements Transactable {
    public static final String ACTIVE = "ACTIVE", CLOSED = "CLOSED";

    private final String accountNumber;
    private final String accountType;
    private final String customerId;           // an account cannot exist without a customer
    private final BankBranch branch;           // associated with 1 branch
    private double balance;
    private String status = ACTIVE;
    private final List<Transaction> transactions = new ArrayList<>();   // records 0..* transactions

    protected Account(String accountNumber, AccountType type, BankBranch branch, String customerId, double balance) {
        if (customerId == null || customerId.isBlank()) throw new BankException("An account must belong to a customer");
        if (branch == null) throw new BankException("An account must be associated with a branch");
        this.accountNumber = accountNumber;
        this.accountType = type.name();
        this.branch = branch;
        this.customerId = customerId;
        this.balance = balance;
    }

    public String getAccountNumber() { return accountNumber; }
    public String getAccountType() { return accountType; }
    public String getCustomerId() { return customerId; }
    public BankBranch getBranch() { return branch; }
    @Override public double getBalance() { return balance; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public List<Transaction> getTransactions() { return Collections.unmodifiableList(transactions); }
    public Transaction getLastTransaction() { return transactions.isEmpty() ? null : transactions.get(transactions.size() - 1); }

    /** Each concrete account defines its own interest. */
    public abstract double calculateInterest();

    @Override
    public abstract void withdraw(double amount);

    @Override
    public void deposit(double amount) { deposit(amount, "DEPOSIT"); }

    @Override
    public void deposit(double amount, String type) {
        requireUsable(amount);
        balance = round(balance + amount);
        transactions.add(Transaction.create(type, amount));
    }

    /** Pays this month's interest into the account. Returns the amount paid. */
    public double payInterest() {
        double interest = calculateInterest();
        if (interest > 0) deposit(interest, "INTEREST");
        return interest;
    }

    protected void debit(double amount) {
        requireUsable(amount);
        if (amount > balance) throw new BankException("Insufficient funds");
        balance = round(balance - amount);
        transactions.add(Transaction.create("WITHDRAWAL", amount));
    }

    protected double interestOnBalance(double rate) { return round(balance * rate); }

    private void requireUsable(double amount) {
        if (!ACTIVE.equals(status)) throw new BankException("Account is " + status);
        if (amount <= 0) throw new BankException("Amount must be greater than zero");
    }

    private static double round(double v) { return Math.round(v * 100) / 100.0; }

    @Override
    public String toString() { return accountType + " " + accountNumber + " (BWP " + String.format("%.2f", balance) + ")"; }
}
