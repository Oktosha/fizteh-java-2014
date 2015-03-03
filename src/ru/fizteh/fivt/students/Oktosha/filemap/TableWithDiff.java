package ru.fizteh.fivt.students.Oktosha.filemap;

import ru.fizteh.fivt.storage.strings.Table;

/**
 * Created by DKolodzey on 04.03.15.
 */
public interface TableWithDiff extends Table {
    int getNumberOfUncommittedChanges();
}
