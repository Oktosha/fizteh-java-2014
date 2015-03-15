package ru.fizteh.fivt.students.Oktosha.commander;

import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;

import java.io.Writer;

/**
 * Created by DKolodzey on 16.03.15.
 */
public class ContextImpl implements Context {

    private final TableProvider tableProvider;
    private Table currentTable;
    private boolean exitRequested;
    private final Writer outputWriter;

    public ContextImpl(TableProvider tableProvider, Writer outputWriter) {
        this.tableProvider = tableProvider;
        this.outputWriter = outputWriter;
        this.currentTable = null;
        this.exitRequested = false;
    }

    @Override
    public TableProvider getTableProvider() {
        return tableProvider;
    }

    @Override
    public Writer getOutputWriter() {
        return outputWriter;
    }

    @Override
    public boolean isExitRequested() {
        return exitRequested;
    }

    @Override
    public Table getCurrentTable() {
        return currentTable;
    }

    @Override
    public void setExitRequestedToTrue() {
        exitRequested = true;
    }

    @Override
    public void setCurrentTable(Table currentTable) {
        this.currentTable = currentTable;
    }

}
