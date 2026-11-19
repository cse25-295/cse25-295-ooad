package bank.model;

public class ChequeAccount extends Account {
    private final String employerName;
    private final String employerAddress;

    public ChequeAccount(String number, BankBranch branch, String customerId, double balance,
                         String employerName, String employerAddress) {
        super(number, AccountType.CHEQUE, branch, customerId, balance);
        if (employerName == null || employerName.isBlank() || employerAddress == null || employerAddress.isBlank())
            throw new BankException("Cheque account requires employer name and address");
        this.employerName = employerName;
        this.employerAddress = employerAddress;
    }

    public String getEmployerName() { return employerName; }
    public String getEmployerAddress() { return employerAddress; }

    @Override public double calculateInterest() { return 0; }   // cheque accounts earn no interest

    @Override
    public void withdraw(double amount) { debit(amount); }
}
