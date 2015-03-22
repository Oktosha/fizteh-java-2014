package ru.fizteh.fivt.students.Oktosha.servlet;

import ru.fizteh.fivt.students.Oktosha.database.DiffManager;
import ru.fizteh.fivt.students.Oktosha.database.tableprovider.ExtendedTableProvider;

/**
 * Created by DKolodzey on 22.03.15.
 */
public interface ServletContext {
    ExtendedTableProvider getTableProvider();
    DiffManager getDiffManager();
}
