package ru.fizteh.fivt.students.Oktosha.proxy;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.IdentityHashMap;

/**
 * Created by DKolodzey on 22.03.15.
 * implements serializing logs into JSON
 */
public class XMLInvocationSerializer implements InvocationSerializer {
    @Override
    public String serialize(Method method, Object[] args, Class<?> implClass, Object returnValue, Throwable thrown) {
        try {
            StringWriter stringWriter = new StringWriter();
            XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
            XMLStreamWriter xmlWriter = outputFactory.createXMLStreamWriter(stringWriter);

            xmlWriter.writeStartElement("invoke");
            xmlWriter.writeAttribute("timestamp", String.valueOf(System.currentTimeMillis()));
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
            if (!(returnValue instanceof Void)) {
                xmlWriter.writeStartElement("return");
                writeObject(returnValue, xmlWriter, new IdentityHashMap<>());
                xmlWriter.writeEndElement(); /* return */
            }
            xmlWriter.writeEndElement(); /* invoke */
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
        return null;
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
        if (map.put(object, null) != null) {
            xmlWriter.writeEmptyElement("cyclic");
            return;
        }
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
