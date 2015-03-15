package ru.fizteh.fivt.students.Oktosha.commander;

import java.util.List;

/**
 * Created by DKolodzey on 13.03.15.
 */
public interface Command {
    String getName();

    void exec(Context context, List<String> arguments) throws CommandExecutionException;
}
