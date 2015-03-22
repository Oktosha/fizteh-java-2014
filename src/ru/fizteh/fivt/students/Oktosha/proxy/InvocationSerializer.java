package ru.fizteh.fivt.students.Oktosha.proxy;

import java.lang.reflect.Method;

/**
 * Created by DKolodzey on 22.03.15.
 * interface for serializing method invocation
 */
public interface InvocationSerializer {
    String serialize(Method method, Object[] args, Class<?> implClass,
                     Object returnValue, Throwable thrown, long timestamp);
}
