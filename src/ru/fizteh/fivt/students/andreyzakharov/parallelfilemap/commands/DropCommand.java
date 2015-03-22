package ru.fizteh.fivt.students.andreyzakharov.parallelfilemap.commands;

import ru.fizteh.fivt.students.andreyzakharov.parallelfilemap.CommandInterruptException;
import ru.fizteh.fivt.students.andreyzakharov.parallelfilemap.MultiFileTableProvider;

public class DropCommand implements Command {
    @Override
    public String execute(MultiFileTableProvider connector, String... args) throws CommandInterruptException {
        if (args.length < 2) {
            throw new CommandInterruptException("drop: too few arguments");
        }
        if (args.length > 2) {
            throw new CommandInterruptException("drop: too many arguments");
        }

        try {
            connector.removeTable(args[1]);
        } catch (IllegalStateException e) {
            return args[1] + " not exists";
        }
        return "deleted";
    }

    @Override
    public String toString() {
        return "drop";
    }
}
