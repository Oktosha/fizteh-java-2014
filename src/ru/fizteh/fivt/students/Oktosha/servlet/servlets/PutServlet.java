package ru.fizteh.fivt.students.Oktosha.servlet.servlets;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.Oktosha.database.storeable.DroppableStructuredTable;
import ru.fizteh.fivt.students.Oktosha.servlet.ServletContext;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;

/**
 * Created by DKolodzey on 22.03.15.
 */
public class PutServlet extends AbstractServlet {
    public PutServlet(ServletContext context) {
        super(context);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        DroppableStructuredTable switchedTable = switchToTransaction(req, resp);
        String key = getParameter("key", req, resp);
        if (key == null) {
            return;
        }
        String value = getParameter("value", req, resp);
        if (value == null) {
            return;
        }
        try {
            Storeable oldValue = switchedTable.put(key,
                    getContext().getTableProvider().deserialize(switchedTable, value));
            resp.getWriter().write(getContext().getTableProvider().serialize(switchedTable, oldValue));
            resp.setStatus(200);
        } catch (ParseException e) {
            resp.sendError(400, "invalid value");
        }
    }
}
