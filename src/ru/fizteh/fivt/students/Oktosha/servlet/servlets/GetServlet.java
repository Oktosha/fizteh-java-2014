package ru.fizteh.fivt.students.Oktosha.servlet.servlets;

import ru.fizteh.fivt.storage.structured.Storeable;
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

    public GetServlet(ServletContext context) {
        super(context);
    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        DroppableStructuredTable switchedTable = switchToTransaction(req, resp);
        if (switchedTable == null) {
            return;
        }
        try {
            String key = getParameter("key", req, resp);
            if (key == null) {
                return;
            }
            Storeable value = switchedTable.get(key);
            if (value == null) {
                resp.sendError(404, "not found");
                return;
            }
            resp.getWriter().write(getContext().getTableProvider().serialize(switchedTable, value));
            resp.setStatus(200);
        } catch (Throwable e) {
            resp.sendError(500, e.getMessage());
        }
    }
}
