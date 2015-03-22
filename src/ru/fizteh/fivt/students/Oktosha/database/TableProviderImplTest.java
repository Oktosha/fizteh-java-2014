package ru.fizteh.fivt.students.Oktosha.database;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class TableProviderImplTest {

    @Rule
    public final TemporaryFolder folder = new TemporaryFolder();

    Path path;
    TableProvider tableProvider;
    List<Class<?>> columnTypes;
    List<SignatureElement> signature;
    Table table1; //like "new folder 1"
    Storeable value1;
    String serializedValue1;

    @Before
    public void setUp() throws Exception {
        File rootFolder = folder.newFolder("Oktosha.tableProvider");
        path = rootFolder.toPath();

        columnTypes = new ArrayList<>();
        columnTypes.add(Boolean.class);
        columnTypes.add(String.class);
        columnTypes.add(Integer.class);
        columnTypes.add(Double.class);
        columnTypes.add(String.class);

        signature = new ArrayList<>();
        signature.add(SignatureElement.BOOLEAN);
        signature.add(SignatureElement.STRING);
        signature.add(SignatureElement.INTEGER);
        signature.add(SignatureElement.DOUBLE);
        signature.add(SignatureElement.STRING);

        value1 = new StoreableImpl(signature);
        value1.setColumnAt(0, true);
        value1.setColumnAt(1, "hello");
        value1.setColumnAt(2, 42);
        value1.setColumnAt(3, 36.6);
        value1.setColumnAt(4, "world");

        serializedValue1 = "[true,\"hello\",42,36.6,\"world\"]";

        tableProvider = new TableProviderImpl(path, new SimpleDroppableStructuredTableFactory());
        table1 = tableProvider.createTable("table1", columnTypes);
    }

    @Test
    public void testDeserialize() throws Exception {
        assertEquals(value1, tableProvider.deserialize(table1, serializedValue1));
    }

    @Test
    public void testSerialize() throws Exception {
        assertEquals(serializedValue1, tableProvider.serialize(table1, value1));
    }
}
