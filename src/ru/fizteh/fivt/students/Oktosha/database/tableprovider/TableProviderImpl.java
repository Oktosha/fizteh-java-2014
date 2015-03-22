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
    ReadWriteLock tablesPoolRWL = new ReentrantReadWriteLock(true);
    ReadWriteLock tableProviderIsClosedRWL = new ReentrantReadWriteLock(true);
    boolean tableProviderIsClosed = false;
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
        try {
            tableProviderIsClosedRWL.readLock().lock();
            tablesPoolRWL.writeLock().lock();
            if (tableProviderIsClosed) {
                throw new IllegalStateException("tableProviderIsClosed");
            }
            if (badTableNamePredicate.test(name)) {
                throw new IllegalArgumentException();
            }
            if ((tables.get(name) != null) && (tables.get(name).isClosed())) {
                tables.put(name, tableFactory.createTableFromData(path.resolve(name), codec));
            }
            return tables.get(name);
        } catch (IOException e) {
            throw new IllegalStateException("can't restore closed table " + name);
        } finally {
            tablesPoolRWL.writeLock().unlock();
            tableProviderIsClosedRWL.readLock().unlock();
        }
    }

    @Override
    public DroppableStructuredTable createTable(String name, List<Class<?>> columnTypes) throws IOException {
        try {
            tableProviderIsClosedRWL.readLock().lock();
            tablesPoolRWL.writeLock().lock();
            if (tableProviderIsClosed) {
                throw new IllegalStateException("tableProviderIsClosed");
            }
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
            tablesPoolRWL.writeLock().unlock();
            tableProviderIsClosedRWL.readLock().unlock();
        }
    }

    @Override
    public void removeTable(String name) throws IOException {
        try {
            tableProviderIsClosedRWL.readLock().lock();
            tablesPoolRWL.writeLock().lock();
            if (tableProviderIsClosed) {
                throw new IllegalStateException("tableProviderIsClosed");
            }
            if (badTableNamePredicate.test(name)) {
                throw new IllegalArgumentException("tableName is incorrect");
            }
            if (!tables.containsKey(name)) {
                throw new IllegalStateException();
            }

            tables.get(name).drop();
            tables.remove(name);

        } finally {
            tablesPoolRWL.writeLock().unlock();
            tableProviderIsClosedRWL.readLock().unlock();
        }
    }

    @Override
    public Storeable deserialize(Table table, String value) throws ParseException {
        try {
            tableProviderIsClosedRWL.readLock().lock();
            if (tableProviderIsClosed) {
                throw new IllegalStateException("tableProviderIsClosed");
            }
            return codec.deserialize(getSignature(table), value);
        } finally {
            tableProviderIsClosedRWL.readLock().unlock();
        }
    }

    @Override
    public String serialize(Table table, Storeable value) throws ColumnFormatException {
        try {
            tableProviderIsClosedRWL.readLock().lock();
            if (tableProviderIsClosed) {
                throw new IllegalStateException("tableProviderIsClosed");
            }
            return codec.serialize(getSignature(table), value);
        } finally {
            tableProviderIsClosedRWL.readLock().unlock();
        }
    }

    @Override
    public Storeable createFor(Table table) {
        try {
            tableProviderIsClosedRWL.readLock().lock();
            if (tableProviderIsClosed) {
                throw new IllegalStateException("tableProviderIsClosed");
            }
            return new StoreableImpl(getSignature(table));
        } finally {
            tableProviderIsClosedRWL.readLock().unlock();
        }
    }

    @Override
    public Storeable createFor(Table table, List<?> values) throws ColumnFormatException, IndexOutOfBoundsException {
        try {
            tableProviderIsClosedRWL.readLock().lock();
            if (tableProviderIsClosed) {
                throw new IllegalStateException("tableProviderIsClosed");
            }
            Storeable storeable = new StoreableImpl(getSignature(table));
            for (int i = 0; i < values.size(); ++i) {
                storeable.setColumnAt(i, values.get(i));
            }
            return storeable;
        } finally {
            tableProviderIsClosedRWL.readLock().unlock();
        }
    }

    private List<SignatureElement> getSignature(Table table) {
        try {
            tableProviderIsClosedRWL.readLock().lock();
            if (tableProviderIsClosed) {
                throw new IllegalStateException("tableProviderIsClosed");
            }
            List<SignatureElement> signature = new ArrayList<>();
            for (int i = 0; i < table.getColumnsCount(); ++i) {
                signature.add(SignatureElement.getSignatureElementByClass(table.getColumnType(i)));
            }
            return signature;
        } finally {
            tableProviderIsClosedRWL.readLock().unlock();
        }
    }

    @Override
    public List<String> getTableNames() {
        tableProviderIsClosedRWL.readLock().lock();
        tablesPoolRWL.readLock().lock();
        try {
            if (tableProviderIsClosed) {
                throw new IllegalStateException("tableProviderIsClosed");
            }
            return new ArrayList<>(tables.keySet());
        } finally {
            tablesPoolRWL.readLock().unlock();
            tableProviderIsClosedRWL.readLock().unlock();
        }
    }

    @Override
    public void close() throws Exception {
        try {
            tableProviderIsClosedRWL.writeLock().lock();
            tableProviderIsClosed = true;
        } finally {
            tableProviderIsClosedRWL.writeLock().lock();
        }
    }
}
