package ru.fizteh.fivt.students.Oktosha.commander;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.StringReader;
import java.io.Writer;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;


public class BatchInterpreterTest {

    private List<Command> commands;
    private Writer errorWriter;
    private Writer outputWriter;
    private Interpreter interpreter;

    @Before
    public void setUp() throws Exception {
        commands = new ArrayList<>();
        commands.add(mock(Command.class));
        commands.add(mock(Command.class));
        when(commands.get(0).getName()).thenReturn("cmd");
        when(commands.get(1).getName()).thenReturn("do");
        doThrow(new CommandExecutionException("do exception message"))
                .when(commands.get(1)).exec(Mockito.any(), Mockito.any());
        errorWriter = mock(Writer.class);
        outputWriter = mock(Writer.class);
        interpreter = new BatchInterpreter();
    }

    @Test
    public void testInterpret() throws Exception {
        verifyNoMoreInteractions(commands.get(1));
        verifyNoMoreInteractions(errorWriter);
        verifyNoMoreInteractions(outputWriter);
        interpreter.interpret(commands, mock(Context.class), new StringReader("cmd"), outputWriter, errorWriter);
        verify(commands.get(0)).exec(Mockito.any(), Mockito.any());
    }

    @Test (expected = CommandExecutionException.class)
    public void testInterpret1() throws Exception {
        interpreter.interpret(commands, mock(Context.class), new StringReader("do"), outputWriter, errorWriter);
    }

    @Test (expected = ParseException.class)
    public void testInterpret2() throws Exception {
        interpreter.interpret(commands, mock(Context.class), new StringReader("unknown"), outputWriter, errorWriter);
    }
}
