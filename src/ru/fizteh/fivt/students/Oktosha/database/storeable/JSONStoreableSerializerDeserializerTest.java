package ru.fizteh.fivt.students.Oktosha.database.storeable;

import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.storage.structured.Storeable;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class JSONStoreableSerializerDeserializerTest {

    private List<SignatureElement> signature;
    private StoreableSerializerDeserializer storeableSerializerDeserializer;
    private Storeable value;

    @Before
    public void setUp() {
        signature = new ArrayList<>();
        signature.add(SignatureElement.BOOLEAN);
        signature.add(SignatureElement.STRING);
        signature.add(SignatureElement.INTEGER);
        signature.add(SignatureElement.DOUBLE);
        signature.add(SignatureElement.STRING);

        storeableSerializerDeserializer = new JSONStoreableSerializerDeserializer();

        value = new StoreableImpl(signature);
    }

    @Test
    public void testSerialize() throws Exception {

        value.setColumnAt(0, true);
        value.setColumnAt(1, "value  f ");
        value.setColumnAt(2, 42);
        value.setColumnAt(3, 36.6);

        assertEquals("[true,\"value  f \",42,36.6,null]", storeableSerializerDeserializer.serialize(signature, value));
    }

    @Test
    public void testDeserialize() throws Exception {

        value = storeableSerializerDeserializer.deserialize(signature, "[true,\"value  f \",42,36.6,null]");

        assertEquals(true, value.getBooleanAt(0));
        assertEquals("value  f ", value.getStringAt(1));
        assertEquals((Integer) 42, value.getIntAt(2));
        assertEquals((Double) 36.6, value.getDoubleAt(3));
        assertNull(value.getStringAt(4));
    }
}
