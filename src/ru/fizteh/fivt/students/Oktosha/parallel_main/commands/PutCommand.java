package ru.fizteh.fivt.students.Oktosha.parallel_main.commands;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.Oktosha.commander.AbstractCommand;
import ru.fizteh.fivt.students.Oktosha.commander.CommandExecutionException;
import ru.fizteh.fivt.students.Oktosha.commander.Context;

import java.text.ParseException;
import java.util.List;

/**
 * Created by DKolodzey on 16.03.15.
 */
public class PutCommand extends AbstractCommand {
    @Override
    public String getName() {
        return "put";
    }

    @Override
    public void exec(Context context, List<String> arguments) throws CommandExecutionException {
        checkArgumentsAtLeast(arguments, 3);
        checkCurrentTableNotNull(context);
        String serializedValue = String.join(" ", arguments.subList(2, arguments.size()));
        Storeable value;
        try {
            value = context.getTableProvider().deserialize(context.getCurrentTable(), serializedValue);
        } catch (ParseException e) {
            throw new CommandExecutionException("wrong type (" + e.getMessage() + ")", e);
        }
        Storeable oldValue = context.getCurrentTable().put(arguments.get(1), value);
        println(context,
                oldValue == null ? "new"
                        : "overwrite " + context.getTableProvider().serialize(context.getCurrentTable(), oldValue));
    }
}
