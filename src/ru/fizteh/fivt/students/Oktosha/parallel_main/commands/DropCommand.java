package ru.fizteh.fivt.students.Oktosha.parallel_main.commands;

import ru.fizteh.fivt.students.Oktosha.commander.AbstractCommand;
import ru.fizteh.fivt.students.Oktosha.commander.CommandExecutionException;
import ru.fizteh.fivt.students.Oktosha.commander.Context;
import ru.fizteh.fivt.students.Oktosha.database.storeable.DroppableStructuredTable;

import java.io.IOException;
import java.util.List;

/**
 * Created by DKolodzey on 16.03.15.
 */
public class DropCommand extends AbstractCommand {
    @Override
    public String getName() {
        return "drop";
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
            DroppableStructuredTable table = context.getTableProvider().getTable(arguments.get(1));
            context.getTableProvider().removeTable(arguments.get(1));
            context.getDiffManager().freeDiffsForTable(table);
        } catch (IOException e) {
            throw new CommandExecutionException(getName()
                                                + "error: failed to drop table "
                                                + arguments.get(1) + ":" + e.getMessage(), e);
        }
        println(context, "dropped");
    }
}
