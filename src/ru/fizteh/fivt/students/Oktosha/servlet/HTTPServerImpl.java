package ru.fizteh.fivt.students.Oktosha.servlet;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import ru.fizteh.fivt.students.Oktosha.servlet.servlets.BeginServlet;
import ru.fizteh.fivt.students.Oktosha.servlet.servlets.GetServlet;

import javax.servlet.http.HttpServlet;
import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Created by DKolodzey on 22.03.15.
 */
public class HTTPServerImpl implements HTTPServer {

    Server server = null;

    @Override
    public void start(InetSocketAddress address, ServletContext context) throws Exception {
        server = new Server(address);
        ServletContextHandler handler = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        handler.addServlet(new ServletHolder(new GetServlet(context)), "/get");
        handler.addServlet(new ServletHolder(new BeginServlet(context)), "/begin");
        handler.setContextPath("/");
        server.setHandler(handler);
        server.start();
    }

    @Override
    public void stop() throws Exception {
        server.stop();
        server = null;
    }

    @Override
    public boolean isRunning() {
        return server != null;
    }
}
