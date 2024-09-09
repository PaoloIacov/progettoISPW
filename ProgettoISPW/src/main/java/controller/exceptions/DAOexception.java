package controller.exceptions;

public class DAOexception extends Exception {

    public DAOexception() {
        super();
    }

    public DAOexception(String message, Throwable cause) {
        super(message, cause);
    }

    public DAOexception(String message) {
        super(message);
    }
}