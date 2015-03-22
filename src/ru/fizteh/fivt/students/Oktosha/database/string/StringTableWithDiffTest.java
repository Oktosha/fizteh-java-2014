package ru.fizteh.fivt.students.Oktosha.database.string;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.students.Oktosha.database.filebackend.MultiFileMap;
import ru.fizteh.fivt.students.Oktosha.database.filebackend.MultiFileMapImpl;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;

import static org.junit.Assert.*;

public class StringTableWithDiffTest {

    private StringTableWithDiff stringTableWithDiff;
    private MultiFileMap multiFileMap;
    private Path path;

    @Rule
    public final TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void setUp() throws Exception {
        File rootFolder = folder.newFolder("Oktosha.fileMap");
        path = rootFolder.toPath();
        multiFileMap = new MultiFileMapImpl(path);
        stringTableWithDiff = new StringTableWithDiffImpl(multiFileMap);
    }

    @Test
    public void testGetNumberOfUncommittedChanges() throws Exception {
        assertEquals(0, stringTableWithDiff.getNumberOfUncommittedChanges());

        stringTableWithDiff.put("A", "a1");
        assertEquals(1, stringTableWithDiff.getNumberOfUncommittedChanges());
        stringTableWithDiff.put("B", "b1");
        assertEquals(2, stringTableWithDiff.getNumberOfUncommittedChanges());
        stringTableWithDiff.put("Ц", "ц1");
        assertEquals(3, stringTableWithDiff.getNumberOfUncommittedChanges());
        stringTableWithDiff.put("D", "d1");
        assertEquals(4, stringTableWithDiff.getNumberOfUncommittedChanges());

        stringTableWithDiff.commit();
        assertEquals(0, stringTableWithDiff.getNumberOfUncommittedChanges());

        stringTableWithDiff.remove("A");
        assertEquals(1, stringTableWithDiff.getNumberOfUncommittedChanges());
        stringTableWithDiff.put("B", "b1");
        assertEquals(2, stringTableWithDiff.getNumberOfUncommittedChanges());
        stringTableWithDiff.put("D", "d2");
        assertEquals(3, stringTableWithDiff.getNumberOfUncommittedChanges());
        stringTableWithDiff.put("E", "e1");
        assertEquals(4, stringTableWithDiff.getNumberOfUncommittedChanges());

        stringTableWithDiff.remove("A");
        assertEquals(4, stringTableWithDiff.getNumberOfUncommittedChanges());
        stringTableWithDiff.put("B", "b1");
        assertEquals(4, stringTableWithDiff.getNumberOfUncommittedChanges());
        stringTableWithDiff.put("D", "d2");
        assertEquals(4, stringTableWithDiff.getNumberOfUncommittedChanges());
        stringTableWithDiff.put("E", "e1");
        assertEquals(4, stringTableWithDiff.getNumberOfUncommittedChanges());

        stringTableWithDiff.put("A", "a2");
        assertEquals(4, stringTableWithDiff.getNumberOfUncommittedChanges());
        stringTableWithDiff.remove("B");
        assertEquals(4, stringTableWithDiff.getNumberOfUncommittedChanges());
        stringTableWithDiff.remove("D");
        assertEquals(4, stringTableWithDiff.getNumberOfUncommittedChanges());
        stringTableWithDiff.remove("E");
        assertEquals(4, stringTableWithDiff.getNumberOfUncommittedChanges());

        stringTableWithDiff.commit();
        assertEquals(0, stringTableWithDiff.getNumberOfUncommittedChanges());

        stringTableWithDiff.remove("A");
        assertEquals(1, stringTableWithDiff.getNumberOfUncommittedChanges());
        stringTableWithDiff.put("B", "b1");
        assertEquals(2, stringTableWithDiff.getNumberOfUncommittedChanges());
        stringTableWithDiff.put("Ц", "ц2");
        assertEquals(3, stringTableWithDiff.getNumberOfUncommittedChanges());
        stringTableWithDiff.remove("D");
        assertEquals(4, stringTableWithDiff.getNumberOfUncommittedChanges());

        stringTableWithDiff.rollback();
        assertEquals(0, stringTableWithDiff.getNumberOfUncommittedChanges());

        stringTableWithDiff = new StringTableWithDiffImpl(multiFileMap);
        assertEquals(0, stringTableWithDiff.getNumberOfUncommittedChanges());

        stringTableWithDiff.clear();

        stringTableWithDiff = new StringTableWithDiffImpl(multiFileMap);
        assertEquals(0, stringTableWithDiff.getNumberOfUncommittedChanges());
    }

    @Test
    public void testGetName() throws Exception {
        assertTrue(
                "unexpected name" + stringTableWithDiff.getName(),
                stringTableWithDiff.getName().startsWith("Oktosha.fileMap"));
    }

    @Test
    public void testGet() throws Exception {
        assertNull(stringTableWithDiff.get("A"));

        stringTableWithDiff.put("A", "a1");
        assertEquals("a1", stringTableWithDiff.get("A"));
        stringTableWithDiff.put("B", "b1");
        assertEquals("b1", stringTableWithDiff.get("B"));
        stringTableWithDiff.put("Ц", "ц1");
        assertEquals("ц1", stringTableWithDiff.get("Ц"));
        stringTableWithDiff.put("D", "d1");
        assertEquals("d1", stringTableWithDiff.get("D"));

        stringTableWithDiff.commit();
        assertEquals("a1", stringTableWithDiff.get("A"));
        assertEquals("b1", stringTableWithDiff.get("B"));
        assertEquals("ц1", stringTableWithDiff.get("Ц"));
        assertEquals("d1", stringTableWithDiff.get("D"));
        assertNull(stringTableWithDiff.get("E"));

        stringTableWithDiff.remove("A");
        assertNull(stringTableWithDiff.get("A"));
        stringTableWithDiff.put("B", "b1");
        assertEquals("b1", stringTableWithDiff.get("B"));
        stringTableWithDiff.put("D", "d2");
        assertEquals("d2", stringTableWithDiff.get("D"));
        stringTableWithDiff.put("E", "e1");
        assertEquals("e1", stringTableWithDiff.get("E"));

        stringTableWithDiff.remove("A");
        assertNull(stringTableWithDiff.get("A"));
        stringTableWithDiff.put("B", "b1");
        assertEquals("b1", stringTableWithDiff.get("B"));
        stringTableWithDiff.put("D", "d2");
        assertEquals("d2", stringTableWithDiff.get("D"));
        stringTableWithDiff.put("E", "e1");
        assertEquals("e1", stringTableWithDiff.get("E"));

        stringTableWithDiff.put("A", "a2");
        assertEquals("a2", stringTableWithDiff.get("A"));
        stringTableWithDiff.remove("B");
        assertNull(stringTableWithDiff.get("B"));
        stringTableWithDiff.remove("D");
        assertNull(stringTableWithDiff.get("D"));
        stringTableWithDiff.remove("E");
        assertNull(stringTableWithDiff.get("E"));

        stringTableWithDiff.commit();
        assertEquals("a2", stringTableWithDiff.get("A"));
        assertNull(stringTableWithDiff.get("B"));
        assertEquals("ц1", stringTableWithDiff.get("Ц"));
        assertNull(stringTableWithDiff.get("D"));
        assertNull(stringTableWithDiff.get("E"));


        stringTableWithDiff.remove("A");
        stringTableWithDiff.put("B", "b1");
        stringTableWithDiff.put("Ц", "ц2");
        stringTableWithDiff.remove("D");

        stringTableWithDiff.rollback();
        assertEquals("a2", stringTableWithDiff.get("A"));
        assertNull(stringTableWithDiff.get("B"));
        assertEquals("ц1", stringTableWithDiff.get("Ц"));
        assertNull(stringTableWithDiff.get("D"));
        assertNull(stringTableWithDiff.get("E"));

        stringTableWithDiff.put("F", "f1");

        stringTableWithDiff = new StringTableWithDiffImpl(new MultiFileMapImpl(path));
        assertEquals("a2", stringTableWithDiff.get("A"));
        assertNull(stringTableWithDiff.get("B"));
        assertEquals("ц1", stringTableWithDiff.get("Ц"));
        assertNull(stringTableWithDiff.get("D"));
        assertNull(stringTableWithDiff.get("E"));
        assertNull(stringTableWithDiff.get("F"));

        stringTableWithDiff.clear();

        stringTableWithDiff = new StringTableWithDiffImpl(new MultiFileMapImpl(path));
        assertNull(stringTableWithDiff.get("A"));
        assertNull(stringTableWithDiff.get("B"));
        assertNull(stringTableWithDiff.get("Ц"));
        assertNull(stringTableWithDiff.get("D"));
        assertNull(stringTableWithDiff.get("E"));
    }

    @Test
    public void testPut() throws Exception {
        assertNull(stringTableWithDiff.put("A", "a1"));
        assertNull(stringTableWithDiff.put("B", "b1"));
        assertNull(stringTableWithDiff.put("Ц", "ц1"));
        assertNull(stringTableWithDiff.put("D", "d1"));

        stringTableWithDiff.commit();

        stringTableWithDiff.remove("A");
        assertEquals("b1", stringTableWithDiff.put("B", "b1"));
        assertEquals("d1", stringTableWithDiff.put("D", "d2"));
        assertNull(stringTableWithDiff.put("E", "e1"));

        stringTableWithDiff.remove("A");
        assertEquals("b1", stringTableWithDiff.put("B", "b1"));
        assertEquals("d2", stringTableWithDiff.put("D", "d2"));
        assertEquals("e1", stringTableWithDiff.put("E", "e1"));

        assertNull(stringTableWithDiff.put("A", "a2"));
        stringTableWithDiff.remove("B");
        stringTableWithDiff.remove("D");
        stringTableWithDiff.remove("E");

        stringTableWithDiff.commit();

        stringTableWithDiff.remove("A");
        assertNull(stringTableWithDiff.put("B", "b1"));
        assertEquals("ц1", stringTableWithDiff.put("Ц", "ц2"));
        stringTableWithDiff.remove("D");
    }

    @Test
    public void testRemove() throws Exception {
        stringTableWithDiff.put("A", "a1");
        stringTableWithDiff.put("B", "b1");
        stringTableWithDiff.put("Ц", "ц1");
        stringTableWithDiff.put("D", "d1");

        stringTableWithDiff.commit();

        assertEquals("a1", stringTableWithDiff.remove("A"));
        stringTableWithDiff.put("B", "b1");
        stringTableWithDiff.put("D", "d2");
        stringTableWithDiff.put("E", "e1");

        assertNull(stringTableWithDiff.remove("A"));
        stringTableWithDiff.put("B", "b1");
        stringTableWithDiff.put("D", "d2");
        stringTableWithDiff.put("E", "e1");

        stringTableWithDiff.put("A", "a2");
        assertEquals("b1", stringTableWithDiff.remove("B"));
        assertEquals("d2", stringTableWithDiff.remove("D"));
        assertEquals("e1", stringTableWithDiff.remove("E"));

        stringTableWithDiff.commit();

        assertEquals("a2", stringTableWithDiff.remove("A"));
        stringTableWithDiff.put("B", "b1");
        stringTableWithDiff.put("Ц", "ц2");
        assertNull(stringTableWithDiff.remove("D"));
        assertNull(stringTableWithDiff.remove("F"));
    }

    @Test
    public void testSize() throws Exception {
        assertEquals(0, stringTableWithDiff.size());

        stringTableWithDiff.put("A", "a1");
        assertEquals(1, stringTableWithDiff.size());
        stringTableWithDiff.put("B", "b1");
        assertEquals(2, stringTableWithDiff.size());
        stringTableWithDiff.put("Ц", "ц1");
        assertEquals(3, stringTableWithDiff.size());
        stringTableWithDiff.put("D", "d1");
        assertEquals(4, stringTableWithDiff.size());

        stringTableWithDiff.commit();
        assertEquals(4, stringTableWithDiff.size());

        stringTableWithDiff.remove("A");
        assertEquals(3, stringTableWithDiff.size());
        stringTableWithDiff.put("B", "b1");
        assertEquals(3, stringTableWithDiff.size());
        stringTableWithDiff.put("D", "d2");
        assertEquals(3, stringTableWithDiff.size());
        stringTableWithDiff.put("E", "e1");
        assertEquals(4, stringTableWithDiff.size());

        stringTableWithDiff.remove("A");
        assertEquals(4, stringTableWithDiff.size());
        stringTableWithDiff.put("B", "b1");
        assertEquals(4, stringTableWithDiff.size());
        stringTableWithDiff.put("D", "d2");
        assertEquals(4, stringTableWithDiff.size());
        stringTableWithDiff.put("E", "e1");
        assertEquals(4, stringTableWithDiff.size());

        stringTableWithDiff.put("A", "a2");
        assertEquals(5, stringTableWithDiff.size());
        stringTableWithDiff.remove("B");
        assertEquals(4, stringTableWithDiff.size());
        stringTableWithDiff.remove("D");
        assertEquals(3, stringTableWithDiff.size());
        stringTableWithDiff.remove("E");
        assertEquals(2, stringTableWithDiff.size());

        stringTableWithDiff.commit();
        assertEquals(2, stringTableWithDiff.size());

        stringTableWithDiff.remove("A");
        assertEquals(1, stringTableWithDiff.size());
        stringTableWithDiff.put("B", "b1");
        assertEquals(2, stringTableWithDiff.size());
        stringTableWithDiff.put("Ц", "ц2");
        assertEquals(2, stringTableWithDiff.size());
        stringTableWithDiff.remove("D");
        assertEquals(2, stringTableWithDiff.size());
        stringTableWithDiff.put("F", "f1");
        assertEquals(3, stringTableWithDiff.size());

        stringTableWithDiff.rollback();
        assertEquals(2, stringTableWithDiff.size());

        stringTableWithDiff = new StringTableWithDiffImpl(multiFileMap);
        assertEquals(2, stringTableWithDiff.size());

        stringTableWithDiff.clear();

        stringTableWithDiff = new StringTableWithDiffImpl(multiFileMap);
        assertEquals(0, stringTableWithDiff.size());
    }

    @Test
    public void testCommit() throws Exception {
        stringTableWithDiff.put("A", "a1");
        stringTableWithDiff.put("B", "b1");
        stringTableWithDiff.put("Ц", "ц1");
        stringTableWithDiff.put("D", "d1");

        assertEquals(4, stringTableWithDiff.commit());

        stringTableWithDiff.remove("A");
        stringTableWithDiff.put("B", "b1");
        stringTableWithDiff.put("D", "d2");
        stringTableWithDiff.put("E", "e1");

        stringTableWithDiff.remove("A");
        stringTableWithDiff.put("B", "b1");
        stringTableWithDiff.put("D", "d2");
        stringTableWithDiff.put("E", "e1");

        stringTableWithDiff.put("A", "a2");
        stringTableWithDiff.remove("B");
        stringTableWithDiff.remove("D");
        stringTableWithDiff.remove("E");

        assertEquals(4, stringTableWithDiff.commit());
    }

    @Test
    public void testRollback() throws Exception {
        stringTableWithDiff.put("A", "a1");
        stringTableWithDiff.put("B", "b1");
        stringTableWithDiff.put("Ц", "ц1");
        stringTableWithDiff.put("D", "d1");

        assertEquals(4, stringTableWithDiff.rollback());
    }

    @Test
    public void testList() throws Exception {
        assertTrue(stringTableWithDiff.list().isEmpty());

        stringTableWithDiff.put("A", "a1");
        assertEquals(new HashSet<>(Arrays.asList("A")),
                new HashSet<>(stringTableWithDiff.list()));
        stringTableWithDiff.put("B", "b1");
        assertEquals(new HashSet<>(Arrays.asList("A", "B")),
                new HashSet<>(stringTableWithDiff.list()));
        stringTableWithDiff.put("Ц", "ц1");
        assertEquals(new HashSet<>(Arrays.asList("A", "B", "Ц")),
                new HashSet<>(stringTableWithDiff.list()));
        stringTableWithDiff.put("D", "d1");
        assertEquals(new HashSet<>(Arrays.asList("A", "B", "Ц", "D")),
                new HashSet<>(stringTableWithDiff.list()));

        stringTableWithDiff.commit();
        assertEquals(new HashSet<>(Arrays.asList("A", "B", "Ц", "D")),
                new HashSet<>(stringTableWithDiff.list()));

        stringTableWithDiff.remove("A");
        assertEquals(new HashSet<>(Arrays.asList("B", "Ц", "D")),
                new HashSet<>(stringTableWithDiff.list()));
        stringTableWithDiff.put("B", "b1");
        assertEquals(new HashSet<>(Arrays.asList("B", "Ц", "D")),
                new HashSet<>(stringTableWithDiff.list()));
        stringTableWithDiff.put("D", "d2");
        assertEquals(new HashSet<>(Arrays.asList("B", "Ц", "D")),
                new HashSet<>(stringTableWithDiff.list()));
        stringTableWithDiff.put("E", "e1");
        assertEquals(new HashSet<>(Arrays.asList("B", "Ц", "D", "E")),
                new HashSet<>(stringTableWithDiff.list()));

        stringTableWithDiff.remove("A");
        assertEquals(new HashSet<>(Arrays.asList("B", "Ц", "D", "E")),
                new HashSet<>(stringTableWithDiff.list()));
        stringTableWithDiff.put("B", "b1");
        assertEquals(new HashSet<>(Arrays.asList("B", "Ц", "D", "E")),
                new HashSet<>(stringTableWithDiff.list()));
        stringTableWithDiff.put("D", "d2");
        assertEquals(new HashSet<>(Arrays.asList("B", "Ц", "D", "E")),
                new HashSet<>(stringTableWithDiff.list()));
        stringTableWithDiff.put("E", "e1");
        assertEquals(new HashSet<>(Arrays.asList("B", "Ц", "D", "E")),
                new HashSet<>(stringTableWithDiff.list()));

        stringTableWithDiff.put("A", "a2");
        assertEquals(new HashSet<>(Arrays.asList("A", "B", "Ц", "D", "E")),
                new HashSet<>(stringTableWithDiff.list()));
        stringTableWithDiff.remove("B");
        assertEquals(new HashSet<>(Arrays.asList("A", "Ц", "D", "E")),
                new HashSet<>(stringTableWithDiff.list()));
        stringTableWithDiff.remove("D");
        assertEquals(new HashSet<>(Arrays.asList("A", "Ц", "E")),
                new HashSet<>(stringTableWithDiff.list()));
        stringTableWithDiff.remove("E");
        assertEquals(new HashSet<>(Arrays.asList("A", "Ц")),
                new HashSet<>(stringTableWithDiff.list()));

        stringTableWithDiff.commit();
        assertEquals(new HashSet<>(Arrays.asList("A", "Ц")),
                new HashSet<>(stringTableWithDiff.list()));


        stringTableWithDiff.remove("A");
        stringTableWithDiff.put("B", "b1");
        stringTableWithDiff.put("Ц", "ц2");
        stringTableWithDiff.remove("D");
        stringTableWithDiff.put("F", "f1");

        stringTableWithDiff.rollback();
        assertEquals(new HashSet<>(Arrays.asList("A", "Ц")),
                new HashSet<>(stringTableWithDiff.list()));

        stringTableWithDiff.put("F", "f1");

        stringTableWithDiff = new StringTableWithDiffImpl(new MultiFileMapImpl(path));
        assertEquals(new HashSet<>(Arrays.asList("A", "Ц")),
                new HashSet<>(stringTableWithDiff.list()));

        stringTableWithDiff.clear();

        stringTableWithDiff = new StringTableWithDiffImpl(new MultiFileMapImpl(path));
        assertTrue(stringTableWithDiff.list().isEmpty());
    }
}
