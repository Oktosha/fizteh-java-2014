package ru.fizteh.fivt.students.Oktosha.parallel_main.commands;

import ru.fizteh.fivt.students.Oktosha.commander.AbstractCommand;
import ru.fizteh.fivt.students.Oktosha.commander.CommandExecutionException;
import ru.fizteh.fivt.students.Oktosha.commander.Context;

import java.util.List;

/**
 * Created by DKolodzey on 16.03.15.
 */
public class ShowCommand extends AbstractCommand {
    @Override
    public String getName() {
        return "show";
    }

    @Override
    public void exec(Context context, List<String> arguments) throws CommandExecutionException {
        if (arguments.size() != 2 || !arguments.get(0).equals(getName()) || !arguments.get(1).equals("tables")) {
            throw new CommandExecutionException(getName() + " is given unexpected arguments: " + arguments.toString());
        }
        for (String tableName : context.getTableProvider().getTableNames()) {
            println(context, tableName + " " + context.getTableProvider().getTable(tableName).size());
        }

    }
}
