package bank.dao;

import bank.model.BankException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/** Connection factory and schema creation (H2). */
public final class Database {
    private static String url = "jdbc:h2:./data/bank";

    private Database() { }

    public static void setUrl(String newUrl) { url = newUrl; }

    public static Connection get() throws SQLException {
        return DriverManager.getConnection(url, "sa", "");
    }

    public static void init() {
        try (Connection c = get(); Statement s = c.createStatement()) {
            s.execute("""
                CREATE TABLE IF NOT EXISTS bank_branches (
                  branch_code VARCHAR(10) PRIMARY KEY,
                  branch_name VARCHAR(60) NOT NULL,
                  branch_location VARCHAR(100) NOT NULL)""");
            s.execute("""
                CREATE TABLE IF NOT EXISTS customers (
                  customer_id VARCHAR(12) PRIMARY KEY,
                  first_name VARCHAR(50) NOT NULL,
                  surname VARCHAR(50) NOT NULL,
                  address VARCHAR(150) NOT NULL,
                  username VARCHAR(30) NOT NULL UNIQUE,
                  password VARCHAR(64) NOT NULL)""");
            s.execute("""
                CREATE TABLE IF NOT EXISTS accounts (
                  account_number VARCHAR(20) PRIMARY KEY,
                  account_type VARCHAR(12) NOT NULL,
                  balance DECIMAL(15,2) NOT NULL,
                  status VARCHAR(10) NOT NULL,
                  customer_id VARCHAR(12) NOT NULL,
                  branch_code VARCHAR(10) NOT NULL,
                  employer_name VARCHAR(100),
                  employer_address VARCHAR(150),
                  FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE,
                  FOREIGN KEY (branch_code) REFERENCES bank_branches(branch_code))""");
            s.execute("""
                CREATE TABLE IF NOT EXISTS transactions (
                  transaction_id VARCHAR(20) PRIMARY KEY,
                  account_number VARCHAR(20) NOT NULL,
                  type VARCHAR(12) NOT NULL,
                  amount DECIMAL(15,2) NOT NULL,
                  tx_date TIMESTAMP NOT NULL,
                  FOREIGN KEY (account_number) REFERENCES accounts(account_number) ON DELETE CASCADE)""");
        } catch (SQLException e) {
            throw new BankException("Could not initialise database", e);
        }
    }
}
