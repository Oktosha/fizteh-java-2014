package ru.fizteh.fivt.students.Oktosha.database.tableprovider;

import ru.fizteh.fivt.students.Oktosha.database.storeable.LoggingDroppableStructuredTableFactory;
import ru.fizteh.fivt.students.Oktosha.proxy.LoggingProxyFactoryImpl;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Paths;

/**
 * Created by DKolodzey on 22.03.15.
 * factory for logging tableProvider
 * by default logs are written to System.out
 * this behaviour can be changed using setWriter
 */
public class LoggingTableProviderFactory implements ExtendedTableProviderFactory {
    Writer writer = new OutputStreamWriter(System.out);

    public void setWriter(Writer writer) {
        this.writer = writer;
    }

    @Override
    public ExtendedTableProvider create(String path) throws IOException {
        ExtendedTableProvider tableProvider = new TableProviderImpl(Paths.get(path),
                new LoggingDroppableStructuredTableFactory(writer));
        Object wrapped = new LoggingProxyFactoryImpl().wrap(writer, tableProvider, ExtendedTableProvider.class);
        return (ExtendedTableProvider) wrapped;
    }
}
