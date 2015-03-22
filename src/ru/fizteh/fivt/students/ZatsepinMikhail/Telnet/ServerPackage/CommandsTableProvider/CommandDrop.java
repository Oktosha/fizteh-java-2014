package ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ServerPackage.CommandsTableProvider;



import ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.TableProviderExtended;

import java.io.IOException;
import java.io.PrintStream;

public class CommandDrop extends CommandTableProviderExtended {
    public CommandDrop() {
        name = "drop";
        numberOfArguments = 2;
    }

    @Override
    public boolean run(TableProviderExtended myMap, String[] args, PrintStream output) {
        try {
            myMap.removeTable(args[1]);
            output.println("dropped");
        } catch (IOException e) {
            output.println("io exception while removing directory");
            return false;
        } catch (IllegalArgumentException e) {
            output.println(e.getMessage());
        } catch (IllegalStateException e) {
            output.println(e.getMessage());
        }
        return true;
    }
}
