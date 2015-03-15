package ru.fizteh.fivt.students.Oktosha.commander;

import ru.fizteh.fivt.storage.structured.Storeable;

import java.util.List;

/**
 * Created by DKolodzey on 16.03.15.
 */
public class GetCommand extends AbstractCommand {
    @Override
    public String getName() {
        return "get";
    }

    @Override
    public void exec(Context context, List<String> arguments) throws CommandExecutionException {
        checkArguments(arguments, 2);
        checkCurrentTableNotNull(context);
        Storeable value = context.getCurrentTable().get(arguments.get(1));
        println(context, value == null
                ? "not found"
                : "found " + context.getTableProvider().serialize(context.getCurrentTable(), value));
    }
}
