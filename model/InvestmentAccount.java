package bank.model;

public class InvestmentAccount extends Account {
    private final double interestRate = 0.05;      // 5% monthly
    private final double minimumDeposit = 500.00;

    public InvestmentAccount(String number, BankBranch branch, String customerId, double balance) {
        super(number, AccountType.INVESTMENT, branch, customerId, balance);
    }

    public double getMinimumDeposit() { return minimumDeposit; }

    @Override public double calculateInterest() { return interestOnBalance(interestRate); }

    @Override
    public void withdraw(double amount) { debit(amount); }
}
