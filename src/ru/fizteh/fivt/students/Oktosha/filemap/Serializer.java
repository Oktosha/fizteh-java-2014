package ru.fizteh.fivt.students.Oktosha.filemap;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;

import java.util.List;

/**
 * Created by DKolodzey on 06.03.15.
 */

public interface Serializer {
    String serialize(List<SignatureElement> signature, Storeable value) throws ColumnFormatException, IndexOutOfBoundsException;
}
