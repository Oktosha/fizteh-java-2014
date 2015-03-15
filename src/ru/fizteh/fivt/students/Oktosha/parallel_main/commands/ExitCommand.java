package ru.fizteh.fivt.students.Oktosha.parallel_main.commands;

import ru.fizteh.fivt.students.Oktosha.commander.AbstractCommand;
import ru.fizteh.fivt.students.Oktosha.commander.CommandExecutionException;
import ru.fizteh.fivt.students.Oktosha.commander.Context;

import java.util.List;

/**
 * Created by DKolodzey on 15.03.15.
 */
public class ExitCommand extends AbstractCommand {
    @Override
    public String getName() {
        return "exit";
    }

    @Override
    public void exec(Context context, List<String> arguments) throws CommandExecutionException {
        checkArguments(arguments, 1);
        context.setExitRequestedToTrue();
    }
}
