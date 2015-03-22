package ru.fizteh.fivt.students.Oktosha.proxy;

import ru.fizteh.fivt.proxy.LoggingProxyFactory;

import java.io.Writer;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * Created by DKolodzey on 22.03.15.
 */
public class LoggingProxyFactoryImpl implements LoggingProxyFactory {
    @Override
    public Object wrap(Writer writer, Object implementation, Class<?> interfaceClass) {
        InvocationHandler handler = new LoggingInvocationHandler(new XMLInvocationSerializer(), writer, implementation);
        Object wrapped = Proxy.newProxyInstance(implementation.getClass().getClassLoader(),
                                                new Class<?>[]{interfaceClass}, handler);
        return wrapped;
    }
}
