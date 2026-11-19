package bank.model;

/** Unchecked exception for all business-rule and persistence failures. */
public class BankException extends RuntimeException {
    public BankException(String message) { super(message); }
    public BankException(String message, Throwable cause) { super(message, cause); }
}
