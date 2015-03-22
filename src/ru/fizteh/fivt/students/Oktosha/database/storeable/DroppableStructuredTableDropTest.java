package ru.fizteh.fivt.students.Oktosha.database.storeable;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by DKolodzey on 04.03.15.
 * Test that clear switches to invalid state
 */
public class DroppableStructuredTableDropTest {

    @Rule
    public final TemporaryFolder folder = new TemporaryFolder();

    Path path;
    JSONStoreableSerializerDeserializer codec;
    List<SignatureElement> signature;
    DroppableStructuredTable table;


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
        table.put("key", new StoreableImpl(signature));
        table.drop();
    }

    @Test(expected = IllegalStateException.class)
    public void testCommit() throws Exception {
        table.commit();
    }

    @Test(expected = IllegalStateException.class)
    public void testDrop() throws Exception {
        table.drop();
    }

    @Test(expected = IllegalStateException.class)
    public void testGet() throws Exception {
        table.get("key");
    }

    @Test(expected = IllegalStateException.class)
    public void testGetName() throws Exception {
        table.getName();
    }

    @Test(expected = IllegalStateException.class)
    public void testGetNumberOfUncommittedChanges() throws Exception {
        table.getNumberOfUncommittedChanges();
    }

    @Test(expected = IllegalStateException.class)
    public void testList() throws Exception {
        table.list();
    }

    @Test(expected = IllegalStateException.class)
    public void testPut() throws Exception {
        table.put("key", new StoreableImpl(signature));
    }

    @Test(expected = IllegalStateException.class)
    public void testRemove() throws Exception {
        table.remove("key");
    }

    @Test(expected = IllegalStateException.class)
    public void testRollback() throws Exception {
        table.rollback();
    }

    @Test(expected = IllegalStateException.class)
    public void testSize() throws Exception {
        table.size();
    }
}
