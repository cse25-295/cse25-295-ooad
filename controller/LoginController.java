package bank.controller;

import bank.dao.CustomerDAO;
import bank.model.BankException;
import bank.model.Customer;
import bank.util.PasswordUtil;

public class LoginController {
    private final CustomerDAO customerDAO;

    public LoginController(CustomerDAO customerDAO) { this.customerDAO = customerDAO; }

    public Customer register(String firstName, String surname, String address, String username, String password) {
        if (password == null || password.length() < 6)
            throw new BankException("Password must be at least 6 characters");
        Customer c = new Customer(null, trim(firstName), trim(surname), trim(address), trim(username), PasswordUtil.hash(password));
        c.register();                                              // domain validation
        if (customerDAO.findByUsername(c.getUsername()) != null)
            throw new BankException("Username already taken");
        c.setCustomerId(String.format("CUS%05d", customerDAO.count() + 1));
        customerDAO.create(c);
        return c;
    }

    public Customer login(String username, String password) {
        Customer c = username == null ? null : customerDAO.findByUsername(username.trim());
        if (c == null || !c.login(username, password)) throw new BankException("Invalid username or password");
        return c;
    }

    private static String trim(String s) { return s == null ? null : s.trim(); }
}
