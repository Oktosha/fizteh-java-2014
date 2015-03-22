package ru.fizteh.fivt.students.Oktosha.servlet;

import ru.fizteh.fivt.students.Oktosha.commander.*;
import ru.fizteh.fivt.students.Oktosha.database.tableprovider.ExtendedTableProvider;
import ru.fizteh.fivt.students.Oktosha.database.tableprovider.LoggingTableProviderFactory;
import ru.fizteh.fivt.students.Oktosha.database.tableprovider.SimpleTableProviderFactory;
import ru.fizteh.fivt.students.Oktosha.parallel_main.commands.*;
import ru.fizteh.fivt.students.Oktosha.servlet.commands.StartHttpCommand;
import ru.fizteh.fivt.students.Oktosha.servlet.commands.StopHttpCommand;

import java.io.*;
import java.text.ParseException;
import java.util.Arrays;

/**
 * Created by DKolodzey on 22.03.15.
 */
public class ServletMain {
    public static void main(String[] args) {
        String property = System.getProperty("fizteh.db.dir");
        if (property == null) {
            System.err.println("missing property fizteh.db.dir");
            System.exit(1);
            return;
        }

        ExtendedTableProvider tableProvider;
        try {
            tableProvider = new LoggingTableProviderFactory().create(property);
        } catch (IOException e) {
            System.err.println("unable to create database: " + e.getMessage());
            System.exit(2);
            return;
        }

        Writer outputWriter = new OutputStreamWriter(System.out);
        Context context = new ContextImpl(tableProvider, outputWriter);
        Writer errorWriter = new OutputStreamWriter(System.err);

        Interpreter interpreter;
        Reader reader;
        if (args.length == 0) {
            interpreter = new InteractiveInterpreter();
            reader = new InputStreamReader(System.in);
        } else {
            interpreter = new BatchInterpreter();
            reader = new StringReader(String.join(" ", args));
        }

        Iterable<Command> commands = Arrays.asList(new CommitCommand(), new CreateCommand(), new DropCommand(),
                new ExitCommand(), new ListCommand(), new PutCommand(), new RemoveCommand(), new RollbackCommand(),
                new ShowCommand(), new SizeCommand(), new UseCommand(), new GetCommand(),
                new StartHttpCommand(), new StopHttpCommand());

        try {
            interpreter.interpret(commands, context, reader, outputWriter, errorWriter);
        } catch (ParseException | CommandExecutionException | IOException e) {
            System.err.println(e.getMessage());
            System.exit(3);
        }
    }
}
