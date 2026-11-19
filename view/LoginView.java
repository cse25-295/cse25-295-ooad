package bank.view;

import bank.controller.LoginController;
import bank.model.BankException;
import bank.model.Customer;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.util.function.Consumer;

/** Boundary class: displays the login/register screen. No business logic. */
public class LoginView {
    private final LoginController controller;
    private final Consumer<Customer> onLogin;

    public LoginView(LoginController controller, Consumer<Customer> onLogin) {
        this.controller = controller;
        this.onLogin = onLogin;
    }

    public Scene build() {
        Label title = new Label("Bank Login");
        title.setStyle("-fx-font-size:22px; -fx-font-weight:bold;");
        TextField user = new TextField();
        user.setPromptText("Username");
        PasswordField pass = new PasswordField();
        pass.setPromptText("Password");
        Label msg = new Label();
        Button login = new Button("Login");
        login.setDefaultButton(true);
        Button register = new Button("Register");

        login.setOnAction(e -> {
            try {
                onLogin.accept(controller.login(user.getText(), pass.getText()));
            } catch (BankException ex) {
                msg.setText(ex.getMessage());
            }
        });
        register.setOnAction(e -> showRegister(msg));

        VBox box = new VBox(10, title, user, pass, new HBox(10, login, register), msg);
        box.setPadding(new Insets(30));
        box.setPrefWidth(340);
        return new Scene(box);
    }

    private void showRegister(Label msg) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Register new customer");
        TextField first = new TextField(), surname = new TextField(), address = new TextField(), username = new TextField();
        PasswordField password = new PasswordField();
        String[] labels = {"First name", "Surname", "Address", "Username", "Password"};
        Node[] fields = {first, surname, address, username, password};
        GridPane grid = new GridPane();
        grid.setHgap(8);
        grid.setVgap(8);
        grid.setPadding(new Insets(15));
        for (int i = 0; i < labels.length; i++) grid.addRow(i, new Label(labels[i]), fields[i]);
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().filter(b -> b == ButtonType.OK).ifPresent(b -> {
            try {
                controller.register(first.getText(), surname.getText(), address.getText(), username.getText(), password.getText());
                msg.setText("Registered. You can now log in.");
            } catch (BankException ex) {
                msg.setText(ex.getMessage());
            }
        });
    }
}
