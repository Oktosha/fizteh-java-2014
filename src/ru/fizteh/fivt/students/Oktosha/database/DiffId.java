package ru.fizteh.fivt.students.Oktosha.database;

/**
 * Created by DKolodzey on 22.03.15.
 */
public final class DiffId {
    private int id;

    public DiffId(int id) {
        this.id = id;
    }

    public boolean canBeIncreased() {
        return id < 1000000;
    }

    public void increase() {
        ++id;
    }
}
