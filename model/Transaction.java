package bank.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Transaction {
    private final String transactionId;
    private final String type;          // DEPOSIT, WITHDRAWAL, INTEREST
    private final double amount;
    private final LocalDateTime date;

    public Transaction(String transactionId, String type, double amount, LocalDateTime date) {
        this.transactionId = transactionId; this.type = type; this.amount = amount; this.date = date;
    }

    public static Transaction create(String type, double amount) {
        return new Transaction("TX" + UUID.randomUUID().toString().substring(0, 12).toUpperCase(), type, amount, LocalDateTime.now());
    }

    public String getTransactionId() { return transactionId; }
    public String getType() { return type; }
    public double getAmount() { return amount; }
    public LocalDateTime getDate() { return date; }

    public String getDetails() {
        return String.format("%s  %-10s BWP %10.2f  [%s]", date.toLocalDate() + " " + date.toLocalTime().withNano(0), type, amount, transactionId);
    }
}
