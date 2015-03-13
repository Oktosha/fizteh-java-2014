package ru.fizteh.fivt.students.Oktosha.commander;

import java.io.Reader;
import java.io.Writer;

/**
 * Created by DKolodzey on 13.03.15.
 * interprets text from reader using given set of commands and applying it to the context
 * writes messages to given writers
 */
public interface Interpreter {
    void interpret(Iterable<Command> commands, Context context,
                   Reader reader, Writer outputWriter, Writer errorWriter)
            throws CommandExecutionException, ArgumentParsingException;
}
