package ru.fizteh.fivt.students.Oktosha.proxy;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.IdentityHashMap;

/**
 * Created by DKolodzey on 22.03.15.
 * implements serializing logs into JSON
 */
public class XMLInvocationSerializer implements InvocationSerializer {
    @Override
    public String serialize(Method method, Object[] args, Class<?> implClass,
                            Object returnValue, Throwable thrown, long timestamp) {
        try {
            return addIndentation(serializeWithoutIndentation(method, args, implClass, returnValue, thrown, timestamp));
        } catch (TransformerException | XMLStreamException e) {
            throw new IllegalStateException("failed to serialize into XML", e);
        }
    }

    String serializeWithoutIndentation(Method method, Object[] args, Class<?> implClass,
                                       Object returnValue, Throwable thrown, long timestamp) throws XMLStreamException {
        XMLStreamWriter xmlWriter;
        StringWriter stringWriter = new StringWriter();
        XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
        xmlWriter = outputFactory.createXMLStreamWriter(stringWriter);

        xmlWriter.writeStartElement("invoke");
        xmlWriter.writeAttribute("timestamp", String.valueOf(timestamp));
        xmlWriter.writeAttribute("class", implClass.getName());
        xmlWriter.writeAttribute("name", method.getName());

        if (args.length == 0) {
            xmlWriter.writeEmptyElement("arguments");
        } else {
            xmlWriter.writeStartElement("arguments");
            for (Object arg : args) {
                xmlWriter.writeStartElement("argument");
                writeObject(arg, xmlWriter, new IdentityHashMap<>());
                xmlWriter.writeEndElement(); /* argument */
            }
            xmlWriter.writeEndElement(); /* arguments */
        }
        if (thrown != null) {
            xmlWriter.writeStartElement("thrown");
            xmlWriter.writeCharacters(thrown.toString());
            xmlWriter.writeEndElement(); /* thrown */
        } else if (method.getReturnType() != void.class) {
            xmlWriter.writeStartElement("return");
            writeObject(returnValue, xmlWriter, new IdentityHashMap<>());
            xmlWriter.writeEndElement(); /* return */
        }
        xmlWriter.writeEndElement(); /* invoke */
        xmlWriter.flush();
        xmlWriter.close();
        return stringWriter.toString();
    }

    String addIndentation(String xmlString) throws TransformerException {
        TransformerFactory factory = TransformerFactory.newInstance();

        Transformer transformer = factory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

        StringWriter formattedStringWriter = new StringWriter();
        transformer.transform(new StreamSource(new StringReader(xmlString)),
                              new StreamResult(formattedStringWriter));
        return formattedStringWriter.toString();
    }

    void writeObject(Object object, XMLStreamWriter xmlWriter,
                     IdentityHashMap<Object, Void> map) throws XMLStreamException {
        if (object == null) {
            xmlWriter.writeEmptyElement("null");
            return;
        }
        if (!(object instanceof Iterable)) {
            xmlWriter.writeCharacters(object.toString());
            return;
        }
        if (map.containsKey(object)) {
            xmlWriter.writeEmptyElement("cyclic");
            return;
        }
        map.put(object, null);
        Iterable iterableObject = (Iterable) object;
        xmlWriter.writeStartElement("list");
            for (Object innerObject : iterableObject) {
                xmlWriter.writeStartElement("value");
                writeObject(innerObject, xmlWriter, map);
                xmlWriter.writeEndElement(); /* value */
            }
        xmlWriter.writeEndElement(); /* list */
        map.remove(object);
    }
}
