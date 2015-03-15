package ru.fizteh.fivt.students.Oktosha.commander;

import java.io.Reader;
import java.io.Writer;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by DKolodzey on 15.03.15.
 * batch (reads from file)
 */
public class BatchInterpreter implements Interpreter {
    @Override
    public void interpret(
            Iterable<Command> commands, Context context, Reader reader, Writer outputWriter, Writer errorWriter)
            throws CommandExecutionException, ParseException {

        Map<String, Command> commandMap = new HashMap<>();
        for (Command cmd : commands) {
            commandMap.put(cmd.getName(), cmd);
        }
        Scanner scanner = new Scanner(reader);
        JobParser jobParser = new JobParserImpl();

        while (scanner.hasNextLine()) {
            List<List<String>> parsedJobs = jobParser.parse(scanner.nextLine());
            for (List<String> job : parsedJobs) {
                if (!commandMap.containsKey(job.get(0))) {
                    throw new ParseException("command " + job.get(0) + " doesn't exist", -1);
                }
            }
            for (List<String> job : parsedJobs) {
                commandMap.get(job.get(0)).exec(context, job);
                if (context.getExitRequested()) {
                    return;
                }
            }
        }
    }
}
