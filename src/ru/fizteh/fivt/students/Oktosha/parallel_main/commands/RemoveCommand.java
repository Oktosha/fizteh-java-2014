package ru.fizteh.fivt.students.Oktosha.parallel_main.commands;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.Oktosha.commander.AbstractCommand;
import ru.fizteh.fivt.students.Oktosha.commander.CommandExecutionException;
import ru.fizteh.fivt.students.Oktosha.commander.Context;

import java.util.List;

/**
 * Created by DKolodzey on 16.03.15.
 */
public class RemoveCommand extends AbstractCommand {
    @Override
    public String getName() {
        return "remove";
    }

    @Override
    public void exec(Context context, List<String> arguments) throws CommandExecutionException {
        checkArguments(arguments, 2);
        checkCurrentTableNotNull(context);
        println(context, context.getCurrentTable().remove(arguments.get(1)) == null ? "not found" : "removed");
    }
}
