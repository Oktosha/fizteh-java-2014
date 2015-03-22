package ru.fizteh.fivt.students.Oktosha.database.storeable;

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

    public Class<?> getJavaClass() {
        return javaClass;
    }

    public String getName() {
        return name;
    }

    public static SignatureElement getSignatureElementByName(String name) throws EnumConstantNotPresentException {
        for (SignatureElement signatureElement : SignatureElement.values()) {
            if (signatureElement.getName().equals(name)) {
                return signatureElement;
            }
        }
        throw new EnumConstantNotPresentException(SignatureElement.class, name);
    }

    public static SignatureElement getSignatureElementByClass(Class<?> javaClass)
            throws EnumConstantNotPresentException {
        for (SignatureElement signatureElement : SignatureElement.values()) {
            if (signatureElement.getJavaClass().equals(javaClass)) {
                return signatureElement;
            }
        }
        throw new EnumConstantNotPresentException(SignatureElement.class, javaClass.toString());
    }

}
