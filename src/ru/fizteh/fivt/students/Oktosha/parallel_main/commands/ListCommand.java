package ru.fizteh.fivt.students.Oktosha.parallel_main.commands;

import ru.fizteh.fivt.students.Oktosha.commander.AbstractCommand;
import ru.fizteh.fivt.students.Oktosha.commander.CommandExecutionException;
import ru.fizteh.fivt.students.Oktosha.commander.Context;

import java.util.List;

/**
 * Created by DKolodzey on 15.03.15.
 * implements list command
 */
public class ListCommand extends AbstractCommand {
    @Override
    public String getName() {
        return "list";
    }

    @Override
    public void exec(Context context, List<String> arguments) throws CommandExecutionException {
        checkArguments(arguments, 1);
        checkCurrentTableNotNull(context);
        println(context, String.join(", ", context.getCurrentTable().list()));
    }

}
