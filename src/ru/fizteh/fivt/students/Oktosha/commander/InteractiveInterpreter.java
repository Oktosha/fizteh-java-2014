package ru.fizteh.fivt.students.Oktosha.commander;

import java.io.IOException;
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
            Iterable<Command> commands, Context context, Reader reader, Writer outputWriter, Writer errorWriter)
            throws IOException {
        Map<String, Command> commandMap = new HashMap<>();

        for (Command cmd : commands) {
            commandMap.put(cmd.getName(), cmd);
        }
        Scanner scanner = new Scanner(reader);
        JobParser jobParser = new JobParserImpl();

        outputWriter.write(prompt);
        outputWriter.flush();

        while (scanner.hasNextLine()) {
            boolean allCommandsRecognized = true;
            List<List<String>> parsedJobs;
            try {
                parsedJobs = jobParser.parse(scanner.nextLine());
            } catch (ParseException e) {
                errorWriter.write(e.getMessage() + "\n");
                errorWriter.flush();
                continue;
            }
            for (List<String> job : parsedJobs) {
                if (!commandMap.containsKey(job.get(0))) {
                    errorWriter.write("command " + job.get(0) + " doesn't exist\n");
                    errorWriter.flush();
                    allCommandsRecognized = false;
                }
            }

            if (allCommandsRecognized) {
                for (List<String> job : parsedJobs) {
                    try {
                        commandMap.get(job.get(0)).exec(context, job);
                        if (context.isExitRequested()) {
                            return;
                        }
                    } catch (CommandExecutionException e) {
                        errorWriter.write(e.getMessage() + "\n");
                        errorWriter.flush();
                    }
                }
            }
            outputWriter.write(prompt);
            outputWriter.flush();
        }
    }
}
