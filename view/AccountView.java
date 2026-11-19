package bank.view;

import bank.controller.AccountController;
import bank.model.Account;
import bank.model.AccountType;
import bank.model.BankBranch;
import bank.model.BankException;
import bank.model.Transaction;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.util.stream.Collectors;

/** Boundary class: displays a customer's accounts and collects input. Talks only to AccountController. */
public class AccountView {
    private final AccountController controller;
    private final Runnable onLogout;
    private final TableView<Account> table = new TableView<>();
    private final Label status = new Label();

    public AccountView(AccountController controller, Runnable onLogout) {
        this.controller = controller;
        this.onLogout = onLogout;
    }

    public Scene build() {
        table.getColumns().add(column("Account No", a -> a.getAccountNumber(), 130));
        table.getColumns().add(column("Type", a -> a.getAccountType(), 100));
        table.getColumns().add(column("Branch", a -> a.getBranch().getBranchName(), 120));
        table.getColumns().add(column("Balance (BWP)", a -> String.format("%.2f", a.getBalance()), 130));

        Button open = new Button("Open Account");
        Button deposit = new Button("Deposit");
        Button withdraw = new Button("Withdraw");
        Button interest = new Button("Pay Monthly Interest");
        Button history = new Button("History");
        Button logout = new Button("Logout");

        open.setOnAction(e -> showOpenDialog());
        deposit.setOnAction(e -> askAmount("Deposit").ifPresent(s ->
                run(() -> controller.deposit(selected().getAccountNumber(), Double.parseDouble(s.trim())))));
        withdraw.setOnAction(e -> askAmount("Withdraw").ifPresent(s ->
                run(() -> controller.withdraw(selected().getAccountNumber(), Double.parseDouble(s.trim())))));
        interest.setOnAction(e -> run(() -> status.setText(
                String.format("Interest paid: BWP %.2f", controller.payMonthlyInterest()))));
        history.setOnAction(e -> run(this::showHistory));
        logout.setOnAction(e -> onLogout.run());

        Label welcome = new Label("Welcome, " + controller.getCustomer().getFullName());
        welcome.setStyle("-fx-font-size:18px; -fx-font-weight:bold;");
        VBox root = new VBox(10, welcome, table, new HBox(8, open, deposit, withdraw, interest, history, logout), status);
        root.setPadding(new Insets(15));
        refresh();
        return new Scene(root, 760, 440);
    }

    private TableColumn<Account, String> column(String title, java.util.function.Function<Account, String> value, int width) {
        TableColumn<Account, String> c = new TableColumn<>(title);
        c.setCellValueFactory(cell -> new SimpleStringProperty(value.apply(cell.getValue())));
        c.setPrefWidth(width);
        return c;
    }

    private void refresh() {
        table.setItems(FXCollections.observableArrayList(controller.getAccounts()));
    }

    private Account selected() {
        Account a = table.getSelectionModel().getSelectedItem();
        if (a == null) throw new BankException("Select an account first");
        return a;
    }

    private void run(Runnable action) {
        try {
            status.setText("");
            action.run();
            refresh();
        } catch (BankException | NumberFormatException ex) {
            String text = ex instanceof NumberFormatException ? "Enter a valid number" : ex.getMessage();
            new Alert(Alert.AlertType.ERROR, text).showAndWait();
        }
    }

    private java.util.Optional<String> askAmount(String title) {
        TextInputDialog d = new TextInputDialog();
        d.setTitle(title);
        d.setHeaderText(null);
        d.setContentText("Amount (BWP):");
        return d.showAndWait();
    }

    private void showOpenDialog() {
        Dialog<ButtonType> d = new Dialog<>();
        d.setTitle("Open account");
        ChoiceBox<AccountType> type = new ChoiceBox<>(FXCollections.observableArrayList(AccountType.values()));
        type.setValue(AccountType.SAVINGS);
        ChoiceBox<BankBranch> branch = new ChoiceBox<>(FXCollections.observableArrayList(controller.getBranches()));
        branch.getSelectionModel().selectFirst();
        TextField deposit = new TextField("0");
        TextField employer = new TextField(), employerAddress = new TextField();
        employer.setPromptText("Cheque accounts only");
        employerAddress.setPromptText("Cheque accounts only");
        GridPane g = new GridPane();
        g.setHgap(8);
        g.setVgap(8);
        g.setPadding(new Insets(15));
        g.addRow(0, new Label("Type"), type);
        g.addRow(1, new Label("Branch"), branch);
        g.addRow(2, new Label("Initial deposit"), deposit);
        g.addRow(3, new Label("Employer"), employer);
        g.addRow(4, new Label("Employer address"), employerAddress);
        d.getDialogPane().setContent(g);
        d.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        d.showAndWait().filter(b -> b == ButtonType.OK).ifPresent(b -> run(() ->
                controller.openAccount(type.getValue(), branch.getValue(), Double.parseDouble(deposit.getText().trim()),
                        employer.getText(), employerAddress.getText())));
    }

    private void showHistory() {
        Account a = selected();
        String text = controller.getHistory(a.getAccountNumber()).stream()
                .map(Transaction::getDetails)
                .collect(Collectors.joining("\n"));
        TextArea area = new TextArea(text);
        area.setEditable(false);
        area.setStyle("-fx-font-family: monospace;");
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("History - " + a.getAccountNumber());
        alert.setHeaderText(null);
        alert.getDialogPane().setContent(area);
        alert.showAndWait();
    }
}
