package bank.dao;

import bank.model.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountDAO {

    @FunctionalInterface
    private interface SqlWork { void run(Connection con) throws SQLException; }

    private static final String SELECT =
            "SELECT a.*, b.branch_name, b.branch_location FROM accounts a JOIN bank_branches b ON a.branch_code = b.branch_code ";

    /** Inserts the account and all its recorded transactions atomically. */
    public void create(Account a) {
        inTransaction(con -> {
            String sql = "INSERT INTO accounts(account_number, account_type, balance, status, customer_id, branch_code, employer_name, employer_address) VALUES (?,?,?,?,?,?,?,?)";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, a.getAccountNumber());
                ps.setString(2, a.getAccountType());
                ps.setDouble(3, a.getBalance());
                ps.setString(4, a.getStatus());
                ps.setString(5, a.getCustomerId());
                ps.setString(6, a.getBranch().getBranchCode());
                if (a instanceof ChequeAccount c) {
                    ps.setString(7, c.getEmployerName());
                    ps.setString(8, c.getEmployerAddress());
                } else {
                    ps.setNull(7, Types.VARCHAR);
                    ps.setNull(8, Types.VARCHAR);
                }
                ps.executeUpdate();
            }
            for (Transaction t : a.getTransactions()) TransactionDAO.insert(con, a.getAccountNumber(), t);
        }, "Could not save account");
    }

    public Account findByNumber(String number) {
        List<Account> list = query(SELECT + "WHERE a.account_number = ?", number);
        return list.isEmpty() ? null : list.get(0);
    }

    public List<Account> findByCustomer(String customerId) {
        return query(SELECT + "WHERE a.customer_id = ? ORDER BY a.account_number", customerId);
    }

    /** Plain CRUD update of balance and status. */
    public void update(Account a) {
        inTransaction(con -> updateRow(con, a), "Could not update account");
    }

    /** Saves the new balance and the account's latest transaction atomically. */
    public void applyTransaction(Account a) {
        inTransaction(con -> {
            updateRow(con, a);
            if (a.getLastTransaction() != null) TransactionDAO.insert(con, a.getAccountNumber(), a.getLastTransaction());
        }, "Could not save transaction");
    }

    public void delete(String number) {
        inTransaction(con -> {
            try (PreparedStatement ps = con.prepareStatement("DELETE FROM accounts WHERE account_number = ?")) {
                ps.setString(1, number);
                ps.executeUpdate();
            }
        }, "Could not delete account");
    }

    public int count() {
        try (Connection con = Database.get(); Statement s = con.createStatement();
             ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM accounts")) {
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            throw new BankException("Could not count accounts", e);
        }
    }

    private void updateRow(Connection con, Account a) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement("UPDATE accounts SET balance = ?, status = ? WHERE account_number = ?")) {
            ps.setDouble(1, a.getBalance());
            ps.setString(2, a.getStatus());
            ps.setString(3, a.getAccountNumber());
            ps.executeUpdate();
        }
    }

    private List<Account> query(String sql, String param) {
        List<Account> list = new ArrayList<>();
        try (Connection con = Database.get(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, param);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new BankException("Could not read accounts", e);
        }
    }

    private Account map(ResultSet rs) throws SQLException {
        String number = rs.getString("account_number");
        String customerId = rs.getString("customer_id");
        double balance = rs.getDouble("balance");
        BankBranch branch = new BankBranch(rs.getString("branch_code"), rs.getString("branch_name"), rs.getString("branch_location"));
        Account a = switch (AccountType.valueOf(rs.getString("account_type"))) {
            case SAVINGS -> new SavingsAccount(number, branch, customerId, balance);
            case INVESTMENT -> new InvestmentAccount(number, branch, customerId, balance);
            case CHEQUE -> new ChequeAccount(number, branch, customerId, balance,
                    rs.getString("employer_name"), rs.getString("employer_address"));
        };
        a.setStatus(rs.getString("status"));
        return a;
    }

    private void inTransaction(SqlWork work, String errorMessage) {
        try (Connection con = Database.get()) {
            con.setAutoCommit(false);
            try {
                work.run(con);
                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new BankException(errorMessage, e);
        }
    }
}
