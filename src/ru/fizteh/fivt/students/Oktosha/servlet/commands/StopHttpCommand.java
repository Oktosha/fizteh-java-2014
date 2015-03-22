package ru.fizteh.fivt.students.Oktosha.servlet.commands;

import ru.fizteh.fivt.students.Oktosha.commander.AbstractCommand;
import ru.fizteh.fivt.students.Oktosha.commander.CommandExecutionException;
import ru.fizteh.fivt.students.Oktosha.commander.Context;
import ru.fizteh.fivt.students.Oktosha.servlet.HTTPServer;

import java.util.List;

/**
 * Created by DKolodzey on 22.03.15.
 */
public class StopHttpCommand extends AbstractCommand {
    @Override
    public String getName() {
        return "stophttp";
    }

    @Override
    public void exec(Context context, List<String> arguments) throws CommandExecutionException {
        checkArguments(arguments, 1);
        HTTPServer server = context.getServer();
        if (!server.isRunning()) {
            throw new CommandExecutionException("not started");
        }
        try {
            server.stop();
        } catch (Exception e) {
            throw new CommandExecutionException(e);
        }
        println(context, "stopped");
    }
}
