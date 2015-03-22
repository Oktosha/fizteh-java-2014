package ru.fizteh.fivt.students.Oktosha.proxy;

import org.junit.Test;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.List;

import static org.junit.Assert.*;

public class XMLInvocationSerializerTest {

    XMLInvocationSerializer serializer = new XMLInvocationSerializer();
    String serializedList = "<list>"
                                + "<value>" + "Hello, world!" + "</value>"
                                + "<value>" + "42"            + "</value>"
                                + "<value>"
                                    + "<null/>"
                                + "</value>"
                                + "<value>"
                                    + "<cyclic/>"
                                + "</value>"
                            + "</list>";
    String tabulatedList =    "<list>\n"
                            + "    <value>" + "Hello, world!" + "</value>\n"
                            + "    <value>" + "42"            + "</value>\n"
                            + "    <value>\n"
                            + "        <null/>\n"
                            + "    </value>\n"
                            + "    <value>\n"
                            + "        <cyclic/>\n"
                            + "    </value>\n"
                            + "</list>\n";

    class SomeClass {
        int x = 0;

        public int sumWithX(int y) {
            return y;
        }

        public void incX() {
            ++x;
        }
    }

    String voidMethodInvoke = "<invoke timestamp=\"12345\" "
            + "class=\"ru.fizteh.fivt.students.Oktosha.proxy.XMLInvocationSerializerTest$SomeClass\" "
            + "name=\"incX\">"
            + "<arguments/>"
            + "</invoke>";

    String intMethodInvoke = "<invoke timestamp=\"12345\" "
            + "class=\"ru.fizteh.fivt.students.Oktosha.proxy.XMLInvocationSerializerTest$SomeClass\" "
            + "name=\"sumWithX\">"
            + "<arguments>"
            + "<argument>1</argument>"
            + "</arguments>"
            + "<return>4</return>"
            + "</invoke>";

    String exceptionMethodInvoke = "<invoke timestamp=\"12345\" "
            + "class=\"ru.fizteh.fivt.students.Oktosha.proxy.XMLInvocationSerializerTest$SomeClass\" "
            + "name=\"sumWithX\">"
            + "<arguments>"
            + "<argument>1</argument>"
            + "</arguments>"
            + "<thrown>java.io.IOException: azaza exception</thrown>"
            + "</invoke>";

    @Test
    public void testSerializeWithoutIndentation() throws Exception {
        assertEquals(voidMethodInvoke,
                serializer.serializeWithoutIndentation(SomeClass.class.getMethod("incX"),
                        null, SomeClass.class, null, null, 12345));
        assertEquals(intMethodInvoke,
                serializer.serializeWithoutIndentation(SomeClass.class.getMethod("sumWithX", int.class),
                new Object[]{1}, SomeClass.class, 4, null, 12345));
        assertEquals(exceptionMethodInvoke,
                serializer.serializeWithoutIndentation(SomeClass.class.getMethod("sumWithX", int.class),
                new Object[]{1}, SomeClass.class, null, new IOException("azaza exception"), 12345));
    }

    @Test
    public void testWriteObject() throws Exception {
        StringWriter stringWriter = new StringWriter();
        XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
        XMLStreamWriter xmlWriter = outputFactory.createXMLStreamWriter(stringWriter);

        List<Object> list = new ArrayList<>(Arrays.asList("Hello, world!", 42, null));
        list.add(list);
        serializer.writeObject(list, xmlWriter, new IdentityHashMap<>());
        assertEquals(serializedList, stringWriter.toString());
    }

    @Test
    public void testAddIndentation() throws Exception {
        assertEquals(tabulatedList, serializer.addIndentation(serializedList));
    }
}

