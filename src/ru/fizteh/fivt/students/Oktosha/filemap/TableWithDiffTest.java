package ru.fizteh.fivt.students.Oktosha.filemap;

import org.junit.Before;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;

import static org.junit.Assert.*;

public class TableWithDiffTest {

    TableWithDiff tableWithDiff;
    MultiFileMap multiFileMap;
    Path path;
    @Before
    public void setUp() throws Exception {
        path = Files.createTempDirectory("Oktosha.fileMap");
        multiFileMap = new MultiFileMapImpl(path);
        tableWithDiff = new TableWithDiffImpl(multiFileMap);
    }

    @Test
    public void testGetNumberOfUncommittedChanges() throws Exception {
        assertEquals(0, tableWithDiff.getNumberOfUncommittedChanges());

        tableWithDiff.put("A", "a1");
        assertEquals(1, tableWithDiff.getNumberOfUncommittedChanges());
        tableWithDiff.put("B", "b1");
        assertEquals(2, tableWithDiff.getNumberOfUncommittedChanges());
        tableWithDiff.put("Ц", "ц1");
        assertEquals(3, tableWithDiff.getNumberOfUncommittedChanges());
        tableWithDiff.put("D", "d1");
        assertEquals(4, tableWithDiff.getNumberOfUncommittedChanges());

        tableWithDiff.commit();
        assertEquals(0, tableWithDiff.getNumberOfUncommittedChanges());

        tableWithDiff.remove("A");
        assertEquals(1, tableWithDiff.getNumberOfUncommittedChanges());
        tableWithDiff.put("B", "b1");
        assertEquals(2, tableWithDiff.getNumberOfUncommittedChanges());
        tableWithDiff.put("D", "d2");
        assertEquals(3, tableWithDiff.getNumberOfUncommittedChanges());
        tableWithDiff.put("E", "e1");
        assertEquals(4, tableWithDiff.getNumberOfUncommittedChanges());

        tableWithDiff.remove("A");
        assertEquals(4, tableWithDiff.getNumberOfUncommittedChanges());
        tableWithDiff.put("B", "b1");
        assertEquals(4, tableWithDiff.getNumberOfUncommittedChanges());
        tableWithDiff.put("D", "d2");
        assertEquals(4, tableWithDiff.getNumberOfUncommittedChanges());
        tableWithDiff.put("E", "e1");
        assertEquals(4, tableWithDiff.getNumberOfUncommittedChanges());

        tableWithDiff.put("A", "a2");
        assertEquals(4, tableWithDiff.getNumberOfUncommittedChanges());
        tableWithDiff.remove("B");
        assertEquals(4, tableWithDiff.getNumberOfUncommittedChanges());
        tableWithDiff.remove("D");
        assertEquals(4, tableWithDiff.getNumberOfUncommittedChanges());
        tableWithDiff.remove("E");
        assertEquals(4, tableWithDiff.getNumberOfUncommittedChanges());

        tableWithDiff.commit();
        assertEquals(0, tableWithDiff.getNumberOfUncommittedChanges());

        tableWithDiff.remove("A");
        assertEquals(1, tableWithDiff.getNumberOfUncommittedChanges());
        tableWithDiff.put("B", "b1");
        assertEquals(2, tableWithDiff.getNumberOfUncommittedChanges());
        tableWithDiff.put("Ц", "ц2");
        assertEquals(3, tableWithDiff.getNumberOfUncommittedChanges());
        tableWithDiff.remove("D");
        assertEquals(4, tableWithDiff.getNumberOfUncommittedChanges());

        tableWithDiff.rollback();
        assertEquals(0, tableWithDiff.getNumberOfUncommittedChanges());

        tableWithDiff = new TableWithDiffImpl(multiFileMap);
        assertEquals(0, tableWithDiff.getNumberOfUncommittedChanges());

        tableWithDiff.drop();

        tableWithDiff = new TableWithDiffImpl(multiFileMap);
        assertEquals(0, tableWithDiff.getNumberOfUncommittedChanges());
    }

    @Test
    public void testGetName() throws Exception {
        assertTrue("unexpected name" + tableWithDiff.getName(), tableWithDiff.getName().startsWith("Oktosha.fileMap"));
    }

    @Test
    public void testGet() throws Exception {
        assertNull(tableWithDiff.get("A"));

        tableWithDiff.put("A", "a1");
        assertEquals("a1", tableWithDiff.get("A"));
        tableWithDiff.put("B", "b1");
        assertEquals("b1", tableWithDiff.get("B"));
        tableWithDiff.put("Ц", "ц1");
        assertEquals("ц1", tableWithDiff.get("Ц"));
        tableWithDiff.put("D", "d1");
        assertEquals("d1", tableWithDiff.get("D"));

        tableWithDiff.commit();
        assertEquals("a1", tableWithDiff.get("A"));
        assertEquals("b1", tableWithDiff.get("B"));
        assertEquals("ц1", tableWithDiff.get("Ц"));
        assertEquals("d1", tableWithDiff.get("D"));
        assertNull(tableWithDiff.get("E"));

        tableWithDiff.remove("A");
        assertNull(tableWithDiff.get("A"));
        tableWithDiff.put("B", "b1");
        assertEquals("b1", tableWithDiff.get("B"));
        tableWithDiff.put("D", "d2");
        assertEquals("d2", tableWithDiff.get("D"));
        tableWithDiff.put("E", "e1");
        assertEquals("e1", tableWithDiff.get("E"));

        tableWithDiff.remove("A");
        assertNull(tableWithDiff.get("A"));
        tableWithDiff.put("B", "b1");
        assertEquals("b1", tableWithDiff.get("B"));
        tableWithDiff.put("D", "d2");
        assertEquals("d2", tableWithDiff.get("D"));
        tableWithDiff.put("E", "e1");
        assertEquals("e1", tableWithDiff.get("E"));

        tableWithDiff.put("A", "a2");
        assertEquals("a2", tableWithDiff.get("A"));
        tableWithDiff.remove("B");
        assertNull(tableWithDiff.get("B"));
        tableWithDiff.remove("D");
        assertNull(tableWithDiff.get("D"));
        tableWithDiff.remove("E");
        assertNull(tableWithDiff.get("E"));

        tableWithDiff.commit();
        assertEquals("a2", tableWithDiff.get("A"));
        assertNull(tableWithDiff.get("B"));
        assertEquals("ц1", tableWithDiff.get("Ц"));
        assertNull(tableWithDiff.get("D"));
        assertNull(tableWithDiff.get("E"));


        tableWithDiff.remove("A");
        tableWithDiff.put("B", "b1");
        tableWithDiff.put("Ц", "ц2");
        tableWithDiff.remove("D");

        tableWithDiff.rollback();
        assertEquals("a2", tableWithDiff.get("A"));
        assertNull(tableWithDiff.get("B"));
        assertEquals("ц1", tableWithDiff.get("Ц"));
        assertNull(tableWithDiff.get("D"));
        assertNull(tableWithDiff.get("E"));

        tableWithDiff.put("F", "f1");

        tableWithDiff = new TableWithDiffImpl(new MultiFileMapImpl(path));
        assertEquals("a2", tableWithDiff.get("A"));
        assertNull(tableWithDiff.get("B"));
        assertEquals("ц1", tableWithDiff.get("Ц"));
        assertNull(tableWithDiff.get("D"));
        assertNull(tableWithDiff.get("E"));
        assertNull(tableWithDiff.get("F"));

        tableWithDiff.drop();

        tableWithDiff = new TableWithDiffImpl(new MultiFileMapImpl(path));
        assertNull(tableWithDiff.get("A"));
        assertNull(tableWithDiff.get("B"));
        assertNull(tableWithDiff.get("Ц"));
        assertNull(tableWithDiff.get("D"));
        assertNull(tableWithDiff.get("E"));
    }

    @Test
    public void testPut() throws Exception {
        assertNull(tableWithDiff.put("A", "a1"));
        assertNull(tableWithDiff.put("B", "b1"));
        assertNull(tableWithDiff.put("Ц", "ц1"));
        assertNull(tableWithDiff.put("D", "d1"));

        tableWithDiff.commit();

        tableWithDiff.remove("A");
        assertEquals("b1", tableWithDiff.put("B", "b1"));
        assertEquals("d1", tableWithDiff.put("D", "d2"));
        assertNull(tableWithDiff.put("E", "e1"));

        tableWithDiff.remove("A");
        assertEquals("b1", tableWithDiff.put("B", "b1"));
        assertEquals("d2", tableWithDiff.put("D", "d2"));
        assertEquals("e1", tableWithDiff.put("E", "e1"));

        assertNull(tableWithDiff.put("A", "a2"));
        tableWithDiff.remove("B");
        tableWithDiff.remove("D");
        tableWithDiff.remove("E");

        tableWithDiff.commit();

        tableWithDiff.remove("A");
        assertNull(tableWithDiff.put("B", "b1"));
        assertEquals("ц1", tableWithDiff.put("Ц", "ц2"));
        tableWithDiff.remove("D");
    }

    @Test
    public void testRemove() throws Exception {
        tableWithDiff.put("A", "a1");
        tableWithDiff.put("B", "b1");
        tableWithDiff.put("Ц", "ц1");
        tableWithDiff.put("D", "d1");

        tableWithDiff.commit();

        assertEquals("a1", tableWithDiff.remove("A"));
        tableWithDiff.put("B", "b1");
        tableWithDiff.put("D", "d2");
        tableWithDiff.put("E", "e1");

        assertNull(tableWithDiff.remove("A"));
        tableWithDiff.put("B", "b1");
        tableWithDiff.put("D", "d2");
        tableWithDiff.put("E", "e1");

        tableWithDiff.put("A", "a2");
        assertEquals("b1", tableWithDiff.remove("B"));
        assertEquals("d2", tableWithDiff.remove("D"));
        assertEquals("e1", tableWithDiff.remove("E"));

        tableWithDiff.commit();

        assertEquals("a2", tableWithDiff.remove("A"));
        tableWithDiff.put("B", "b1");
        tableWithDiff.put("Ц", "ц2");
        assertNull(tableWithDiff.remove("D"));
        assertNull(tableWithDiff.remove("F"));
    }

    @Test
    public void testSize() throws Exception {
        assertEquals(0, tableWithDiff.size());

        tableWithDiff.put("A", "a1");
        assertEquals(1, tableWithDiff.size());
        tableWithDiff.put("B", "b1");
        assertEquals(2, tableWithDiff.size());
        tableWithDiff.put("Ц", "ц1");
        assertEquals(3, tableWithDiff.size());
        tableWithDiff.put("D", "d1");
        assertEquals(4, tableWithDiff.size());

        tableWithDiff.commit();
        assertEquals(4, tableWithDiff.size());

        tableWithDiff.remove("A");
        assertEquals(3, tableWithDiff.size());
        tableWithDiff.put("B", "b1");
        assertEquals(3, tableWithDiff.size());
        tableWithDiff.put("D", "d2");
        assertEquals(3, tableWithDiff.size());
        tableWithDiff.put("E", "e1");
        assertEquals(4, tableWithDiff.size());

        tableWithDiff.remove("A");
        assertEquals(4, tableWithDiff.size());
        tableWithDiff.put("B", "b1");
        assertEquals(4, tableWithDiff.size());
        tableWithDiff.put("D", "d2");
        assertEquals(4, tableWithDiff.size());
        tableWithDiff.put("E", "e1");
        assertEquals(4, tableWithDiff.size());

        tableWithDiff.put("A", "a2");
        assertEquals(5, tableWithDiff.size());
        tableWithDiff.remove("B");
        assertEquals(4, tableWithDiff.size());
        tableWithDiff.remove("D");
        assertEquals(3, tableWithDiff.size());
        tableWithDiff.remove("E");
        assertEquals(2, tableWithDiff.size());

        tableWithDiff.commit();
        assertEquals(2, tableWithDiff.size());

        tableWithDiff.remove("A");
        assertEquals(1, tableWithDiff.size());
        tableWithDiff.put("B", "b1");
        assertEquals(2, tableWithDiff.size());
        tableWithDiff.put("Ц", "ц2");
        assertEquals(2, tableWithDiff.size());
        tableWithDiff.remove("D");
        assertEquals(2, tableWithDiff.size());
        tableWithDiff.put("F", "f1");
        assertEquals(3, tableWithDiff.size());

        tableWithDiff.rollback();
        assertEquals(2, tableWithDiff.size());

        tableWithDiff = new TableWithDiffImpl(multiFileMap);
        assertEquals(2, tableWithDiff.size());

        tableWithDiff.drop();

        tableWithDiff = new TableWithDiffImpl(multiFileMap);
        assertEquals(0, tableWithDiff.size());
    }

    @Test
    public void testCommit() throws Exception {
        tableWithDiff.put("A", "a1");
        tableWithDiff.put("B", "b1");
        tableWithDiff.put("Ц", "ц1");
        tableWithDiff.put("D", "d1");

        assertEquals(4, tableWithDiff.commit());

        tableWithDiff.remove("A");
        tableWithDiff.put("B", "b1");
        tableWithDiff.put("D", "d2");
        tableWithDiff.put("E", "e1");

        tableWithDiff.remove("A");
        tableWithDiff.put("B", "b1");
        tableWithDiff.put("D", "d2");
        tableWithDiff.put("E", "e1");

        tableWithDiff.put("A", "a2");
        tableWithDiff.remove("B");
        tableWithDiff.remove("D");
        tableWithDiff.remove("E");

        assertEquals(4, tableWithDiff.commit());
    }

    @Test
    public void testRollback() throws Exception {
        tableWithDiff.put("A", "a1");
        tableWithDiff.put("B", "b1");
        tableWithDiff.put("Ц", "ц1");
        tableWithDiff.put("D", "d1");

        assertEquals(4, tableWithDiff.rollback());
    }

    @Test
    public void testList() throws Exception {
        assertTrue(tableWithDiff.list().isEmpty());

        tableWithDiff.put("A", "a1");
        assertEquals(new HashSet<String>(Arrays.asList("A")),
                new HashSet<>(tableWithDiff.list()));
        tableWithDiff.put("B", "b1");
        assertEquals(new HashSet<String>(Arrays.asList("A", "B")),
                new HashSet<>(tableWithDiff.list()));
        tableWithDiff.put("Ц", "ц1");
        assertEquals(new HashSet<String>(Arrays.asList("A", "B", "Ц")),
                new HashSet<>(tableWithDiff.list()));
        tableWithDiff.put("D", "d1");
        assertEquals(new HashSet<String>(Arrays.asList("A", "B", "Ц", "D")),
                new HashSet<>(tableWithDiff.list()));

        tableWithDiff.commit();
        assertEquals(new HashSet<String>(Arrays.asList("A", "B", "Ц", "D")),
                new HashSet<>(tableWithDiff.list()));

        tableWithDiff.remove("A");
        assertEquals(new HashSet<String>(Arrays.asList("B", "Ц", "D")),
                new HashSet<>(tableWithDiff.list()));
        tableWithDiff.put("B", "b1");
        assertEquals(new HashSet<String>(Arrays.asList("B", "Ц", "D")),
                new HashSet<>(tableWithDiff.list()));;
        tableWithDiff.put("D", "d2");
        assertEquals(new HashSet<String>(Arrays.asList("B", "Ц", "D")),
                new HashSet<>(tableWithDiff.list()));
        tableWithDiff.put("E", "e1");
        assertEquals(new HashSet<String>(Arrays.asList("B", "Ц", "D", "E")),
                new HashSet<>(tableWithDiff.list()));

        tableWithDiff.remove("A");
        assertEquals(new HashSet<String>(Arrays.asList("B", "Ц", "D", "E")),
                new HashSet<>(tableWithDiff.list()));
        tableWithDiff.put("B", "b1");
        assertEquals(new HashSet<String>(Arrays.asList("B", "Ц", "D", "E")),
                new HashSet<>(tableWithDiff.list()));
        tableWithDiff.put("D", "d2");
        assertEquals(new HashSet<String>(Arrays.asList("B", "Ц", "D", "E")),
                new HashSet<>(tableWithDiff.list()));
        tableWithDiff.put("E", "e1");
        assertEquals(new HashSet<String>(Arrays.asList("B", "Ц", "D", "E")),
                new HashSet<>(tableWithDiff.list()));

        tableWithDiff.put("A", "a2");
        assertEquals(new HashSet<String>(Arrays.asList("A", "B", "Ц", "D", "E")),
                new HashSet<>(tableWithDiff.list()));
        tableWithDiff.remove("B");
        assertEquals(new HashSet<String>(Arrays.asList("A", "Ц", "D", "E")),
                new HashSet<>(tableWithDiff.list()));
        tableWithDiff.remove("D");
        assertEquals(new HashSet<String>(Arrays.asList("A", "Ц", "E")),
                new HashSet<>(tableWithDiff.list()));
        tableWithDiff.remove("E");
        assertEquals(new HashSet<String>(Arrays.asList("A", "Ц")),
                new HashSet<>(tableWithDiff.list()));

        tableWithDiff.commit();
        assertEquals(new HashSet<String>(Arrays.asList("A", "Ц")),
                new HashSet<>(tableWithDiff.list()));


        tableWithDiff.remove("A");
        tableWithDiff.put("B", "b1");
        tableWithDiff.put("Ц", "ц2");
        tableWithDiff.remove("D");
        tableWithDiff.put("F", "f1");

        tableWithDiff.rollback();
        assertEquals(new HashSet<String>(Arrays.asList("A", "Ц")),
                new HashSet<>(tableWithDiff.list()));

        tableWithDiff.put("F", "f1");

        tableWithDiff = new TableWithDiffImpl(new MultiFileMapImpl(path));
        assertEquals(new HashSet<String>(Arrays.asList("A", "Ц")),
                new HashSet<>(tableWithDiff.list()));

        tableWithDiff.drop();

        tableWithDiff = new TableWithDiffImpl(new MultiFileMapImpl(path));
        assertTrue(tableWithDiff.list().isEmpty());
    }
}
