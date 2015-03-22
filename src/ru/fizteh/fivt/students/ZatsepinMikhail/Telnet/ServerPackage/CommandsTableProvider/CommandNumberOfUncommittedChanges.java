package ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ServerPackage.CommandsTableProvider;


import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.TableProviderExtended;

import java.io.PrintStream;

public class CommandNumberOfUncommittedChanges extends CommandTableProviderExtended {
    public CommandNumberOfUncommittedChanges() {
        name = "uncomm-changes";
        numberOfArguments = 2;
    }

    @Override
    public boolean run(TableProviderExtended dataBase, String[] args, PrintStream output) {
        Table currentTable = dataBase.getTable(args[1]);
        if (currentTable == null) {
            output.println("-1");
            return false;
        }
        output.println(currentTable.getNumberOfUncommittedChanges());
        return true;
    }
}
