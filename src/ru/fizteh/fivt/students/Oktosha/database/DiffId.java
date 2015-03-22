package ru.fizteh.fivt.students.Oktosha.database;

/**
 * Created by DKolodzey on 22.03.15.
 */
public final class DiffId {
    private final int id;

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

    public DiffId increased() {
        return new DiffId(id + 1);
    }

    public int toInt() {
        return id;
    }

    @Override
    public String toString() {
        return String.format("%05d", id);
    }

    @Override
    public boolean equals(Object obj) {
        return (obj != null) && (obj instanceof DiffId) && (((DiffId) obj).toInt() == id);
    }

    @Override
    public int hashCode() {
        return id;
    }
}
