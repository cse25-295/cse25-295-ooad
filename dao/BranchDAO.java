package bank.dao;

import bank.model.BankBranch;
import bank.model.BankException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BranchDAO {

    public void create(BankBranch b) {
        try (Connection con = Database.get();
             PreparedStatement ps = con.prepareStatement("INSERT INTO bank_branches(branch_code, branch_name, branch_location) VALUES (?,?,?)")) {
            ps.setString(1, b.getBranchCode());
            ps.setString(2, b.getBranchName());
            ps.setString(3, b.getBranchLocation());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new BankException("Could not save branch", e);
        }
    }

    public BankBranch findByCode(String code) {
        try (Connection con = Database.get(); PreparedStatement ps = con.prepareStatement("SELECT * FROM bank_branches WHERE branch_code = ?")) {
            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        } catch (SQLException e) {
            throw new BankException("Could not read branch", e);
        }
    }

    public List<BankBranch> findAll() {
        List<BankBranch> list = new ArrayList<>();
        try (Connection con = Database.get(); Statement s = con.createStatement();
             ResultSet rs = s.executeQuery("SELECT * FROM bank_branches ORDER BY branch_code")) {
            while (rs.next()) list.add(map(rs));
            return list;
        } catch (SQLException e) {
            throw new BankException("Could not read branches", e);
        }
    }

    public void delete(String code) {
        try (Connection con = Database.get(); PreparedStatement ps = con.prepareStatement("DELETE FROM bank_branches WHERE branch_code = ?")) {
            ps.setString(1, code);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new BankException("Could not delete branch", e);
        }
    }

    static BankBranch map(ResultSet rs) throws SQLException {
        return new BankBranch(rs.getString("branch_code"), rs.getString("branch_name"), rs.getString("branch_location"));
    }
}
