package ru.fizteh.fivt.students.Oktosha.filemap;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by DKolodzey on 07.03.15.
 */
public class DroppableStructuredTableImpl implements DroppableStructuredTable {

    Path path;
    StringTableWithDiff backEndTable;
    List<SignatureElement> signature;
    StoreableSerializerDeserializer codec;
    private boolean tableIsDropped;


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
        this.tableIsDropped = false;

        List<String> keys = this.backEndTable.list();
        try {
            for (String key : keys) {
                codec.deserialize(signature, backEndTable.get(key));
            }
        } catch (ParseException e){
            throw new IOException(e);
        }
    }

    public DroppableStructuredTableImpl(Path path, List<SignatureElement> signature,
                                        StoreableSerializerDeserializer codec) throws IOException {
        if (path.toFile().exists()) {
            throw new IOException("failed to create table; folder already exists");
        }

        Files.createDirectory(path);
        writeSignature(path.resolve("signature.tsv"), signature);

        this.path = path;
        this.signature = signature;
        this.backEndTable = new StringTableWithDiffImpl(new MultiFileMapImpl(path));
        this.codec = codec;
        this.tableIsDropped = false;
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
        try (FileOutputStream outputStream = new FileOutputStream(signaturePath.toFile())) {
            PrintWriter writer = new PrintWriter(outputStream);
            writeSignature(writer, signature);
        }
    }

    public static void writeSignature(PrintWriter writer, List<SignatureElement> signature) throws IOException {
        for (int i = 0; i < signature.size(); ++i) {
            writer.print(signature.get(i).getName() + " ");
        }
    }

    @Override
    public Storeable put(String key, Storeable value) throws ColumnFormatException {
        if (tableIsDropped)
            throw new IllegalStateException();
        try {
            String encodedValue = backEndTable.put(key, codec.serialize(signature, value));
            if (encodedValue == null)
                return null; //no value on this key before
            return codec.deserialize(signature, encodedValue);
        } catch (ParseException e) {
            throw new IllegalStateException("failed to deserialize held value");
        }
    }

    @Override
    public Storeable remove(String key) {
        if (tableIsDropped)
            throw new IllegalStateException();
        try {
            String encodedValue = backEndTable.remove(key);
            if (encodedValue == null)
                return null; //no value on this key before
            return codec.deserialize(signature, encodedValue);
        } catch (ParseException e) {
            throw new IllegalStateException("failed to deserialize held value");
        }
    }

    @Override
    public int size() {
        if (tableIsDropped)
            throw new IllegalStateException();
        return backEndTable.size();
    }

    @Override
    public List<String> list() {
        if (tableIsDropped)
            throw new IllegalStateException();
        return backEndTable.list();
    }

    @Override
    public int commit() throws IOException {
        if (tableIsDropped)
            throw new IllegalStateException();
        return backEndTable.commit();
    }

    @Override
    public int rollback() {
        if (tableIsDropped)
            throw new IllegalStateException();
        return backEndTable.rollback();
    }

    @Override
    public int getNumberOfUncommittedChanges() {
        if (tableIsDropped)
            throw new IllegalStateException();
        return backEndTable.getNumberOfUncommittedChanges();
    }

    @Override
    public int getColumnsCount() {
        if (tableIsDropped)
            throw new IllegalStateException();
        return signature.size();
    }

    @Override
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        if (tableIsDropped)
            throw new IllegalStateException();
        return signature.get(columnIndex).getJavaClass();
    }

    @Override
    public String getName() {
        if (tableIsDropped)
            throw new IllegalStateException();
        return backEndTable.getName();
    }

    @Override
    public Storeable get(String key) {
        if (tableIsDropped)
            throw new IllegalStateException();
        try {
            String encodedValue = backEndTable.get(key);
            if (encodedValue == null)
                return null; //no value on this key before
            return codec.deserialize(signature, encodedValue);
        } catch (ParseException e) {
            throw new IllegalStateException("failed to deserialize held value");
        }
    }

    @Override
    public void drop() throws IOException {
        if (tableIsDropped)
            throw new IllegalStateException();
        backEndTable.drop();
        tableIsDropped = true;
    };
}
