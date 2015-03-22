package ru.fizteh.fivt.students.Oktosha.servlet.commands;

import ru.fizteh.fivt.students.Oktosha.commander.AbstractCommand;
import ru.fizteh.fivt.students.Oktosha.commander.CommandExecutionException;
import ru.fizteh.fivt.students.Oktosha.commander.Context;
import ru.fizteh.fivt.students.Oktosha.servlet.HTTPServer;
import ru.fizteh.fivt.students.Oktosha.servlet.ServletContextImpl;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * Created by DKolodzey on 22.03.15.
 */
public class StartHttpCommand extends AbstractCommand {
    @Override
    public String getName() {
        return "starthttp";
    }

    @Override
    public void exec(Context context, List<String> arguments) throws CommandExecutionException {
        checkArguments(arguments, 2);
        HTTPServer server = context.getServer();
        if (server.isRunning()) {
            println(context, "not started: already started");
            return;
        }
        try {
            server.start(new InetSocketAddress(Integer.parseInt(arguments.get(1))),
                    new ServletContextImpl(context.getDiffManager(), context.getTableProvider()));
            println(context, "started at port" + arguments.get(1));
        } catch (Exception e) {
            throw new CommandExecutionException(e);
        }
    }
}
