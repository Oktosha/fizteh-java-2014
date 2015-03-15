package ru.fizteh.fivt.students.Oktosha.parallel_main.commands;

import ru.fizteh.fivt.students.Oktosha.commander.AbstractCommand;
import ru.fizteh.fivt.students.Oktosha.commander.Command;
import ru.fizteh.fivt.students.Oktosha.commander.CommandExecutionException;
import ru.fizteh.fivt.students.Oktosha.commander.Context;

import java.util.List;

/**
 * Created by DKolodzey on 15.03.15.
 */
public class RollbackCommand extends AbstractCommand implements Command {
    @Override
    public String getName() {
        return "rollback";
    }

    @Override
    public void exec(Context context, List<String> arguments) throws CommandExecutionException {
        checkArguments(arguments, 1);
        checkCurrentTableNotNull(context);
        println(context, String.valueOf(context.getCurrentTable().rollback()));
    }
}
