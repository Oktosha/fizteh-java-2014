package ru.fizteh.fivt.students.Oktosha.commander;

import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;

import java.io.Writer;

/**
 * Created by DKolodzey on 13.03.15.
 * context which is given to commands
 */
public interface Context {
    TableProvider getTableProvider();
    Table getCurrentTable();
    void setCurrentTable(Table table);
    boolean isExitRequested();
    void setExitRequestedToTrue();
    Writer getOutputWriter();
}
