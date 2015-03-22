package ru.fizteh.fivt.students.Oktosha.servlet;

import ru.fizteh.fivt.students.Oktosha.database.DiffManager;
import ru.fizteh.fivt.students.Oktosha.database.tableprovider.ExtendedTableProvider;

/**
 * Created by DKolodzey on 22.03.15.
 */
public class ServletContextImpl implements ServletContext {
    private final DiffManager diffManager;
    private final ExtendedTableProvider tableProvider;

    public ServletContextImpl(DiffManager diffManager, ExtendedTableProvider tableProvider) {
        this.diffManager = diffManager;
        this.tableProvider = tableProvider;
    }

    @Override
    public ExtendedTableProvider getTableProvider() {
        return tableProvider;
    }

    @Override
    public DiffManager getDiffManager() {
        return diffManager;
    }
}
