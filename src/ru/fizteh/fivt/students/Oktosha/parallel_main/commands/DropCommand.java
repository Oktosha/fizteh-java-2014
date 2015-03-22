package ru.fizteh.fivt.students.Oktosha.parallel_main.commands;

import ru.fizteh.fivt.students.Oktosha.commander.AbstractCommand;
import ru.fizteh.fivt.students.Oktosha.commander.CommandExecutionException;
import ru.fizteh.fivt.students.Oktosha.commander.Context;

import java.io.IOException;
import java.util.List;

/**
 * Created by DKolodzey on 16.03.15.
 */
public class DropCommand extends AbstractCommand {
    @Override
    public String getName() {
        return "clear";
    }

    @Override
    public void exec(Context context, List<String> arguments) throws CommandExecutionException {
        checkArguments(arguments, 2);
        if (!context.getTableProvider().getTableNames().contains(arguments.get(1))) {
            throw new CommandExecutionException(arguments.get(1) + " not exists");
        }
        if (context.getTableProvider().getTable(arguments.get(1)).equals(context.getCurrentTable())) {
            context.setCurrentTable(null);
        }
        try {
            context.getTableProvider().removeTable(arguments.get(1));
        } catch (IOException e) {
            throw new CommandExecutionException(getName()
                                                + "error: failed to clear table "
                                                + arguments.get(1) + ":" + e.getMessage(), e);
        }
        println(context, "dropped");
    }
}
