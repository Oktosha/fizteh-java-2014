package ru.fizteh.fivt.students.Oktosha.database.tableprovider;

import ru.fizteh.fivt.storage.structured.TableProviderFactory;

import java.io.IOException;

/**
 * Created by DKolodzey on 22.03.15.
 */
public interface ExtendedTableProviderFactory extends TableProviderFactory {
    ExtendedTableProvider create(String path) throws IOException;
}
