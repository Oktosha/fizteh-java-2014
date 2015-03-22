package ru.fizteh.fivt.students.Oktosha.database.storeable;

import org.junit.Test;

import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static org.junit.Assert.*;

public class DroppableStructuredTableImplTest {

    @Test
    public void testReadSignature() throws Exception {
        StringReader stringReader = new StringReader("boolean String int double String");
        Scanner scanner = new Scanner(stringReader);

        List<SignatureElement> signature = DroppableStructuredTableImpl.readSignature(scanner);

        assertEquals(SignatureElement.BOOLEAN, signature.get(0));
        assertEquals(SignatureElement.STRING, signature.get(1));
        assertEquals(SignatureElement.INTEGER, signature.get(2));
        assertEquals(SignatureElement.DOUBLE, signature.get(3));
        assertEquals(SignatureElement.STRING, signature.get(4));
    }

    @Test
    public void testWriteSignature() throws Exception {
        List<SignatureElement> signature = new ArrayList<>();
        signature.add(SignatureElement.BOOLEAN);
        signature.add(SignatureElement.STRING);
        signature.add(SignatureElement.INTEGER);
        signature.add(SignatureElement.DOUBLE);
        signature.add(SignatureElement.STRING);

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);

        DroppableStructuredTableImpl.writeSignature(printWriter, signature);
        assertEquals("boolean String int double String ", stringWriter.toString());
    }
}
