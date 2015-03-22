package ru.fizteh.fivt.students.Oktosha.commander;

/**
 * Created by DKolodzey on 13.03.15.
 * Exception which is thrown during command execution
 */
public class CommandExecutionException extends Exception {

    public CommandExecutionException(String s) {
        super(s);
    }

    public CommandExecutionException(String message, Throwable cause) {
        super(message, cause);
    }

    public CommandExecutionException(Throwable cause) {
        super(cause);
    }

}
