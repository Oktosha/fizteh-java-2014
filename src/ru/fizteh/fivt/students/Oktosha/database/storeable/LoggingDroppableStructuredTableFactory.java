package ru.fizteh.fivt.students.Oktosha.database.storeable;

import ru.fizteh.fivt.proxy.LoggingProxyFactory;
import ru.fizteh.fivt.students.Oktosha.proxy.LoggingProxyFactoryImpl;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Path;
import java.util.List;

/**
 * Created by DKolodzey on 22.03.15.
 */
public class LoggingDroppableStructuredTableFactory implements DroppableStructuredTableFactory {

    DroppableStructuredTableFactory tableFactory = new SimpleDroppableStructuredTableFactory();
    LoggingProxyFactory loggingFactory = new LoggingProxyFactoryImpl();
    Writer writer = new OutputStreamWriter(System.out);

    public LoggingDroppableStructuredTableFactory(Writer writer) {
        this.writer = writer;
    }

    @Override
    public DroppableStructuredTable createTableFromData(Path path,
                                                        StoreableSerializerDeserializer codec) throws IOException {

        DroppableStructuredTable table = tableFactory.createTableFromData(path, codec);
        Object wrappedTable = loggingFactory.wrap(writer, table, DroppableStructuredTable.class);
        return (DroppableStructuredTable) wrappedTable;
    }

    @Override
    public DroppableStructuredTable createEmptyTable(Path path,
                                                     StoreableSerializerDeserializer codec,
                                                     List<SignatureElement> signature) throws IOException {
        DroppableStructuredTable table = tableFactory.createEmptyTable(path, codec, signature);
        Object wrappedTable = loggingFactory.wrap(writer, table, DroppableStructuredTable.class);
        return (DroppableStructuredTable) wrappedTable;
    }
}
