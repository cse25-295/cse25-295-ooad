package bank.dao;

import bank.model.BankException;
import bank.model.Transaction;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {

    /** Used inside a caller-managed JDBC transaction. */
    static void insert(Connection con, String accountNumber, Transaction t) throws SQLException {
        String sql = "INSERT INTO transactions(transaction_id, account_number, type, amount, tx_date) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, t.getTransactionId());
            ps.setString(2, accountNumber);
            ps.setString(3, t.getType());
            ps.setDouble(4, t.getAmount());
            ps.setTimestamp(5, Timestamp.valueOf(t.getDate()));
            ps.executeUpdate();
        }
    }

    public void create(String accountNumber, Transaction t) {
        try (Connection con = Database.get()) {
            insert(con, accountNumber, t);
        } catch (SQLException e) {
            throw new BankException("Could not save transaction", e);
        }
    }

    public List<Transaction> findByAccount(String accountNumber) {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE account_number = ? ORDER BY tx_date, transaction_id";
        try (Connection con = Database.get(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, accountNumber);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Transaction(rs.getString("transaction_id"), rs.getString("type"),
                            rs.getDouble("amount"), rs.getTimestamp("tx_date").toLocalDateTime()));
                }
            }
            return list;
        } catch (SQLException e) {
            throw new BankException("Could not read transactions", e);
        }
    }
}
