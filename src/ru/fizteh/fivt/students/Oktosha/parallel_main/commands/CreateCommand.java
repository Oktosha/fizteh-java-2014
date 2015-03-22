package ru.fizteh.fivt.students.Oktosha.parallel_main.commands;

import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.Oktosha.commander.AbstractCommand;
import ru.fizteh.fivt.students.Oktosha.commander.Command;
import ru.fizteh.fivt.students.Oktosha.commander.CommandExecutionException;
import ru.fizteh.fivt.students.Oktosha.commander.Context;
import ru.fizteh.fivt.students.Oktosha.database.storeable.SignatureElement;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by DKolodzey on 15.03.15.
 */
public class CreateCommand extends AbstractCommand implements Command {
    @Override
    public String getName() {
        return "create";
    }

    @Override
    public void exec(Context context, List<String> arguments) throws CommandExecutionException {
        checkArgumentsAtLeast(arguments, 3);

        List<Class<?>> signature = new ArrayList<>();
        for (int i = 2; i < arguments.size(); ++i) {
            try {
                signature.add(SignatureElement.getSignatureElementByName(arguments.get(i)).getJavaClass());
            } catch (EnumConstantNotPresentException e) {
                throw new CommandExecutionException("wrong type (" + arguments.get(i) + ")", e);
            }
        }
        Table table;
        try {
            table = context.getTableProvider().createTable(arguments.get(1), signature);
        } catch (IOException e) {
            throw new CommandExecutionException(getName() + " error: " + e.getMessage(), e);
        }

        println(context, (table == null) ? arguments.get(1) + " exists" : "created");
    }
}
