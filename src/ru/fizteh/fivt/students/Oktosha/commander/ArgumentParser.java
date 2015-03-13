package ru.fizteh.fivt.students.Oktosha.commander;

import java.io.Reader;
import java.util.List;

/**
 * Created by DKolodzey on 13.03.15.
 * parses command arguments from reader
 */
public interface ArgumentParser {
    List<String> parse(Reader reader) throws ArgumentParsingException;
}
