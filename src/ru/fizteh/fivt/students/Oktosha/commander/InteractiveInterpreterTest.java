package ru.fizteh.fivt.students.Oktosha.commander;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.StringReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

public class InteractiveInterpreterTest {

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
        interpreter = new InteractiveInterpreter();
    }

    @Test
    public void testInterpret() throws Exception {
        verifyNoMoreInteractions(commands.get(1));
        verifyNoMoreInteractions(errorWriter);
        interpreter.interpret(commands, mock(Context.class), new StringReader("cmd"), outputWriter, errorWriter);
        verify(commands.get(0)).exec(Mockito.any(), Mockito.any());
        verify(outputWriter, times(2)).write("$ ");
    }

    @Test
    public void testInterpret1() throws Exception {
        interpreter.interpret(commands, mock(Context.class), new StringReader("do"), outputWriter, errorWriter);
        verify(errorWriter).write("do exception message\n");
        verify(errorWriter, atLeast(1)).flush();
    }

    @Test
    public void testInterpret2() throws Exception {
        interpreter.interpret(commands, mock(Context.class), new StringReader("unknown"), outputWriter, errorWriter);
        verify(errorWriter).write("command unknown doesn't exist\n");
        verify(errorWriter, atLeast(1)).flush();
    }
}
