package bank.dao;

import bank.model.BankException;
import bank.model.Customer;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {

    public void create(Customer c) {
        String sql = "INSERT INTO customers(customer_id, first_name, surname, address, username, password) VALUES (?,?,?,?,?,?)";
        try (Connection con = Database.get(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getCustomerId());
            ps.setString(2, c.getFirstName());
            ps.setString(3, c.getSurname());
            ps.setString(4, c.getAddress());
            ps.setString(5, c.getUsername());
            ps.setString(6, c.getPassword());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new BankException("Could not save customer", e);
        }
    }

    public Customer findById(String id) { return findOne("SELECT * FROM customers WHERE customer_id = ?", id); }

    public Customer findByUsername(String username) { return findOne("SELECT * FROM customers WHERE username = ?", username); }

    public List<Customer> findAll() {
        List<Customer> list = new ArrayList<>();
        try (Connection con = Database.get(); Statement s = con.createStatement();
             ResultSet rs = s.executeQuery("SELECT * FROM customers ORDER BY customer_id")) {
            while (rs.next()) list.add(map(rs));
            return list;
        } catch (SQLException e) {
            throw new BankException("Could not read customers", e);
        }
    }

    public int count() {
        try (Connection con = Database.get(); Statement s = con.createStatement();
             ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM customers")) {
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            throw new BankException("Could not count customers", e);
        }
    }

    public void update(Customer c) {
        String sql = "UPDATE customers SET first_name = ?, surname = ?, address = ? WHERE customer_id = ?";
        try (Connection con = Database.get(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getFirstName());
            ps.setString(2, c.getSurname());
            ps.setString(3, c.getAddress());
            ps.setString(4, c.getCustomerId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new BankException("Could not update customer", e);
        }
    }

    public void delete(String id) {
        try (Connection con = Database.get(); PreparedStatement ps = con.prepareStatement("DELETE FROM customers WHERE customer_id = ?")) {
            ps.setString(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new BankException("Could not delete customer", e);
        }
    }

    private Customer findOne(String sql, String param) {
        try (Connection con = Database.get(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, param);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        } catch (SQLException e) {
            throw new BankException("Could not read customer", e);
        }
    }

    private Customer map(ResultSet rs) throws SQLException {
        return new Customer(rs.getString("customer_id"), rs.getString("first_name"), rs.getString("surname"),
                rs.getString("address"), rs.getString("username"), rs.getString("password"));
    }
}
