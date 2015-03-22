package ru.fizteh.fivt.students.Oktosha.database;

/**
 * Created by DKolodzey on 22.03.15.
 */
public class PoolIsFullException extends Throwable {
    public PoolIsFullException() {
    }

    public PoolIsFullException(Throwable cause) {
        super(cause);
    }

    public PoolIsFullException(String message) {
        super(message);
    }

    public PoolIsFullException(String message, Throwable cause) {
        super(message, cause);
    }

    public PoolIsFullException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
