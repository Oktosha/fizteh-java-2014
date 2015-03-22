package ru.fizteh.fivt.students.Oktosha.servlet.servlets;

import ru.fizteh.fivt.students.Oktosha.database.DiffId;
import ru.fizteh.fivt.students.Oktosha.database.storeable.DroppableStructuredTable;
import ru.fizteh.fivt.students.Oktosha.servlet.ServletContext;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by DKolodzey on 22.03.15.
 */
public class AbstractServlet extends HttpServlet {
    private ServletContext context;

    protected AbstractServlet(ServletContext context) {
        this.context = context;
    }

    protected String getParameter(String name, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String parameter = req.getParameter(name);
        if (parameter == null) {
            resp.sendError(400, "parameter " + name + " not given");
        }
        return parameter;
    }

    protected DroppableStructuredTable switchToTransaction(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        DiffId tid;
        try {
            tid = new DiffId(req.getParameter("tid"));
        } catch (NumberFormatException e) {
            resp.sendError(400, e.getMessage());
            return null;
        }
        DroppableStructuredTable table = getContext().getDiffManager().getTableForDiff(tid);
        if (table == null) {
            resp.sendError(400, "given not existing diffId");
            return null;
        }
        table.setDiff(getContext().getDiffManager().getDiff(tid));
        return table;
    }
    protected ServletContext getContext() {
        return context;
    }
}
