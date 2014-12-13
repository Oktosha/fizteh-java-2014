package ru.fizteh.fivt.students.kuzmichevdima.FileHashMap;

import java.util.Vector;

public class CmdPut implements Command {
    @Override
    public void execute(Vector<String> args, DB db) {
        if (db.currentTable == null) {
            System.out.println("no table");
            return;
        }
        String key = args.get(1);
        String value = args.get(2);
        if (db.currentTable.put(key, value) != null) {
            System.out.println("overwrite");
        } else {
            System.out.println("new");
        }
    }
}
