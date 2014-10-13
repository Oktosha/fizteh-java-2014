package ru.fizteh.fivt.students.isalysultan.FileMap;

import java.io.IOException;
import java.util.Scanner;

public class PocketParser {

    PocketParser() {
        // Disable instantiation to this class.
    }

    public static void batchMode(Table object, String[] argv)
            throws IOException {
        Scanner in = new Scanner(System.in);
        int commandCount = 0;
        Commands newCommand = new Commands();
        StringBuilder allStringBuild = new StringBuilder();
        for (String argument : argv) {
            if (allStringBuild.length() != 0) {
                allStringBuild.append(' ');
            }
            allStringBuild.append(argument);
        }
        String allString = allStringBuild.toString();
        String[] commands = allString.split(";");
        int i = 0;
        Parser newParser = new Parser();
        while (i <= commands.length) {
            String[] command = commands[i].split(" ");
            ++i;
            newParser.myParser(object,command);
        }
    }
}
