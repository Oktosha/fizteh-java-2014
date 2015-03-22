package ru.fizteh.fivt.students.Oktosha.servlet.servlets;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by DKolodzey on 22.03.15.
 */
public class AbstractServlet extends HttpServlet {
    protected String getParameter(String name, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String parameter = req.getParameter(name);
        if (parameter == null) {
            resp.sendError(400, "parameter " + name + " not given");
        }
        return parameter;
    }
}
