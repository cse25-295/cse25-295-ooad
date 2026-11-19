package bank.model;

/** Creates new accounts and enforces the opening rules. */
public final class AccountFactory {
    private AccountFactory() { }

    public static Account create(AccountType type, String number, BankBranch branch, String customerId,
                                 double initialDeposit, String employerName, String employerAddress) {
        if (type == null) throw new BankException("Choose an account type");
        if (initialDeposit < 0) throw new BankException("Initial deposit cannot be negative");
        Account a;
        switch (type) {
            case SAVINGS -> a = new SavingsAccount(number, branch, customerId, 0);
            case INVESTMENT -> {
                InvestmentAccount inv = new InvestmentAccount(number, branch, customerId, 0);
                if (initialDeposit < inv.getMinimumDeposit())
                    throw new BankException("Investment account needs an initial deposit of at least BWP500.00");
                a = inv;
            }
            case CHEQUE -> a = new ChequeAccount(number, branch, customerId, 0, employerName, employerAddress);
            default -> throw new BankException("Unknown account type");
        }
        if (initialDeposit > 0) a.deposit(initialDeposit);
        return a;
    }
}
