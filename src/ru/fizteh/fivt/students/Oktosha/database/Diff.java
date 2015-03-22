package ru.fizteh.fivt.students.Oktosha.database;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by DKolodzey on 22.03.15.
 */
public class Diff {
    private final ReadWriteLock rwl = new ReentrantReadWriteLock(true);
    private final Map<String, String> map = new HashMap<>();

    public ReadWriteLock getRwl() {
        return rwl;
    }

    public Map<String, String> getMap() {
        return map;
    }
}
