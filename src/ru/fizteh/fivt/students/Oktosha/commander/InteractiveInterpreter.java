package ru.fizteh.fivt.students.Oktosha.commander;

import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by DKolodzey on 15.03.15.
 * interprets commands in interactive mode
 */
public class InteractiveInterpreter implements Interpreter {
    private final String prompt = "$ ";

    @Override
    public void interpret(
            Iterable<Command> commands, Context context, Reader reader, Writer outputWriter, Writer errorWriter) {
        Map<String, Command> commandMap = new HashMap<>();

        for (Command cmd : commands) {
            commandMap.put(cmd.getName(), cmd);
        }
        Scanner scanner = new Scanner(reader);
        PrintWriter outputPrintWriter = new PrintWriter(outputWriter, true);
        PrintWriter errorPrintWriter = new PrintWriter(errorWriter, true);
        JobParser jobParser = new JobParserImpl();

        outputPrintWriter.print(prompt);

        while (scanner.hasNextLine()) {
            final boolean[] allCommandsRecognized = {true};
            List<List<String>> parsedJobs;
            try {
                parsedJobs = jobParser.parse(scanner.nextLine());
            } catch (ParseException e) {
                errorPrintWriter.println(e.getMessage());
                continue;
            }
            parsedJobs.stream().filter(job -> !commandMap.containsKey(job.get(0))).forEach(job -> {
                errorPrintWriter.println("command " + job.get(0) + " doesn't exist");
                allCommandsRecognized[0] = false;
            });
            if (allCommandsRecognized[0]) {
                for (List<String> job : parsedJobs) {
                    try {
                        commandMap.get(job.get(0)).exec(context, job);
                        if (context.getExitRequested()) {
                            return;
                        }
                    } catch (CommandExecutionException e) {
                        errorPrintWriter.println(e.getMessage());
                    }
                }
            }
            outputPrintWriter.print(prompt);
        }
    }
}
