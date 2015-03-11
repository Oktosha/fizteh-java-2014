package ru.fizteh.fivt.students.Oktosha.filemap;

import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.storage.structured.Storeable;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class DroppableStructuredTableTest {

    Path path;
    JSONStoreableSerializerDeserializer codec;
    List<SignatureElement> signature;
    DroppableStructuredTable table;
    List<Storeable> values;

    @Before
    public void setUp() throws Exception {
        path = Files.createTempDirectory("Oktosha.fileMap");
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
        values.get(0).setColumnAt(0, true);
        values.get(0).setColumnAt(1, "value  f ");
        values.get(0).setColumnAt(2, 42);
        values.get(0).setColumnAt(3, 36.6);
        values.get(0).setColumnAt(4, "not null");

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
        boolean areEqual = values.get(0).equals(table.put("key", values.get(1)));
        assertTrue(areEqual);
        assertNull(table.put("other key", values.get(0)));
    }

    @Test
    public void testRemove() throws Exception {

    }

    @Test
    public void testSize() throws Exception {

    }

    @Test
    public void testList() throws Exception {

    }

    @Test
    public void testCommit() throws Exception {

    }

    @Test
    public void testRollback() throws Exception {

    }

    @Test
    public void testGetNumberOfUncommittedChanges() throws Exception {

    }

    @Test
    public void testGetColumnsCount() throws Exception {

    }

    @Test
    public void testGetColumnType() throws Exception {

    }

    @Test
    public void testGetName() throws Exception {

    }

    @Test
    public void testGet() throws Exception {

    }

    @Test
    public void testDrop() throws Exception {

    }
}
