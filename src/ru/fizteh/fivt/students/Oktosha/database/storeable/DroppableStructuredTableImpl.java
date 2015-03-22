package ru.fizteh.fivt.students.Oktosha.database.storeable;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
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
    private boolean tableIsDropped = false;
    private final ReadWriteLock beingDroppedRWL = new ReentrantReadWriteLock(true);


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
        beingDroppedRWL.readLock().lock();
        try {
            if (tableIsDropped) {
                throw new IllegalStateException();
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
            beingDroppedRWL.readLock().unlock();
        }
    }

    @Override
    public Storeable remove(String key) {
        beingDroppedRWL.readLock().lock();
        try {
            if (tableIsDropped) {
                throw new IllegalStateException();
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
            beingDroppedRWL.readLock().unlock();
        }
    }

    @Override
    public int size() {
        beingDroppedRWL.readLock().lock();
        try {
            if (tableIsDropped) {
                throw new IllegalStateException();
            }
            return backEndTable.size();
        } finally {
            beingDroppedRWL.readLock().unlock();
        }
    }

    @Override
    public List<String> list() {
        beingDroppedRWL.readLock().lock();
        try {
            if (tableIsDropped) {
                throw new IllegalStateException();
            }
            return backEndTable.list();
        } finally {
            beingDroppedRWL.readLock().unlock();
        }
    }

    @Override
    public int commit() throws IOException {
        beingDroppedRWL.readLock().lock();
        try {
            if (tableIsDropped) {
                throw new IllegalStateException();
            }
            return backEndTable.commit();
        } finally {
            beingDroppedRWL.readLock().unlock();
        }
    }

    @Override
    public int rollback() {
        beingDroppedRWL.readLock().lock();
        try {
            if (tableIsDropped) {
                throw new IllegalStateException();
            }
            return backEndTable.rollback();
        } finally {
            beingDroppedRWL.readLock().unlock();
        }
    }

    @Override
    public int getNumberOfUncommittedChanges() {
        beingDroppedRWL.readLock().lock();
        try {
            if (tableIsDropped) {
                throw new IllegalStateException();
            }
            return backEndTable.getNumberOfUncommittedChanges();
        } finally {
            beingDroppedRWL.readLock().unlock();
        }
    }

    @Override
    public int getColumnsCount() {
        beingDroppedRWL.readLock().lock();
        try {
            if (tableIsDropped) {
                throw new IllegalStateException();
            }
            return signature.size();
        } finally {
            beingDroppedRWL.readLock().unlock();
        }
    }

    @Override
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        beingDroppedRWL.readLock().lock();
        try {
            if (tableIsDropped) {
                throw new IllegalStateException();
            }
            return signature.get(columnIndex).getJavaClass();
        } finally {
            beingDroppedRWL.readLock().unlock();
        }
    }

    @Override
    public String getName() {
        beingDroppedRWL.readLock().lock();
        try {
            if (tableIsDropped) {
                throw new IllegalStateException();
            }
            return backEndTable.getName();
        } finally {
            beingDroppedRWL.readLock().unlock();
        }
    }

    @Override
    public Storeable get(String key) {
        beingDroppedRWL.readLock().lock();
        try {
            if (tableIsDropped) {
                throw new IllegalStateException();
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
            beingDroppedRWL.readLock().unlock();
        }
    }

    @Override
    public void drop() throws IOException {
        beingDroppedRWL.writeLock().lock();
        try {
            if (tableIsDropped) {
                throw new IllegalStateException();
            }
            backEndTable.clear();
            Files.deleteIfExists(path.resolve("signature.tsv"));
            Files.deleteIfExists(path);
            tableIsDropped = true;
        } finally {
            beingDroppedRWL.writeLock().unlock();
        }
    }
}

