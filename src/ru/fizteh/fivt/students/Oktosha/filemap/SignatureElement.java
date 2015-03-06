package ru.fizteh.fivt.students.Oktosha.filemap;

/**
 * Created by DKolodzey on 06.03.15.
 */
public enum SignatureElement {
    BOOLEAN(Boolean.class, "boolean"),
    INTEGER(Integer.class, "int"),
    LONG(Long.class, "long"),
    BYTE(Byte.class, "byte"),
    FLOAT(Float.class, "float"),
    DOUBLE(Double.class, "double"),
    STRING(String.class, "String");

    private final Class<?> javaClass;
    private final String name;

    private SignatureElement(Class<?> javaClass, String name) {
        this.javaClass = javaClass;
        this.name = name;
    }
}
