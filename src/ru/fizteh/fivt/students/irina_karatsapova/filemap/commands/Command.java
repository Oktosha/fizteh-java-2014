package ru.fizteh.fivt.students.irina_karatsapova.filemap.commands;

public interface Command {
    void execute(String[] args) throws Exception;
    
    String name();
    
    int minArgs();
    
    int maxArgs();
}

