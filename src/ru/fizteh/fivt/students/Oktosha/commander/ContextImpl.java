package ru.fizteh.fivt.students.Oktosha.commander;

import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.Oktosha.database.DiffManager;
import ru.fizteh.fivt.students.Oktosha.database.DiffManagerImpl;
import ru.fizteh.fivt.students.Oktosha.database.tableprovider.ExtendedTableProvider;
import ru.fizteh.fivt.students.Oktosha.servlet.HTTPServer;
import ru.fizteh.fivt.students.Oktosha.servlet.HTTPServerImpl;

import java.io.Writer;

/**
 * Created by DKolodzey on 16.03.15.
 */
public class ContextImpl implements Context {

    private final ExtendedTableProvider tableProvider;
    private Table currentTable;
    private boolean exitRequested;
    private final Writer outputWriter;
    private final HTTPServer server = new HTTPServerImpl();
    private final DiffManager diffManager = new DiffManagerImpl();

    public ContextImpl(ExtendedTableProvider tableProvider, Writer outputWriter) {
        this.tableProvider = tableProvider;
        this.outputWriter = outputWriter;
        this.currentTable = null;
        this.exitRequested = false;
    }

    @Override
    public ExtendedTableProvider getTableProvider() {
        return tableProvider;
    }

    @Override
    public Writer getOutputWriter() {
        return outputWriter;
    }

    @Override
    public HTTPServer getServer() {
        return server;
    }

    @Override
    public DiffManager getDiffManager() {
        return diffManager;
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
