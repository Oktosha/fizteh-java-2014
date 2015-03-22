package ru.fizteh.fivt.students.Oktosha.commander;

import org.junit.Test;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class JobParserImplTest {

    @Test
    public void testParse() throws Exception {
        String s1 = "  cmd1; cmd2 [\"json string\", null];;   ; cmd3 ; cmd4 arg1 arg2 ";
        List<List<String>> ans1 = new ArrayList<>();
        ans1.add(new ArrayList<>(Arrays.asList("cmd1")));
        ans1.add(new ArrayList<>(Arrays.asList("cmd2", "[\"json string\",", "null]")));
        ans1.add(new ArrayList<>(Arrays.asList("cmd3")));
        ans1.add(new ArrayList<>(Arrays.asList("cmd4", "arg1", "arg2")));
        assertEquals(ans1, new JobParserImpl().parse(s1));
    }

    @Test (expected = ParseException.class)
    public void testParse1() throws Exception {
        new JobParserImpl().parse(" something\"  something");
    }
}
