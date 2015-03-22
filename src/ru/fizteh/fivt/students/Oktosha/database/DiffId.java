package ru.fizteh.fivt.students.Oktosha.database;

/**
 * Created by DKolodzey on 22.03.15.
 */
public final class DiffId {
    private int id;

    public DiffId(int id) {
        this.id = id;
    }

    public DiffId(String s) {
        if (s == null) {
            throw new NumberFormatException("invalid DiffId");
        }
        id = Integer.parseInt(s);
        if (id < 0 || id > 1000000) {
            throw new NumberFormatException("invalid DiffId");
        }
    }

    public boolean canBeIncreased() {
        return id < 1000000;
    }

    public void increase() {
        ++id;
    }

    @Override
    public String toString() {
        return String.format("%0.5d", id);
    }
}
