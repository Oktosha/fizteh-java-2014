package ru.fizteh.fivt.students.Oktosha.commander;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by DKolodzey on 15.03.15.
 * class which parses given string to tokens
 */
public class JobParserImpl implements JobParser {
    @Override
    public List<List<String>> parse(String line) throws ParseException {
        return new JobParsingAutomaton().parse(line);
    }
}

class JobParsingAutomaton {

    private static enum Symbol {
        WHITESPACE(0),
        SEMICOLON(1),
        ALPHA(2),
        QUOTE(3),
        EOL(4);

        int ord;
        private Symbol(int ord) {
            this.ord = ord;
        }
        public int getOrd() {
            return ord;
        }
        public static Symbol forChar(char ch) {
            if (Character.isWhitespace(ch)) {
                return WHITESPACE;
            } else if (ch == ';') {
                return SEMICOLON;
            } else if (ch == '"') {
                return QUOTE;
            } else {
                return ALPHA;
            }
        }
    }

    private static enum State {
        EMPTY(0),
        NOT_EMPTY(1),
        QUOTED(2),
        ACCEPTED(3),
        DECLINED(4);

        private final int ord;
        private State(int ord) {
            this.ord = ord;
        }
        public int getOrd() {
            return ord;
        }
    }

    private static enum Action {
        ADD_CHAR,
        PUSH_WORD,
        PUSH_LIST,
        PUSH_BOTH,
        SAVE_QUOTE_ERROR
    }

    private static final State[][] NEXT_STATE = {
           /* WHITESPACE      SEMICOLON       ALPHA            QUOTE            EOL          */
            {State.EMPTY,    State.EMPTY,    State.NOT_EMPTY, State.QUOTED,    State.ACCEPTED}, /* EMPTY     */
            {State.EMPTY,    State.EMPTY,    State.NOT_EMPTY, State.QUOTED,    State.ACCEPTED}, /* NOT_EMPTY */
            {State.QUOTED,   State.QUOTED,   State.QUOTED,    State.NOT_EMPTY, State.DECLINED}, /* QUOTED    */
            {State.DECLINED, State.DECLINED, State.DECLINED,  State.DECLINED,  State.DECLINED}, /* ACCEPTED  */
            {State.DECLINED, State.DECLINED, State.DECLINED,  State.DECLINED,  State.DECLINED}  /* DECLINED  */
    };

    private static final Action[][] ACTION = {
       /* WHITESPACE        SEMICOLON         ALPHA            QUOTE            EOL            */
        {null,             Action.PUSH_LIST, Action.ADD_CHAR, Action.ADD_CHAR, Action.PUSH_LIST},        /* EMPTY     */
        {Action.PUSH_WORD, Action.PUSH_BOTH, Action.ADD_CHAR, Action.ADD_CHAR, Action.PUSH_BOTH},        /* NOT_EMPTY */
        {Action.ADD_CHAR,  Action.ADD_CHAR,  Action.ADD_CHAR, Action.ADD_CHAR, Action.SAVE_QUOTE_ERROR}, /* QUOTED    */
        {null,             null,             null,            null,            null},                    /* ACCEPTED  */
        {null,             null,             null,            null,            null},                    /* DECLINED  */
    };

    private final List<List<String>> parsedJobs = new ArrayList<>();
    private List<String> currentJob = new ArrayList<>();
    private StringBuilder currentWordBuilder = new StringBuilder();
    private String errorMessage;

    private void performAction(Action action, char ch) {
        if (action == null) {
            return;
        }
        switch (action) {
            case ADD_CHAR:
                currentWordBuilder.append(ch);
                break;
            case PUSH_WORD:
                currentJob.add(currentWordBuilder.toString());
                currentWordBuilder = new StringBuilder();
                break;
            case PUSH_LIST:
                if (currentJob.isEmpty()) {
                    break;
                }
                parsedJobs.add(currentJob);
                currentJob = new ArrayList<>();
                break;
            case PUSH_BOTH:
                performAction(Action.PUSH_WORD, ch);
                performAction(Action.PUSH_LIST, ch);
                break;
            case SAVE_QUOTE_ERROR:
                errorMessage = "expected closing quote";
                break;
            default:
                throw new EnumConstantNotPresentException(Action.class, action.toString());
        }
    }

    public List<List<String>> parse(String line) throws ParseException {
        State currentState = State.EMPTY;
        for (int i = 0; i < line.length(); ++i) {
            char ch = line.charAt(i);
            performAction(ACTION[currentState.getOrd()][Symbol.forChar(ch).getOrd()], line.charAt(i));
            currentState = NEXT_STATE[currentState.getOrd()][Symbol.forChar(ch).getOrd()];
        }
        performAction(ACTION[currentState.getOrd()][Symbol.EOL.getOrd()], (char) 0);
        currentState = NEXT_STATE[currentState.getOrd()][Symbol.EOL.getOrd()];

        if (currentState.equals(State.ACCEPTED)) {
            return parsedJobs;
        } else if (currentState.equals(State.DECLINED)) {
            throw new ParseException(errorMessage, line.length());
        } else {
            throw new IllegalStateException();
        }
    }
}
