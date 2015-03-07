package ru.fizteh.fivt.students.Oktosha.filemap;

import ru.fizteh.fivt.storage.strings.Table;

import java.io.IOException;

/**
 * Created by DKolodzey on 04.03.15.
 */
public interface StringTableWithDiff extends Table {
    int getNumberOfUncommittedChanges();

    void drop() throws IOException;
}
