package ru.fizteh.fivt.students.Oktosha.servlet.servlets;

import ru.fizteh.fivt.students.Oktosha.database.storeable.DroppableStructuredTable;
import ru.fizteh.fivt.students.Oktosha.servlet.ServletContext;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by DKolodzey on 23.03.15.
 */
public class SizeServlet extends AbstractServlet {
    public SizeServlet(ServletContext context) {
        super(context);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        DroppableStructuredTable switchedTable = switchToTransaction(req, resp);
        try {
        resp.getWriter().write(switchedTable.size());
        resp.setStatus(200);
        } catch (Throwable e) {
            resp.sendError(500, e.getMessage());
        }
    }
}
