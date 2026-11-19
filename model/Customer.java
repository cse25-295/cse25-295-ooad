package bank.model;

import bank.util.PasswordUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Customer {
    private String customerId;
    private String firstName, surname, address;
    private final String username;
    private final String password;                       // stored as a SHA-256 hash
    private final List<Account> accounts = new ArrayList<>();   // composition: holds 1..* accounts

    public Customer(String customerId, String firstName, String surname, String address, String username, String password) {
        this.customerId = customerId; this.firstName = firstName; this.surname = surname; this.address = address;
        this.username = username; this.password = password;
    }

    /** Validates the registration details. */
    public void register() {
        if (blank(firstName) || blank(surname) || blank(address) || blank(username))
            throw new BankException("All fields are required");
    }

    /** True when the credentials match this customer. */
    public boolean login(String username, String rawPassword) {
        return this.username.equals(username == null ? "" : username.trim())
                && rawPassword != null && password.equals(PasswordUtil.hash(rawPassword));
    }

    /** Opens a new account for this customer and adds it to the customer's accounts. */
    public Account openAccount(AccountType type, String accountNumber, BankBranch branch, double initialDeposit,
                               String employerName, String employerAddress) {
        Account a = AccountFactory.create(type, accountNumber, branch, customerId, initialDeposit, employerName, employerAddress);
        accounts.add(a);
        return a;
    }

    private static boolean blank(String s) { return s == null || s.isBlank(); }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String id) { this.customerId = id; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String v) { firstName = v; }
    public String getSurname() { return surname; }
    public void setSurname(String v) { surname = v; }
    public String getAddress() { return address; }
    public void setAddress(String v) { address = v; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public List<Account> getAccounts() { return Collections.unmodifiableList(accounts); }
    public String getFullName() { return firstName + " " + surname; }
}
