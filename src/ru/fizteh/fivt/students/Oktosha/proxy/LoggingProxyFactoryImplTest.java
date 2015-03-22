package ru.fizteh.fivt.students.Oktosha.proxy;

import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.proxy.LoggingProxyFactory;

import java.io.IOException;
import java.io.StringWriter;

import static org.junit.Assert.*;

public class LoggingProxyFactoryImplTest {

    interface SomeInterface {
        int sumWithX(int y);
        void incX();
        void throwIOException() throws IOException;
    }

    class SomeClass implements SomeInterface {
        int x = 0;

        public int sumWithX(int y) {
            return y;
        }

        public void incX() {
            ++x;
        }

        public void throwIOException() throws IOException {
            throw new IOException("azaza");
        }

        @Override
        public boolean equals(Object o) {
            if (o == null) {
                return false;
            }
            return (hashCode() == o.hashCode());
        }

        @Override
        public int hashCode() {
            return x;
        }

        @Override
        public String toString() {
            return String.valueOf(hashCode());
        }
    }

    SomeInterface wrapped;
    StringWriter writer;

    @Before
    public void setUp() throws Exception {
        writer = new StringWriter();
        LoggingProxyFactory factory = new LoggingProxyFactoryImpl();
        wrapped = (SomeInterface) factory.wrap(writer,
                new SomeClass(), SomeInterface.class);
    }

    @Test
    public void testWrap() throws Exception {
        wrapped.sumWithX(3);
        wrapped.incX();
        wrapped.sumWithX(3);
        assertNotEquals("", writer.toString());
        System.out.println(writer.toString());
    }

    @Test
    public void testEquals() throws Exception {
        assertTrue(new SomeClass().equals(0));
        assertTrue(wrapped.equals(0));
        assertEquals(0, wrapped.hashCode());
        assertEquals("0", wrapped.toString());
        assertEquals("", writer.toString());
    }

    @Test(expected = IOException.class)
    public void testWrapException() throws Exception {
        wrapped.throwIOException();
    }
}
