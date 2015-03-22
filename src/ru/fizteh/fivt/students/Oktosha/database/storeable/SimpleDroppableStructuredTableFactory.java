package ru.fizteh.fivt.students.Oktosha.database.storeable;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Created by DKolodzey on 22.03.15.
 */
public class SimpleDroppableStructuredTableFactory implements DroppableStructuredTableFactory {
    @Override
    public DroppableStructuredTable createTableFromData(Path path,
                                                        StoreableSerializerDeserializer codec) throws IOException {
        return new DroppableStructuredTableImpl(path, codec);
    }

    @Override
    public DroppableStructuredTable createEmptyTable(Path path,
                                                     StoreableSerializerDeserializer codec,
                                                     List<SignatureElement> signature) throws IOException {
        return new DroppableStructuredTableImpl(path, codec, signature);
    }
}
