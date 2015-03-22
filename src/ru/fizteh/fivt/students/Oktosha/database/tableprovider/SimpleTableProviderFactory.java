package ru.fizteh.fivt.students.Oktosha.database.tableprovider;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.Oktosha.database.storeable.SimpleDroppableStructuredTableFactory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by DKolodzey on 12.03.15.
 * factory for better TableProviders
 */
public class SimpleTableProviderFactory implements ExtendedTableProviderFactory {
    private boolean isDropped = false;
    List<ExtendedTableProvider> tableProviders = new ArrayList<>();
    @Override
    public ExtendedTableProvider create(String path) throws IOException {
        if (isDropped) {
            throw new IllegalStateException("tableProviderFactory accessed being dropped");
        }
        ExtendedTableProvider tableProvider = new TableProviderImpl(Paths.get(path),
                new SimpleDroppableStructuredTableFactory());
        tableProviders.add(tableProvider);
        return tableProvider;
    }

    @Override
    public void close() throws Exception {
        if (isDropped) {
            throw new IllegalStateException("tableProviderFactory accessed being dropped");
        }
        isDropped = true;
        for (ExtendedTableProvider tableProvider : tableProviders) {
            tableProvider.close();
        }

    }
}
