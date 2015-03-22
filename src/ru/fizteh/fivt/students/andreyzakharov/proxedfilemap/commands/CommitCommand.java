package ru.fizteh.fivt.students.andreyzakharov.proxedfilemap.commands;

import ru.fizteh.fivt.students.andreyzakharov.proxedfilemap.CommandInterruptException;
import ru.fizteh.fivt.students.andreyzakharov.proxedfilemap.MultiFileTableProvider;

public class CommitCommand implements Command {
    @Override
    public String execute(MultiFileTableProvider connector, String... args) throws CommandInterruptException {
        if (args.length > 1) {
            throw new CommandInterruptException("commit: too many arguments");
        }
        if (connector.getCurrent() == null) {
            throw new CommandInterruptException("no table");
        }
        return Integer.toString(connector.getCurrent().commit());
    }

    @Override
    public String toString() {
        return "commit";
    }
}
