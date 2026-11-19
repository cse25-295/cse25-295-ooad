package bank.model;

public class SavingsAccount extends Account {
    private final double interestRate = 0.0005;   // 0.05% monthly

    public SavingsAccount(String number, BankBranch branch, String customerId, double balance) {
        super(number, AccountType.SAVINGS, branch, customerId, balance);
    }

    @Override public double calculateInterest() { return interestOnBalance(interestRate); }

    @Override
    public void withdraw(double amount) { throw new BankException("Savings accounts do not allow withdrawals"); }
}
