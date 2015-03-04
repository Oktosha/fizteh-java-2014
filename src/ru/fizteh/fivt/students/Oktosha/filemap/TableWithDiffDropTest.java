package ru.fizteh.fivt.students.Oktosha.filemap;

import org.junit.Before;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.*;

/**
 * Created by DKolodzey on 04.03.15.
 * Test that drop switches to invalid state
 */
public class TableWithDiffDropTest {

    TableWithDiff tableWithDiff;
    MultiFileMap multiFileMap;
    Path path;
    @Before
    public void setUp() throws Exception {
        path = Files.createTempDirectory("Oktosha.fileMap");
        multiFileMap = new MultiFileMapImpl(path);
        tableWithDiff = new TableWithDiffImpl(multiFileMap);
        tableWithDiff.put("key", "value");
        tableWithDiff.put("ключ", "значение");
        tableWithDiff.drop();
    }

    @Test(expected = IllegalStateException.class)
    public void testCommit() throws Exception{
        tableWithDiff.commit();
    }

    @Test(expected = IllegalStateException.class)
    public void testDrop() throws Exception {
        tableWithDiff.drop();
    }

    @Test(expected = IllegalStateException.class)
    public void testGet() throws Exception {
        tableWithDiff.get("key");
    }

    @Test(expected = IllegalStateException.class)
    public void testGetName() throws Exception {
        tableWithDiff.getName();
    }

    @Test(expected = IllegalStateException.class)
    public void testGetNumberOfUncommittedChanges() throws Exception {
        tableWithDiff.getNumberOfUncommittedChanges();
    }

    @Test(expected = IllegalStateException.class)
    public void testList() throws Exception {
        tableWithDiff.list();
    }

    @Test(expected = IllegalStateException.class)
    public void testPut() throws Exception {
        tableWithDiff.put("key", "value");
    }

    @Test(expected = IllegalStateException.class)
    public void testRemove() throws Exception {
        tableWithDiff.remove("key");
    }

    @Test(expected = IllegalStateException.class)
    public void testRollback() throws Exception {
        tableWithDiff.rollback();
    }

    @Test(expected = IllegalStateException.class)
    public void testSize() throws Exception {
        tableWithDiff.size();
    }
}
