package ru.fizteh.fivt.students.Oktosha.database.filebackend;

import java.io.IOException;
import java.util.List;

/**
 * Created by DKolodzey on 03.03.15.
 */
public interface MultiFileMap {

    String getName();

    void save() throws IOException;

    void clear();

    String put(String key, String value);

    String get(String key);

    String remove(String key);

    int size();

    List<String> list();
}
