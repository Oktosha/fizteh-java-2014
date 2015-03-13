package ru.fizteh.fivt.students.Oktosha.commander;

/**
 * Created by DKolodzey on 13.03.15.
 * thrown by argument parser
 */
public class ArgumentParsingException extends Exception {
    public ArgumentParsingException(Throwable cause) {
        super(cause);
    }

    public ArgumentParsingException(String message) {
        super(message);
    }

    public ArgumentParsingException(String message, Throwable cause) {
        super(message, cause);
    }
}
