package bank.model;

public class BankBranch {
    private final String branchCode, branchName, branchLocation;

    public BankBranch(String branchCode, String branchName, String branchLocation) {
        this.branchCode = branchCode; this.branchName = branchName; this.branchLocation = branchLocation;
    }

    public String getBranchCode() { return branchCode; }
    public String getBranchName() { return branchName; }
    public String getBranchLocation() { return branchLocation; }
    public String getBranchDetails() { return branchName + " (" + branchCode + "), " + branchLocation; }
    @Override public String toString() { return branchName; }
}
