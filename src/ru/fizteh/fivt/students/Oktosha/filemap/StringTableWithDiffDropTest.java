package ru.fizteh.fivt.students.Oktosha.filemap;

import org.junit.Before;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Created by DKolodzey on 04.03.15.
 * Test that drop switches to invalid state
 */
public class StringTableWithDiffDropTest {

    StringTableWithDiff stringTableWithDiff;
    MultiFileMap multiFileMap;
    Path path;
    @Before
    public void setUp() throws Exception {
        path = Files.createTempDirectory("Oktosha.fileMap");
        multiFileMap = new MultiFileMapImpl(path);
        stringTableWithDiff = new StringTableWithDiffImpl(multiFileMap);
        stringTableWithDiff.put("key", "value");
        stringTableWithDiff.put("ключ", "значение");
        stringTableWithDiff.drop();
    }

    @Test(expected = IllegalStateException.class)
    public void testCommit() throws Exception{
        stringTableWithDiff.commit();
    }

    @Test(expected = IllegalStateException.class)
    public void testDrop() throws Exception {
        stringTableWithDiff.drop();
    }

    @Test(expected = IllegalStateException.class)
    public void testGet() throws Exception {
        stringTableWithDiff.get("key");
    }

    @Test(expected = IllegalStateException.class)
    public void testGetName() throws Exception {
        stringTableWithDiff.getName();
    }

    @Test(expected = IllegalStateException.class)
    public void testGetNumberOfUncommittedChanges() throws Exception {
        stringTableWithDiff.getNumberOfUncommittedChanges();
    }

    @Test(expected = IllegalStateException.class)
    public void testList() throws Exception {
        stringTableWithDiff.list();
    }

    @Test(expected = IllegalStateException.class)
    public void testPut() throws Exception {
        stringTableWithDiff.put("key", "value");
    }

    @Test(expected = IllegalStateException.class)
    public void testRemove() throws Exception {
        stringTableWithDiff.remove("key");
    }

    @Test(expected = IllegalStateException.class)
    public void testRollback() throws Exception {
        stringTableWithDiff.rollback();
    }

    @Test(expected = IllegalStateException.class)
    public void testSize() throws Exception {
        stringTableWithDiff.size();
    }
}
