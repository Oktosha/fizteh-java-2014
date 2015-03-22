package ru.fizteh.fivt.students.Oktosha.database.storeable;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;

import java.text.ParseException;
import java.util.List;

/**
 * Created by DKolodzey on 06.03.15.
 */

public interface StoreableSerializerDeserializer {
    String serialize(List<SignatureElement> signature, Storeable value)
            throws ColumnFormatException, IndexOutOfBoundsException;
    Storeable deserialize(List<SignatureElement> signature, String serializedValue) throws ParseException;
}
