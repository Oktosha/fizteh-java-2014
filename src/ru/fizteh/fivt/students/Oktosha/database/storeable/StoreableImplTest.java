package ru.fizteh.fivt.students.Oktosha.database.storeable;

import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.storage.structured.Storeable;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class StoreableImplTest {

    private Storeable value;

    @Before
    public void setUp() {
        List<SignatureElement> signature = new ArrayList<>();
        signature.add(SignatureElement.BOOLEAN);
        signature.add(SignatureElement.STRING);
        signature.add(SignatureElement.INTEGER);
        signature.add(SignatureElement.DOUBLE);
        signature.add(SignatureElement.STRING);

        value = new StoreableImpl(signature);
        value.setColumnAt(0, null);
        value.setColumnAt(1, "value  f ");
        value.setColumnAt(2, 42);
        value.setColumnAt(3, 36.6);
        value.setColumnAt(4, null);
    }
    @Test
    public void testToString() throws Exception {
        assertEquals("StoreableImpl[,value  f ,42,36.6,]", value.toString());
    }
}
