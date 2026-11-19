package bank.util;

import bank.controller.AccountController;
import bank.controller.LoginController;
import bank.dao.*;
import bank.model.AccountType;
import bank.model.BankBranch;
import bank.model.Customer;

/** Inserts branches and 10 sample customers (with accounts) when the database is empty. Login: user1..user10 / pass1234. */
public final class SeedData {
    private SeedData() { }

    public static void loadIfEmpty(CustomerDAO cDao, AccountDAO aDao, TransactionDAO tDao, BranchDAO bDao) {
        if (bDao.findAll().isEmpty()) {
            bDao.create(new BankBranch("GBE01", "Main Branch", "Gaborone"));
            bDao.create(new BankBranch("FTN01", "Francistown Branch", "Francistown"));
        }
        if (!cDao.findAll().isEmpty()) return;
        BankBranch main = bDao.findByCode("GBE01"), ftn = bDao.findByCode("FTN01");
        LoginController login = new LoginController(cDao);
        String[] first = {"Thabo", "Neo", "Kabo", "Lesedi", "Mpho", "Tumelo", "Boitumelo", "Onkabetse", "Keabetswe", "Goitseone"};
        String[] last = {"Molefe", "Kgosi", "Seretse", "Mogae", "Masire", "Khama", "Motsumi", "Mmusi", "Tau", "Dube"};
        for (int i = 0; i < 10; i++) {
            Customer c = login.register(first[i], last[i], "Plot " + (100 + i) + ", Gaborone", "user" + (i + 1), "pass1234");
            AccountController ac = new AccountController(c, aDao, tDao, bDao);
            ac.openAccount(AccountType.SAVINGS, main, 200.0 * (i + 1), null, null);
            if (i % 2 == 0) ac.openAccount(AccountType.INVESTMENT, main, 500.0 + 100 * i, null, null);
            if (i % 3 == 0) ac.openAccount(AccountType.CHEQUE, ftn, 1000.0, "Botswana Power Corporation", "Plot 1, Gaborone");
        }
    }
}
