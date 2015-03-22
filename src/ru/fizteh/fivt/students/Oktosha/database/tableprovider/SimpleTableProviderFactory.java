package ru.fizteh.fivt.students.Oktosha.database.tableprovider;

import ru.fizteh.fivt.students.Oktosha.database.storeable.SimpleDroppableStructuredTableFactory;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * Created by DKolodzey on 12.03.15.
 * factory for better TableProviders
 */
public class SimpleTableProviderFactory implements ExtendedTableProviderFactory {
    @Override
    public ExtendedTableProvider create(String path) throws IOException {
        return new TableProviderImpl(Paths.get(path), new SimpleDroppableStructuredTableFactory());
    }
}
