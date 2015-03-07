package ru.fizteh.fivt.students.Oktosha.filemap;

import jdk.nashorn.internal.parser.JSONParser;
import org.json.JSONArray;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by DKolodzey on 07.03.15.
 */
public class StructuredTableImpl implements Table {

    Path path;
    List<SignatureElement> signature;
    StringTableWithDiff backEndTable;

    public StructuredTableImpl(Path path) throws IOException {
        if (!path.toFile().exists()) {
            throw new IOException("bd folder does not exist");
        }
        if (!path.toFile().isDirectory()) {
            throw new IOException("bd folder is not a folder");
        }
        signature = readSignature(path.resolve("signature.tsv"));
        backEndTable = new StringTableWithDiffImpl(new MultiFileMapImpl(path));
    }

    public static List<SignatureElement> readSignature(Path signaturePath) throws IOException {
        if (!signaturePath.toFile().exists()) {
            throw new IOException("bd does not contain signature");
        }
        List<SignatureElement> signature;
        try (FileInputStream inputStream = new FileInputStream(signaturePath.toFile())) {
            Scanner scanner = new Scanner(inputStream);
            JSONArray jsonSignatureArray = new JSONArray(scanner.nextLine());
            signature = new ArrayList<SignatureElement>();
            for (int i = 0; i < jsonSignatureArray.length(); ++i) {
                signature.add(SignatureElement.getSignatureElementByName((String) jsonSignatureArray.get(i)));
            }
        } catch (EnumConstantNotPresentException e) {
            throw new IOException("signature is broken" + signaturePath.toString(), e);
        }
        return signature;
    }

    public StructuredTableImpl(Path path, List<SignatureElement> signature) throws IOException{
        if (path.toFile().exists()) {
            throw new IOException("failed to create table; folder already exists");
        }
        Files.createDirectory(path);
        this.path = path;
        this.signature = signature;
        this.backEndTable = new StringTableWithDiffImpl(new MultiFileMapImpl(path));
        writeSignature(path.resolve("signature.tsv"), signature);
    }

    public static void writeSignature(Path signaturePath, List<SignatureElement> signature) throws IOException {
        if (signaturePath.toFile().exists()) {
            throw new IOException("bd already contains signature; can't write");
        }
        try (FileOutputStream outputStream = new FileOutputStream(signaturePath.toFile())) {
            JSONArray jsonSignatureArray = new JSONArray();
            for (int i = 0; i < signature.size(); ++i) {
                jsonSignatureArray.put(signature.get(i).getName());
            }
            PrintWriter writer = new PrintWriter(outputStream);
            writer.print(jsonSignatureArray.toString());
        }
    }

    @Override
    public Storeable put(String key, Storeable value) throws ColumnFormatException {
        return null;
    }

    @Override
    public Storeable remove(String key) {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public List<String> list() {
        return null;
    }

    @Override
    public int commit() throws IOException {
        return 0;
    }

    @Override
    public int rollback() {
        return 0;
    }

    @Override
    public int getNumberOfUncommittedChanges() {
        return 0;
    }

    @Override
    public int getColumnsCount() {
        return 0;
    }

    @Override
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public Storeable get(String key) {
        return null;
    }
}
