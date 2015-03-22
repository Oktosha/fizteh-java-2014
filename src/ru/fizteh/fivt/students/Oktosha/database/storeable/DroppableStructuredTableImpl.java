package ru.fizteh.fivt.students.Oktosha.database.storeable;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.Oktosha.database.Diff;
import ru.fizteh.fivt.students.Oktosha.database.filebackend.MultiFileMapImpl;
import ru.fizteh.fivt.students.Oktosha.database.string.StringTableWithDiff;
import ru.fizteh.fivt.students.Oktosha.database.string.StringTableWithDiffImpl;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by DKolodzey on 07.03.15.
 * DroppableStructuredTableImpl implements extended interface Table from structured
 * which will be held inside TableProviderImpl
 */
public class DroppableStructuredTableImpl implements DroppableStructuredTable {

    Path path;
    StringTableWithDiff backEndTable;
    List<SignatureElement> signature;
    StoreableSerializerDeserializer codec;
    private boolean tableIsClosed = false;
    private final ReadWriteLock beingClosedRWL = new ReentrantReadWriteLock(true);


    public DroppableStructuredTableImpl(Path path, StoreableSerializerDeserializer codec) throws IOException {
        if (!path.toFile().exists()) {
            throw new IOException("bd folder does not exist");
        }
        if (!path.toFile().isDirectory()) {
            throw new IOException("bd folder is not a folder");
        }

        this.path = path;
        this.signature = readSignature(path.resolve("signature.tsv"));
        this.backEndTable = new StringTableWithDiffImpl(new MultiFileMapImpl(path));
        this.codec = codec;


        List<String> keys = this.backEndTable.list();
        try {
            for (String key : keys) {
                codec.deserialize(signature, backEndTable.get(key));
            }
        } catch (ParseException e) {
            throw new IOException(e);
        }
    }

    public DroppableStructuredTableImpl(Path path, StoreableSerializerDeserializer codec,
                                        List<SignatureElement> signature) throws IOException {
        if (path.toFile().exists()) {
            throw new IOException("failed to create table; folder already exists");
        }

        Files.createDirectory(path);
        writeSignature(path.resolve("signature.tsv"), signature);

        this.path = path;
        this.signature = signature;
        this.backEndTable = new StringTableWithDiffImpl(new MultiFileMapImpl(path));
        this.codec = codec;
    }

    public static List<SignatureElement> readSignature(Path signaturePath) throws IOException {
        if (!signaturePath.toFile().exists()) {
            throw new IOException("bd does not contain signature");
        }
        try (FileInputStream inputStream = new FileInputStream(signaturePath.toFile())) {
            return readSignature(new Scanner(inputStream));
        }
    }

    public static List<SignatureElement> readSignature(Scanner scanner) throws IOException {
        List<SignatureElement> signature = new ArrayList<>();
        try {
            while (scanner.hasNext()) {
                signature.add(SignatureElement.getSignatureElementByName(scanner.next()));
            }
        } catch (EnumConstantNotPresentException e) {
            throw new IOException("signature is broken", e);
        }
        return signature;
    }

    public static void writeSignature(Path signaturePath, List<SignatureElement> signature) throws IOException {
        if (signaturePath.toFile().exists()) {
            throw new IOException("bd already contains signature; can't write");
        }
        try (FileOutputStream outputStream = new FileOutputStream(signaturePath.toFile());
             PrintWriter writer = new PrintWriter(outputStream)) {
            writeSignature(writer, signature);
        }
    }

    public static void writeSignature(PrintWriter writer, List<SignatureElement> signature) {
        for (SignatureElement signatureElement : signature) {
            writer.print(signatureElement.getName() + " ");
        }
    }

    @Override
    public Storeable put(String key, Storeable value) throws ColumnFormatException {
        beingClosedRWL.readLock().lock();
        try {
            if (tableIsClosed) {
                throw new IllegalStateException("tableIsDropped");
            }
            try {
                String encodedValue = backEndTable.put(key, codec.serialize(signature, value));
                if (encodedValue == null) {
                    return null; //no value on this key before
                }
                return codec.deserialize(signature, encodedValue);
            } catch (ParseException e) {
                throw new IllegalStateException("failed to deserialize held value");
            }
        } finally {
            beingClosedRWL.readLock().unlock();
        }
    }

    @Override
    public Storeable remove(String key) {
        beingClosedRWL.readLock().lock();
        try {
            if (tableIsClosed) {
                throw new IllegalStateException("tableIsDropped");
            }
            try {
                String encodedValue = backEndTable.remove(key);
                if (encodedValue == null) {
                    return null; //no value on this key before
                }
                return codec.deserialize(signature, encodedValue);
            } catch (ParseException e) {
                throw new IllegalStateException("failed to deserialize held value");
            }
        } finally {
            beingClosedRWL.readLock().unlock();
        }
    }

    @Override
    public int size() {
        beingClosedRWL.readLock().lock();
        try {
            if (tableIsClosed) {
                throw new IllegalStateException("tableIsDropped");
            }
            return backEndTable.size();
        } finally {
            beingClosedRWL.readLock().unlock();
        }
    }

    @Override
    public List<String> list() {
        beingClosedRWL.readLock().lock();
        try {
            if (tableIsClosed) {
                throw new IllegalStateException("tableIsDropped");
            }
            return backEndTable.list();
        } finally {
            beingClosedRWL.readLock().unlock();
        }
    }

    @Override
    public int commit() throws IOException {
        beingClosedRWL.readLock().lock();
        try {
            if (tableIsClosed) {
                throw new IllegalStateException("tableIsDropped");
            }
            return backEndTable.commit();
        } finally {
            beingClosedRWL.readLock().unlock();
        }
    }

    @Override
    public int rollback() {
        beingClosedRWL.readLock().lock();
        try {
            if (tableIsClosed) {
                throw new IllegalStateException("tableIsDropped");
            }
            return backEndTable.rollback();
        } finally {
            beingClosedRWL.readLock().unlock();
        }
    }

    @Override
    public int getNumberOfUncommittedChanges() {
        beingClosedRWL.readLock().lock();
        try {
            if (tableIsClosed) {
                throw new IllegalStateException("tableIsDropped");
            }
            return backEndTable.getNumberOfUncommittedChanges();
        } finally {
            beingClosedRWL.readLock().unlock();
        }
    }

    @Override
    public int getColumnsCount() {
        beingClosedRWL.readLock().lock();
        try {
            if (tableIsClosed) {
                throw new IllegalStateException("tableIsDropped");
            }
            return signature.size();
        } finally {
            beingClosedRWL.readLock().unlock();
        }
    }

    @Override
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        beingClosedRWL.readLock().lock();
        try {
            if (tableIsClosed) {
                throw new IllegalStateException("tableIsDropped");
            }
            return signature.get(columnIndex).getJavaClass();
        } finally {
            beingClosedRWL.readLock().unlock();
        }
    }

    @Override
    public String getName() {
        beingClosedRWL.readLock().lock();
        try {
            if (tableIsClosed) {
                throw new IllegalStateException("tableIsDropped");
            }
            return backEndTable.getName();
        } finally {
            beingClosedRWL.readLock().unlock();
        }
    }

    @Override
    public Storeable get(String key) {
        beingClosedRWL.readLock().lock();
        try {
            if (tableIsClosed) {
                throw new IllegalStateException("tableIsDropped");
            }
            try {
                String encodedValue = backEndTable.get(key);
                if (encodedValue == null) {
                    return null; //no value on this key before
                }
                return codec.deserialize(signature, encodedValue);
            } catch (ParseException e) {
                throw new IllegalStateException("failed to deserialize held value");
            }
        } finally {
            beingClosedRWL.readLock().unlock();
        }
    }

    @Override
    public void setDiff(Diff diff) {
        try {
            beingClosedRWL.readLock().lock();
            if (tableIsClosed) {
                throw new IllegalStateException("tableIsDropped");
            }
            backEndTable.setDiff(diff);
        } finally {
            beingClosedRWL.readLock().unlock();
        }
    }

    @Override
    public void close() throws Exception {
        try {
            beingClosedRWL.writeLock().lock();
            if (tableIsClosed) {
                throw new IllegalStateException("tableIsDropped");
            }
            tableIsClosed = true;
            backEndTable.rollback();
        } finally {
            beingClosedRWL.writeLock().unlock();
        }
    }

    /* Dropping table from table (not from table provider) will make him to create new empty table instead */

    @Override
    public void drop() throws IOException {
        try {
            beingClosedRWL.writeLock().lock();
            if (tableIsClosed) {
                throw new IllegalStateException("tableIsDropped");
            }
            backEndTable.clear();
            Files.deleteIfExists(path.resolve("signature.tsv"));
            Files.deleteIfExists(path);
            tableIsClosed = true;
        } finally {
            beingClosedRWL.writeLock().unlock();
        }
    }

    /* Method for table provider to learn something about dropped/closed table */

    @Override
    public boolean isClosed() {
        return tableIsClosed;
    }
}

