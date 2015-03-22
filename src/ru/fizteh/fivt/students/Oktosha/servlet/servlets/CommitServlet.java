package ru.fizteh.fivt.students.Oktosha.servlet.servlets;

import ru.fizteh.fivt.students.Oktosha.database.DiffId;
import ru.fizteh.fivt.students.Oktosha.database.storeable.DroppableStructuredTable;
import ru.fizteh.fivt.students.Oktosha.servlet.ServletContext;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by DKolodzey on 23.03.15.
 */
public class CommitServlet extends AbstractServlet {
    public CommitServlet(ServletContext context) {
        super(context);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        DroppableStructuredTable switchedTable = switchToTransaction(req, resp);
        if (switchedTable == null) {
            return;
        }
        try {
            resp.getWriter().write(String.valueOf(switchedTable.commit()));
            resp.setStatus(200);
            getContext().getDiffManager().freeDiff(new DiffId(req.getParameter("tid")));
        } catch (Throwable e) {
            resp.sendError(500, e.getMessage());
        }
    }
}
