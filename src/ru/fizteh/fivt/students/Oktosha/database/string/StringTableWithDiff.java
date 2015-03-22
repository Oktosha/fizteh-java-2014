package ru.fizteh.fivt.students.Oktosha.database.string;

import java.io.IOException;
import java.util.List;

/**
 * Created by DKolodzey on 04.03.15.
 * better string.Table with drop and IOException in commit
 */
public interface StringTableWithDiff {

    String getName();

    String get(String key);

    String put(String key, String value);

    String remove(String key);

    int size();

    int commit() throws IOException;

    int rollback();

    List<String> list();

    int getNumberOfUncommittedChanges();

    void drop() throws IOException;
}
