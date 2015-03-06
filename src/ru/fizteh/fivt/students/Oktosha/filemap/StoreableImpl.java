package ru.fizteh.fivt.students.Oktosha.filemap;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;

import java.util.List;

/**
 * Created by DKolodzey on 05.03.15.
 * class which implements
 */
public class StoreableImpl implements Storeable {

    final SignatureElement[] signature;
    final Object[] columns;

    public StoreableImpl(List<SignatureElement> signature) {
        this.signature = (SignatureElement[]) signature.toArray();
        this.columns = new Object[signature.size()];
    }

    @Override
    public void setColumnAt(int columnIndex, Object value) throws ColumnFormatException, IndexOutOfBoundsException {
        if (!signature[columnIndex].getJavaClass().isInstance(value)) {
            throw new ColumnFormatException();
        }
        columns[columnIndex] = value;
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
        if (!signature[columnIndex].getJavaClass().equals(type)) {
            throw new ColumnFormatException("expected " + type.toString() +
                                            "at " + columnIndex +
                                            "got " + signature[columnIndex].getJavaClass());
        }
    }
}
