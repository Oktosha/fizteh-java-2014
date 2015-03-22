package ru.fizteh.fivt.students.Oktosha.servlet.servlets;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.Oktosha.database.DiffId;
import ru.fizteh.fivt.students.Oktosha.database.storeable.DroppableStructuredTable;
import ru.fizteh.fivt.students.Oktosha.servlet.ServletContext;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by DKolodzey on 22.03.15.
 */
public class GetServlet extends AbstractServlet {
    ServletContext context;

    public GetServlet(ServletContext context) {
        this.context = context;
    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        DiffId tid;
        try {
            tid = new DiffId(req.getParameter("tid"));
        } catch (NumberFormatException e) {
            resp.sendError(400, e.getMessage());
            return;
        }
        String key = req.getParameter("key");
        DroppableStructuredTable table = context.getDiffManager().getTableForDiff(tid);
        if (table == null) {
            resp.sendError(400, "given not existing diffId");
            return;
        }
        try {
            table.setDiff(context.getDiffManager().getDiff(tid));
            Storeable value = table.get(key);
            if (value == null) {
                resp.sendError(404, "not found");
                return;
            }
            resp.getWriter().write(context.getTableProvider().serialize(table, value));
            resp.setStatus(200);
        } catch (Throwable e) {
            resp.sendError(500, e.getMessage());
        }
    }
}
