package ru.fizteh.fivt.students.Oktosha.database.tableprovider;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.Oktosha.database.storeable.*;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Predicate;

/**
 * Created by DKolodzey on 12.03.15.
 * Thread safe implementation of TableProvider
 */
public class TableProviderImpl implements ExtendedTableProvider {
    Path path;
    Map<String, DroppableStructuredTable> tables = new HashMap<>();
    Predicate<String> badTableNamePredicate = (s)->(s == null);
    StoreableSerializerDeserializer codec = new JSONStoreableSerializerDeserializer();
    ReadWriteLock rwl = new ReentrantReadWriteLock(true);
    final DroppableStructuredTableFactory tableFactory;

    TableProviderImpl(Path path, DroppableStructuredTableFactory tableFactory) throws IOException {
        this.path = path;
        this.tableFactory = tableFactory;
        if (!this.path.toFile().isDirectory()) {
            throw new IOException("bd dir is not a dir");
        }
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path, (f)->(f.toFile().isDirectory()))) {
            for (Path tableDir : stream) {
                tables.put(tableDir.getFileName().toString(), tableFactory.createTableFromData(tableDir, codec));
            }
        }
    }

    @Override
    public DroppableStructuredTable getTable(String name) {
        rwl.readLock().lock();
        try {
            if (badTableNamePredicate.test(name)) {
                throw new IllegalArgumentException();
            }
            return tables.get(name);
        } finally {
            rwl.readLock().unlock();
        }
    }

    @Override
    public DroppableStructuredTable createTable(String name, List<Class<?>> columnTypes) throws IOException {
        rwl.writeLock().lock();
        try {

            if (badTableNamePredicate.test(name)) {
                throw new IllegalArgumentException("tableName is incorrect");
            }
            if (tables.containsKey(name)) {
                return null;
            }

            List<SignatureElement> signature = new ArrayList<>();
            for (Class<?> columnType : columnTypes) {
                try {
                    signature.add(SignatureElement.getSignatureElementByClass(columnType));
                } catch (EnumConstantNotPresentException | NullPointerException e) {
                    throw new IllegalArgumentException(e);
                }
            }

            tables.put(name, tableFactory.createEmptyTable(path.resolve(name), codec, signature));
            return tables.get(name);

        } finally {
            rwl.writeLock().unlock();
        }
    }

    @Override
    public void removeTable(String name) throws IOException {
        rwl.writeLock().lock();
        try {

            if (badTableNamePredicate.test(name)) {
                throw new IllegalArgumentException("tableName is incorrect");
            }
            if (!tables.containsKey(name)) {
                throw new IllegalStateException();
            }

            tables.get(name).drop();
            tables.remove(name);

        } finally {
            rwl.writeLock().unlock();
        }
    }

    @Override
    public Storeable deserialize(Table table, String value) throws ParseException {
        return codec.deserialize(getSignature(table), value);
    }

    @Override
    public String serialize(Table table, Storeable value) throws ColumnFormatException {
        return codec.serialize(getSignature(table), value);
    }

    @Override
    public Storeable createFor(Table table) {
        return new StoreableImpl(getSignature(table));
    }

    @Override
    public Storeable createFor(Table table, List<?> values) throws ColumnFormatException, IndexOutOfBoundsException {
        Storeable storeable = new StoreableImpl(getSignature(table));
        for (int i = 0; i < values.size(); ++i) {
            storeable.setColumnAt(i, values.get(i));
        }
        return storeable;
    }

    private List<SignatureElement> getSignature(Table table) {
        List<SignatureElement> signature = new ArrayList<>();
        for (int i = 0; i < table.getColumnsCount(); ++i) {
            signature.add(SignatureElement.getSignatureElementByClass(table.getColumnType(i)));
        }
        return signature;
    }

    @Override
    public List<String> getTableNames() {
        rwl.readLock().lock();
        try {
            return new ArrayList<>(tables.keySet());
        } finally {
            rwl.readLock().unlock();
        }
    }
}
