package ru.fizteh.fivt.students.Oktosha.database.storeable;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.structured.Storeable;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class DroppableStructuredTableTest {

    @Rule
    public final TemporaryFolder folder = new TemporaryFolder();

    Path path;
    JSONStoreableSerializerDeserializer codec;
    List<SignatureElement> signature;
    DroppableStructuredTable table;
    List<Storeable> values;

    @Before
    public void setUp() throws Exception {
        File rootFolder = folder.newFolder();
        path = rootFolder.toPath();
        path = path.resolve("tableName");

        codec = new JSONStoreableSerializerDeserializer();

        signature = new ArrayList<>();
        signature.add(SignatureElement.BOOLEAN);
        signature.add(SignatureElement.STRING);
        signature.add(SignatureElement.INTEGER);
        signature.add(SignatureElement.DOUBLE);
        signature.add(SignatureElement.STRING);

        table = new DroppableStructuredTableImpl(path, codec, signature);

        values = new ArrayList<>();

        values.add(new StoreableImpl(signature));
        values.get(0).setColumnAt(0, null);
        values.get(0).setColumnAt(1, "value  f ");
        values.get(0).setColumnAt(2, 42);
        values.get(0).setColumnAt(3, 36.6);
        values.get(0).setColumnAt(4, null);

        values.add(new StoreableImpl(signature));
        values.get(1).setColumnAt(0, null);
        values.get(1).setColumnAt(1, "other");
        values.get(1).setColumnAt(2, 33);
        values.get(1).setColumnAt(3, 21.);
        values.get(1).setColumnAt(4, "other other");

        table.put("key", values.get(0));
    }

    @Test
    public void testPut() throws Exception {
        assertEquals(values.get(0), table.put("key", values.get(1)));
        assertNull(table.put("other key", values.get(0)));
    }

    @Test
    public void testRemove() throws Exception {
        assertEquals(values.get(0), table.remove("key"));
        assertNull(table.remove("key"));
    }

    @Test
    public void testSize() throws Exception {
        assertEquals(1, table.size());
    }

    @Test
    public void testList() throws Exception {
        assertEquals(Arrays.asList("key"), table.list());
    }

    @Test
    public void testCommit() throws Exception {
        assertEquals(1, table.commit());
        assertEquals(0, table.commit());
        table = new DroppableStructuredTableImpl(path, codec);
        assertEquals(Arrays.asList("key"), table.list());
    }

    @Test
    public void testRollback() throws Exception {
        assertEquals(1, table.rollback());
        assertEquals(0, table.rollback());
        assertEquals(0, table.size());
    }

    @Test
    public void testGetNumberOfUncommittedChanges() throws Exception {
        assertEquals(1, table.getNumberOfUncommittedChanges());
    }

    @Test
    public void testGetColumnsCount() throws Exception {
        assertEquals(5, table.getColumnsCount());
    }

    @Test
    public void testGetColumnType() throws Exception {
        assertEquals(Boolean.class, table.getColumnType(0));
        assertEquals(String.class,  table.getColumnType(1));
        assertEquals(Integer.class, table.getColumnType(2));
        assertEquals(Double.class, table.getColumnType(3));
        assertEquals(String.class, table.getColumnType(4));
    }

    @Test
    public void testGetName() throws Exception {
        assertEquals("tableName", table.getName());
    }

    @Test
    public void testGet() throws Exception {
        assertEquals(values.get(0), table.get("key"));
        assertNull(table.get("unknown key"));
    }

    @Test
    public void testDrop() throws Exception {
        table.drop();
        assertFalse(path.toFile().exists());
   }
}
