package bank.controller;

import bank.dao.AccountDAO;
import bank.dao.BranchDAO;
import bank.dao.TransactionDAO;
import bank.model.*;
import java.util.List;

/** Mediates between the account screens and the core model / DAOs for one logged-in customer. */
public class AccountController {
    private final Customer customer;
    private final AccountDAO accountDAO;
    private final TransactionDAO transactionDAO;
    private final BranchDAO branchDAO;

    public AccountController(Customer customer, AccountDAO accountDAO, TransactionDAO transactionDAO, BranchDAO branchDAO) {
        this.customer = customer;
        this.accountDAO = accountDAO;
        this.transactionDAO = transactionDAO;
        this.branchDAO = branchDAO;
    }

    public Customer getCustomer() { return customer; }

    public List<BankBranch> getBranches() { return branchDAO.findAll(); }

    public Account openAccount(AccountType type, BankBranch branch, double initialDeposit, String employerName, String employerAddress) {
        if (type == null) throw new BankException("Choose an account type");
        if (branch == null) throw new BankException("Choose a branch");
        String number = String.format("%s%07d", type.prefix(), accountDAO.count() + 1);
        Account a = customer.openAccount(type, number, branch, initialDeposit, employerName, employerAddress);
        accountDAO.create(a);
        return a;
    }

    public List<Account> getAccounts() { return accountDAO.findByCustomer(customer.getCustomerId()); }

    public Account deposit(String accountNumber, double amount) {
        Account a = owned(accountNumber);
        a.deposit(amount);
        accountDAO.applyTransaction(a);
        return a;
    }

    public Account withdraw(String accountNumber, double amount) {
        Account a = owned(accountNumber);
        a.withdraw(amount);
        accountDAO.applyTransaction(a);
        return a;
    }

    /** Pays interest into every account the customer holds (cheque accounts earn none). Returns the total paid. */
    public double payMonthlyInterest() {
        double total = 0;
        for (Account a : getAccounts()) {
            double paid = a.payInterest();
            if (paid > 0) {
                accountDAO.applyTransaction(a);
                total += paid;
            }
        }
        return total;
    }

    public List<Transaction> getHistory(String accountNumber) {
        return transactionDAO.findByAccount(owned(accountNumber).getAccountNumber());
    }

    private Account owned(String accountNumber) {
        Account a = accountDAO.findByNumber(accountNumber);
        if (a == null || !a.getCustomerId().equals(customer.getCustomerId())) throw new BankException("Account not found");
        return a;
    }
}
