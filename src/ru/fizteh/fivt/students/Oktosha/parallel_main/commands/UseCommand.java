package ru.fizteh.fivt.students.Oktosha.parallel_main.commands;

import ru.fizteh.fivt.students.Oktosha.commander.AbstractCommand;
import ru.fizteh.fivt.students.Oktosha.commander.CommandExecutionException;
import ru.fizteh.fivt.students.Oktosha.commander.Context;

import java.util.List;

/**
 * Created by DKolodzey on 16.03.15.
 */
public class UseCommand extends AbstractCommand {
    @Override
    public String getName() {
        return "use";
    }

    @Override
    public void exec(Context context, List<String> arguments) throws CommandExecutionException {
        checkArguments(arguments, 2);
        if (context.getTableProvider().getTable(arguments.get(1)) == null) {
            throw new CommandExecutionException(arguments.get(1) + " not exists");
        }
        if (context.getCurrentTable() != null && context.getCurrentTable().getNumberOfUncommittedChanges() != 0) {
            throw new CommandExecutionException(context.getCurrentTable().getNumberOfUncommittedChanges()
                                                + " unsaved changes");
        }
        context.setCurrentTable(context.getTableProvider().getTable(arguments.get(1)));
        println(context, "using " + arguments.get(1));
    }
}
