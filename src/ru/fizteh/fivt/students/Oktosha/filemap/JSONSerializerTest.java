package ru.fizteh.fivt.students.Oktosha.filemap;

import org.junit.Test;
import ru.fizteh.fivt.storage.structured.Storeable;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class JSONSerializerTest {

    @Test
    public void testSerialize() throws Exception {
        List<SignatureElement> signature = new ArrayList<>();
        signature.add(SignatureElement.BOOLEAN);
        signature.add(SignatureElement.STRING);
        signature.add(SignatureElement.INTEGER);
        signature.add(SignatureElement.DOUBLE);
        signature.add(SignatureElement.STRING);
        Storeable value = new StoreableImpl(signature);
        value.setColumnAt(0, true);
        value.setColumnAt(1, "value  f ");
        value.setColumnAt(2, 42);
        value.setColumnAt(3, 36.6);
        Serializer serializer = new JSONSerializer();
        assertEquals("[true,\"value  f \",42,36.6,null]", serializer.serialize(signature, value));
    }
}
