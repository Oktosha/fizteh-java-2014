package ru.fizteh.fivt.students.Oktosha.database.storeable;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;

import java.util.Arrays;
import java.util.List;

/**
 * Created by DKolodzey on 05.03.15.
 * class which implements
 */
public class StoreableImpl implements Storeable {

    private final List<SignatureElement> signature;
    private final Object[] columns;

    public StoreableImpl(List<SignatureElement> signature) {
        this.signature = signature;
        this.columns = new Object[signature.size()];
    }

    @Override
    public void setColumnAt(int columnIndex, Object value) throws ColumnFormatException, IndexOutOfBoundsException {
        if ((value == null) || (signature.get(columnIndex).getJavaClass().isInstance(value))) {
            columns[columnIndex] = value;
        } else {
            throw new ColumnFormatException();
        }
    }

    @Override
    public Object getColumnAt(int columnIndex) throws IndexOutOfBoundsException {
        return columns[columnIndex];
    }

    @Override
    public Integer getIntAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkTypeAt(Integer.class, columnIndex);
        return (Integer) columns[columnIndex];
    }

    @Override
    public Long getLongAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkTypeAt(Long.class, columnIndex);
        return (Long) columns[columnIndex];
    }

    @Override
    public Byte getByteAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkTypeAt(Byte.class, columnIndex);
        return (Byte) columns[columnIndex];
    }

    @Override
    public Float getFloatAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkTypeAt(Float.class, columnIndex);
        return (Float) columns[columnIndex];
    }

    @Override
    public Double getDoubleAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkTypeAt(Double.class, columnIndex);
        return (Double) columns[columnIndex];
    }

    @Override
    public Boolean getBooleanAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkTypeAt(Boolean.class, columnIndex);
        return (Boolean) columns[columnIndex];
    }

    @Override
    public String getStringAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkTypeAt(String.class, columnIndex);
        return (String) columns[columnIndex];
    }

    private void checkTypeAt(Class<?> type, int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (!signature.get(columnIndex).getJavaClass().equals(type)) {
            throw new ColumnFormatException("expected " + type.toString()
                                            + "at " + columnIndex
                                            + "got " + signature.get(columnIndex).getJavaClass());
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof StoreableImpl) {
            boolean signaturesAreEqual = this.signature.equals(((StoreableImpl) other).signature);
            boolean valuesAreEqual = Arrays.equals(this.columns, ((StoreableImpl) other).columns);
            return signaturesAreEqual && valuesAreEqual;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.signature.hashCode() + Arrays.hashCode(this.columns);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(this.getClass().getSimpleName());
        builder.append("[");
        for (int i = 0; i  < columns.length; ++i) {
            if (columns[i] != null) {
                builder.append(columns[i].toString());
            }
            if (i != columns.length - 1) {
                builder.append(",");
            }
        }
        builder.append("]");
        return builder.toString();
    }
}
