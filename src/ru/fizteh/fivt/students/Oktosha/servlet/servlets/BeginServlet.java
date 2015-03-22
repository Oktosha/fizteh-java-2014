package ru.fizteh.fivt.students.Oktosha.servlet.servlets;

import ru.fizteh.fivt.students.Oktosha.database.DiffId;
import ru.fizteh.fivt.students.Oktosha.database.PoolIsFullException;
import ru.fizteh.fivt.students.Oktosha.database.storeable.DroppableStructuredTable;
import ru.fizteh.fivt.students.Oktosha.servlet.ServletContext;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by DKolodzey on 22.03.15.
 */
public class BeginServlet extends AbstractServlet {

    public BeginServlet(ServletContext context) {
        super(context);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String tableName = getParameter("table", req, resp);
        if (tableName == null) {
            return;
        }
        DroppableStructuredTable table = getContext().getTableProvider().getTable(tableName);
        if (table == null) {
            resp.sendError(400, tableName + " not exists");
            return;
        }
        try {
            DiffId diffId = getContext().getDiffManager().createDiff(table);
            resp.getWriter().write(diffId.toString());
            resp.setStatus(200);
        } catch (PoolIsFullException e) {
            resp.sendError(500, e.getMessage());
            return;
        }
    }
}
