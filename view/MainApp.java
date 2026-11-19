package bank.view;

import bank.controller.AccountController;
import bank.controller.LoginController;
import bank.dao.AccountDAO;
import bank.dao.BranchDAO;
import bank.dao.CustomerDAO;
import bank.dao.Database;
import bank.dao.TransactionDAO;
import bank.model.Customer;
import bank.util.SeedData;
import javafx.application.Application;
import javafx.stage.Stage;

/** Entry point: wires views, controllers and DAOs together. */
public class MainApp extends Application {
    private final CustomerDAO customerDAO = new CustomerDAO();
    private final AccountDAO accountDAO = new AccountDAO();
    private final TransactionDAO transactionDAO = new TransactionDAO();
    private final BranchDAO branchDAO = new BranchDAO();

    @Override
    public void start(Stage stage) {
        Database.init();
        SeedData.loadIfEmpty(customerDAO, accountDAO, transactionDAO, branchDAO);
        showLogin(stage);
        stage.show();
    }

    private void showLogin(Stage stage) {
        stage.setTitle("Banking System - Login");
        stage.setScene(new LoginView(new LoginController(customerDAO), c -> showAccounts(stage, c)).build());
    }

    private void showAccounts(Stage stage, Customer customer) {
        stage.setTitle("Banking System - Accounts");
        AccountController ac = new AccountController(customer, accountDAO, transactionDAO, branchDAO);
        stage.setScene(new AccountView(ac, () -> showLogin(stage)).build());
    }

    public static void main(String[] args) { launch(args); }
}
