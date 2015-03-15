package ru.fizteh.fivt.students.Oktosha.parallel_main.commands;

import ru.fizteh.fivt.students.Oktosha.commander.AbstractCommand;
import ru.fizteh.fivt.students.Oktosha.commander.CommandExecutionException;
import ru.fizteh.fivt.students.Oktosha.commander.Context;

import java.io.IOException;
import java.util.List;

/**
 * Created by DKolodzey on 16.03.15.
 */
public class CommitCommand extends AbstractCommand {
    @Override
    public String getName() {
        return "commit";
    }

    @Override
    public void exec(Context context, List<String> arguments) throws CommandExecutionException {
        checkArguments(arguments, 1);
        checkCurrentTableNotNull(context);
        try {
            println(context, String.valueOf(context.getCurrentTable().commit()));
        } catch (IOException e) {
            throw new CommandExecutionException(getName() + "error: failed to save database " + e.getMessage(), e);
        }
    }
}
