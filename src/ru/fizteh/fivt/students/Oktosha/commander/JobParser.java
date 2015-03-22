package ru.fizteh.fivt.students.Oktosha.commander;

import java.text.ParseException;
import java.util.List;

/**
 * Created by DKolodzey on 13.03.15.
 * parses command arguments from reader
 */
interface JobParser {
    List<List<String>> parse(String line) throws ParseException;
}
