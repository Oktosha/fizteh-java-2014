package ru.fizteh.fivt.students.Oktosha.filemap;

import org.junit.Before;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.*;

public class MultiFileMapTest {

    Path path;
    MultiFileMap multiFileMap;

    @Before
    public void setUp() throws Exception {
        path = Files.createTempDirectory("Oktosha.fileMap");
        multiFileMap = new MultiFileMapImpl(path);
        multiFileMap.put("key", "value");
        multiFileMap.put("ключ", "значение");
    }

    @Test
    public void testSave() throws Exception {

    }

    @Test
    public void testClear() throws Exception {

    }

    @Test
    public void testPut() throws Exception {

    }

    @Test
    public void testGet() throws Exception {

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

    @Test(expected = IllegalArgumentException.class)
    public void testGetNull() throws Exception {
        multiFileMap.get(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveNull() throws Exception {
        multiFileMap.remove(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutNullNull() throws Exception {
        multiFileMap.put(null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutNullKeyNormalValue() throws Exception {
        multiFileMap.put(null, "value");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutNormalKeyNullValue() throws Exception {
        multiFileMap.put("key", null);
    }

}
