package ru.fizteh.fivt.students.Oktosha.proxy;

import java.io.Writer;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by DKolodzey on 22.03.15.
 */
public class LoggingInvocationHandler implements InvocationHandler {

    private final InvocationSerializer serializer;
    private final Writer writer;
    private final Object object;

    public LoggingInvocationHandler(InvocationSerializer serializer, Writer writer, Object object) {
        this.serializer = serializer;
        this.writer = writer;
        this.object = object;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.equals(Object.class.getMethod("equals", Object.class))) {
            return object.equals(args[0]);
        }
        if (method.equals(Object.class.getMethod("hashCode"))) {
            return object.hashCode();
        }
        if (method.equals(Object.class.getMethod("toString"))) {
            return object.toString();
        }
        long timestamp = System.currentTimeMillis();
        Throwable thrown = null;
        Object returnValue = null;
        try {
            returnValue = method.invoke(object, args);
            return returnValue;
        } catch (InvocationTargetException e) {
            thrown = e.getCause();
            throw thrown;
        } finally {
            String log = serializer.serialize(method, args, object.getClass(), returnValue, thrown, timestamp);
            writer.write(log);
            writer.flush();
        }
    }
}

