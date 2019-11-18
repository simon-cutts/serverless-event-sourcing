package com.sighware.customer.error;

public class CustomerUpdateException extends Exception {
    public CustomerUpdateException() {
    }

    public CustomerUpdateException(String message) {
        super(message);
    }

    public CustomerUpdateException(String message, Throwable cause) {
        super(message, cause);
    }

    public CustomerUpdateException(Throwable cause) {
        super(cause);
    }

    public CustomerUpdateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
