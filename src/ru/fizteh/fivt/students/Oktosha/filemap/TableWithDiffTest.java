package ru.fizteh.fivt.students.Oktosha.filemap;

import org.junit.Before;
import org.junit.Test;

import java.nio.file.Files;

import static org.junit.Assert.*;

public class TableWithDiffTest {

    TableWithDiff tableWithDiff;

    @Before
    public void setUp() throws Exception {
        MultiFileMap multiFileMap = new MultiFileMapImpl(Files.createTempDirectory("Oktosha.fileMap"));
        tableWithDiff = new TableWithDiffImpl(multiFileMap);
        tableWithDiff.put("key", "value");
        tableWithDiff.put("ключ", "значение");
    }

    @Test
    public void testGetNumberOfUncommittedChanges() throws Exception {

    }

    @Test
    public void testGetName() throws Exception {
        assertTrue("unexpected name" + tableWithDiff.getName(), tableWithDiff.getName().startsWith("Oktosha.fileMap"));
    }

    @Test
    public void testGet() throws Exception {

    }

    @Test
    public void testPut() throws Exception {

    }

    @Test
    public void testRemove() throws Exception {

    }

    @Test
    public void testSize() throws Exception {

    }

    @Test
    public void testCommit() throws Exception {

    }

    @Test
    public void testRollback() throws Exception {

    }

    @Test
    public void testList() throws Exception {

    }

}
