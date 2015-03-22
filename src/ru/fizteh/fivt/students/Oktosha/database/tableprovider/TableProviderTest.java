package ru.fizteh.fivt.students.Oktosha.database.tableprovider;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.Oktosha.database.storeable.SignatureElement;
import ru.fizteh.fivt.students.Oktosha.database.storeable.SimpleDroppableStructuredTableFactory;
import ru.fizteh.fivt.students.Oktosha.database.storeable.StoreableImpl;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class TableProviderTest {

    @Rule
    public final TemporaryFolder folder = new TemporaryFolder();

    Path path;
    TableProvider tableProvider;
    List<Class<?>> columnTypes;
    List<SignatureElement> signature;
    Table table1; //like "new folder 1"
    Storeable emptyValue;
    Storeable value1;

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

        emptyValue = new StoreableImpl(signature);

        value1 = new StoreableImpl(signature);
        value1.setColumnAt(0, true);
        value1.setColumnAt(1, "hello");
        value1.setColumnAt(2, 42);
        value1.setColumnAt(3, 36.6);
        value1.setColumnAt(4, "world");

        tableProvider = new TableProviderImpl(path, new SimpleDroppableStructuredTableFactory());
        table1 = tableProvider.createTable("table1", columnTypes);
    }

    @Test
    public void testGetTable() throws Exception {
        assertEquals(table1, tableProvider.getTable("table1"));
        assertNull(tableProvider.getTable("table2"));
    }

    @Test
    public void testCreateTable() throws Exception {
        //null returned for already created table
        assertNull(tableProvider.createTable("table1", columnTypes));
    }

    @Test
    public void testRemoveTable() throws Exception {
        tableProvider.removeTable("table1");
        assertNull(tableProvider.getTable("table1"));
    }

    @Test (expected = IllegalStateException.class)
    public void testRemoveTable1() throws Exception {
        tableProvider.removeTable("table1");
        table1.getColumnsCount();
    }

    @Test (expected = IllegalStateException.class)
    public  void testRemoveTable2() throws Exception {
        tableProvider.removeTable("not existing table");
    }

    @Test
    public void testLoad() throws Exception {
        tableProvider = new TableProviderImpl(path, new SimpleDroppableStructuredTableFactory());
        assertEquals(Arrays.asList("table1"), tableProvider.getTableNames());
    }

    @Test
    public void testCreateFor() throws Exception {
        assertEquals(emptyValue, tableProvider.createFor(table1));
    }

    @Test
    public void testCreateFor1() throws Exception {
        assertEquals(value1, tableProvider.createFor(table1, Arrays.asList(true, "hello", 42, 36.6, "world")));
    }

    @Test
    public void testGetTableNames() throws Exception {
        assertEquals(Arrays.asList("table1"), tableProvider.getTableNames());
    }
}
