package ru.fizteh.fivt.students.Oktosha.database.filebackend;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class MultiFileMapTest {

    @Rule
    public final TemporaryFolder folder = new TemporaryFolder();

    Path path;
    MultiFileMap multiFileMap;

    @Before
    public void setUp() throws Exception {
        File rootFolder = folder.newFolder("Oktosha.fileMap");
        path = rootFolder.toPath();
        multiFileMap = new MultiFileMapImpl(path);
        multiFileMap.put("key", "value");
        multiFileMap.put("ключ", "значение");
    }

    @Test
    public void testGetName() throws Exception {
        assertTrue("unexpected name" + multiFileMap.getName(), multiFileMap.getName().startsWith("Oktosha.fileMap"));
    }

    @Test
    public void testSave() throws Exception {
        multiFileMap.save();
        MultiFileMap loadedMultiFileMap = new MultiFileMapImpl(path);
        List<String> listOfKeys = multiFileMap.list();
        List<String> loadedListOfKeys = loadedMultiFileMap.list();
        assertEquals(new HashSet<>(listOfKeys), new HashSet<>(loadedListOfKeys));
        for (String key : listOfKeys) {
            assertEquals(multiFileMap.get(key), loadedMultiFileMap.get(key));
        }
    }

    @Test
    public void testClear() throws Exception {
        multiFileMap.clear();
        assertEquals(0, multiFileMap.size());
        assertEquals(0, multiFileMap.list().size());
        assertNull(multiFileMap.get("key"));
        assertNull(multiFileMap.get("ключ"));
        assertNull(multiFileMap.get("unknown key"));
    }

    @Test
    public void testPut() throws Exception {
        assertEquals("value", multiFileMap.put("key", "value"));
        assertEquals("value", multiFileMap.get("key"));
        assertEquals("value", multiFileMap.put("key", "new value"));
        assertEquals("new value", multiFileMap.get("key"));
        assertNull(multiFileMap.put("new key", "some value"));
        assertEquals("some value", multiFileMap.put("new key", "значение"));
    }

    @Test
    public void testGet() throws Exception {
        assertEquals("value", multiFileMap.get("key"));
        assertEquals("значение", multiFileMap.get("ключ"));
        assertNull(multiFileMap.get("unknown key"));
    }

    @Test
    public void testRemove() throws Exception {
        assertNull(multiFileMap.remove("незивестный ключ"));
        assertEquals("value", multiFileMap.remove("key"));
        assertNull(multiFileMap.remove("key"));
        assertEquals("значение", multiFileMap.remove("ключ"));
        assertNull(multiFileMap.remove("ключ"));
        assertNull(multiFileMap.remove("unknown key"));
    }

    @Test
    public void testSize() throws Exception {
        assertEquals(2, multiFileMap.size());
        multiFileMap.put("new key", "new value");
        assertEquals(3, multiFileMap.size());
        multiFileMap.put("key", "value");
        assertEquals(3, multiFileMap.size());
        multiFileMap.put("key", "new value");
        assertEquals(3, multiFileMap.size());
        multiFileMap.get("key");
        assertEquals(3, multiFileMap.size());
        multiFileMap.remove("key");
        assertEquals(2, multiFileMap.size());
        multiFileMap.clear();
        assertEquals(0, multiFileMap.size());
    }

    @Test
    public void testList() throws Exception {
        Set<String> keysInMap = new HashSet<>(multiFileMap.list());
        Set<String> expectedKeys = new HashSet<>(2);
        expectedKeys.add("key");
        expectedKeys.add("ключ");
        assertEquals(expectedKeys, keysInMap);
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
