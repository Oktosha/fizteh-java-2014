package ru.fizteh.fivt.students.vladislav_korzun.JUnit;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;
import ru.fizteh.fivt.students.vladislav_korzun.JUnit.Interpreter.Command;
import ru.fizteh.fivt.students.vladislav_korzun.JUnit.Interpreter.Interpreter;

import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;

public class JUnit {
    public static void main(String[] args) throws Exception {
        String dbDirPath = System.getProperty("fizteh.db.dir");
        if (dbDirPath == null) {
            System.err.println("You must specify fizteh.db.dir via -Ddb.file JVM parameter");
            System.exit(1);
        }
        try {
            TableProviderFactory factory = new MyTableProviderFactory();
            run(new TableConnector(factory.create(dbDirPath)), args);
        } catch (DataBaseException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
        }

    }

    private static void run(Object state, String[] args) throws Exception {
        Interpreter dbInterpeter = new Interpreter(state, new Command[] {
                new Command("put", 2, new BiConsumer<Object, String[]>() {
                    @Override
                    public void accept(Object state, String[] args) {
                        Table link = ((TableConnector) state).getUsedTable();
                        if (link != null) {
                            String value = link.put(args[0], args[1]);
                            if (value != null) {
                                System.out.println("overwrite");
                                System.out.println(value);
                            } else {
                                System.out.println("new");
                            }
                        } else {
                            System.out.println("no table");
                        }
                    }
                }),
                new Command("get", 1, new BiConsumer<Object, String[]>() {
                    @Override
                    public void accept(Object state, String[] args) {
                        Table link = ((TableConnector) state).getUsedTable();
                        if (link != null) {
                            String value = link.get(args[0]);
                            if (value != null) {
                                System.out.println("found");
                                System.out.println(value);
                            } else {
                                System.out.println("not found");
                            }
                        } else {
                            System.out.println("no table");
                        }
                    }
                }),
                new Command("remove", 1, new BiConsumer<Object, String[]>() {
                    @Override
                    public void accept(Object state, String[] args) {
                        Table link = ((TableConnector) state).getUsedTable();
                        if (link != null) {
                            String value = link.remove(args[0]);
                            if (value != null) {
                                System.out.println("removed");
                                System.out.println(value);
                            } else {
                                System.out.println("not found");
                            }
                        } else {
                            System.out.println("no table");
                        }
                    }
                }),
                new Command("list", 0, new BiConsumer<Object, String[]>() {
                    @Override
                    public void accept(Object state, String[] args) {
                        Table link = ((TableConnector) state).getUsedTable();
                        if (link != null) {
                            List<String> value = link.list();
                            if (value != null) {
                                String joined = new String();
                                for (String str : value) {
                                    joined += str + ", "; 
                                }
                                System.out.println(joined);
                            } else {
                                System.out.println("");
                            }
                        } else {
                            System.out.println("no table");
                        }
                    }
                }),
                new Command("size", 0, new BiConsumer<Object, String[]>() {
                    @Override
                    public void accept(Object state, String[] args) {
                        Table link = ((TableConnector) state).getUsedTable();
                        if (link != null) {
                            int value = link.size();
                            System.out.println(value);
                        } else {
                            System.out.println("no table");
                        }
                    }
                }),
                new Command("commit", 0, new BiConsumer<Object, String[]>() {
                    @Override
                    public void accept(Object state, String[] args) {
                        Table link = ((TableConnector) state).getUsedTable();
                        if (link != null) {
                            try {
                                link.commit();
                                System.out.println("committed");
                            } catch (DataBaseException e) {
                                System.err.println(e.getMessage());
                            }
                        } else {
                            System.out.println("no table");
                        }
                    }
                }),
                new Command("rollback", 0, new BiConsumer<Object, String[]>() {
                    @Override
                    public void accept(Object state, String[] args) {
                        Table link = ((TableConnector) state).getUsedTable();
                        if (link != null) {
                            int rollbacked = link.rollback();
                            System.out.println(rollbacked + "changes have rollbacked");
                        } else {
                            System.out.println("no table");
                        }
                    }
                }),
                new Command("create", 1, new BiConsumer<Object, String[]>() {
                    @Override
                    public void accept(Object state, String[] args) {
                        TableProvider manager = ((TableConnector) state).getManager();
                        try {
                            if (manager.createTable(args[0]) != null) {
                                System.out.println("created");
                            } else {
                                System.out.println(args[0] + "already exist");
                            }
                        } catch (DataBaseException e) {
                            e.getMessage();
                        }
                    }
                }),
                new Command("use", 1, new BiConsumer<Object, String[]>() {
                    @Override
                    public void accept(Object state, String[] args) {
                        TableConnector connector = ((TableConnector) state);
                        TableProvider manager = connector.getManager();
                        try {
                            Table newTable = manager.getTable(args[0]);
                            MyTable usedTable = (MyTable) connector.getUsedTable();
                            if (newTable != null) {
                                if (usedTable != null && (usedTable.unsavedChanges() > 0)) {
                                    System.out.println(usedTable.unsavedChanges()
                                            + " unsaved changes");
                                } else {
                                   connector.setUsedTable(newTable);
                                   System.out.println("using " + args[0]);
                                }
                            } else {
                                System.out.println(args[0] + "not exist");
                            }
                        } catch (DataBaseException e) {
                            e.getMessage();
                        }
                    }
                }),
                new Command("drop", 1, new BiConsumer<Object, String[]>() {
                    @Override
                    public void accept(Object state, String[] args) {
                        TableConnector connector = ((TableConnector) state);
                        TableProvider manager = connector.getManager();
                        Table usedTable = connector.getUsedTable();
                        if (usedTable != null && usedTable.getName().equals(args[0])) { 
                            connector.setUsedTable(null);
                        }
                        try {
                            manager.removeTable(args[0]);
                            System.out.println("dropped");
                        } catch (IllegalStateException e) {
                            System.out.println(args[0] + " not exist");
                        }
                    }
                }),
                new Command("show", 1, new BiConsumer<Object, String[]>() {
                    @Override
                    public void accept(Object state, String[] args) {
                        
                        if (args[0].equals("tables")) {
                            TableConnector connector = ((TableConnector) state);
                            MyTableProvider manager = (MyTableProvider) connector.getManager();
                            Set<String> tableNames = manager.list();
                            System.out.println("table_name row_count");
                            for (String name : tableNames) {
                                try {
                                   Table curTable = manager.getTable(name);
                                    System.out.println(curTable.getName() + " " + curTable.size());
                                } catch (DataBaseException e) {
                                    e.getMessage();
                                }
                            }
                        } else {
                           System.err.println("Command 'show " + args[0] + "' doesn't supported");
                        }
                        
                    }
                }),
                new Command("exit", 0, new BiConsumer<Object, String[]>() {
                    @Override
                    public void accept(Object state, String[] args) {
                        System.exit(0);
                    }
                }),
        });
        dbInterpeter.run(args);
    }
}
