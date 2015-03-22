package ru.fizteh.fivt.students.sautin1.parallel.junit;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.sautin1.parallel.filemap.GeneralTable;
import ru.fizteh.fivt.students.sautin1.parallel.shell.CommandExecuteException;
import ru.fizteh.fivt.students.sautin1.parallel.shell.UserInterruptException;
import ru.fizteh.fivt.students.sautin1.parallel.storeable.AbstractStoreableDatabaseCommand;
import ru.fizteh.fivt.students.sautin1.parallel.storeable.StoreableDatabaseState;

/**
 * Rollback command.
 * Created by sautin1 on 10/12/14.
 */
public class RollbackCommand extends AbstractStoreableDatabaseCommand {

    public RollbackCommand() {
        super("rollback", 0, 0);
    }

    /**
     * Rollback all the changes made to the active table.
     * @param state - database state.
     * @param args - command arguments.
     * @throws UserInterruptException if user desires to exit.
     * @throws CommandExecuteException if any error occurs.
     */
    @Override
    public void execute(StoreableDatabaseState state, String... args)
            throws UserInterruptException, CommandExecuteException {
        if (checkArgumentNumber(args) != CheckArgumentNumber.EQUAL) {
            throw new CommandExecuteException(toString() + ": wrong number of arguments");
        }
        GeneralTable<Storeable> table = state.getActiveTable();
        if (table != null) {
            int diffCount = table.rollback();
            System.out.println(diffCount);
        } else {
            System.err.println("no table");
        }
    }
}
