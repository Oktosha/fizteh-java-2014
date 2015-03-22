package ru.fizteh.fivt.students.Oktosha.servlet;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.servlet.http.HttpServlet;
import java.net.InetSocketAddress;

/**
 * Created by DKolodzey on 22.03.15.
 */
public class HTTPServerImpl implements HTTPServer {

    Server server;

    @Override
    public void start(InetSocketAddress address, ServletContext context) {
        Server server = new Server(address);
        ServletContextHandler handler = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        handler.addServlet(new ServletHolder(new HttpServlet() {
        }), "/get");
        handler.setContextPath("/");

    }

    @Override
    public void stop() {

    }

    @Override
    public boolean isRunning() {
        return false;
    }
}
