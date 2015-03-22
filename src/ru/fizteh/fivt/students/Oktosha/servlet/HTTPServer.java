package ru.fizteh.fivt.students.Oktosha.servlet;

import java.net.InetSocketAddress;

/**
 * Created by DKolodzey on 22.03.15.
 */
public interface HTTPServer {
    void start(InetSocketAddress address, ServletContext context);
    void stop();
    boolean isRunning();
}
