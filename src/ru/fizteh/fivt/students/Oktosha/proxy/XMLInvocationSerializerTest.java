package ru.fizteh.fivt.students.Oktosha.proxy;

import org.junit.Test;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

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

    @Test
    public void testSerialize() throws Exception {
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

