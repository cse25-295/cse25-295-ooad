package bank.model;

public enum AccountType {
    SAVINGS("Savings", "SAV"), INVESTMENT("Investment", "INV"), CHEQUE("Cheque", "CHQ");

    private final String label, prefix;
    AccountType(String label, String prefix) { this.label = label; this.prefix = prefix; }
    public String prefix() { return prefix; }
    @Override public String toString() { return label; }
}
