package ru.fizteh.fivt.students.Oktosha.commander;

import java.io.IOException;
import java.util.List;

/**
 * Created by DKolodzey on 16.03.15.
 * Abstract helper superclass
 */
public abstract class AbstractCommand implements Command {
    protected void println(Context context, String line) throws CommandExecutionException {
        try {
            context.getOutputWriter().write(line + "\n");
            context.getOutputWriter().flush();
        } catch (IOException e) {
            throw new CommandExecutionException(getName() + " error: exception occurred during output", e);
        }
    }

    protected void checkArguments(List<String> arguments, int expectedCount) throws CommandExecutionException {
        if (arguments.size() != expectedCount || !arguments.get(0).equals(getName())) {
            throw new CommandExecutionException(getName() + " is given unexpected arguments: " + arguments.toString());
        }
    }

    protected void checkCurrentTableNotNull(Context context) throws CommandExecutionException {
        if (context.getCurrentTable() == null) {
            throw new CommandExecutionException("no table");
        }
    }

    protected void checkArgumentsAtLeast(List<String> arguments, int argumentsCount) throws CommandExecutionException {
        if (arguments.size() < argumentsCount || !arguments.get(0).equals(getName())) {
            throw new CommandExecutionException(getName() + " is given unexpected arguments: " + arguments.toString());
        }
    }
}
