package edu.kit.informatik.torus.io;

import edu.kit.informatik.torus.model.BoardType;
import edu.kit.informatik.torus.model.Game;
import edu.kit.informatik.torus.model.RuleException;
import edu.kit.informatik.torus.model.Token;
import edu.kit.informatik.torus.model.Position;

import java.util.Set;

import static edu.kit.informatik.torus.model.Game.NUMBER_OF_TOKENS;

public enum Command {
    START("start") {
        @Override
        String execute(String argument, Game game) throws ParseException, RuleException {
            BoardType boardType = parseBoardType(argument);
            game.reset();
            game.setBoardType(boardType);
            return OK;
        }
    },
    SELECT("select") {
        @Override
        String execute(String argument, Game game) throws ParseException, RuleException {
            game.take(parseToken(argument));
            return OK;
        }
    },
    PLACE("place") {
        @Override
        String execute(String argument, Game game) throws ParseException, RuleException {
            if (game.place(parsePosition(argument))) {
                return "P" + game.getPlayerWhoPlaced() + " wins\n" + game.getCounter();
            } else if (game.outOfTokens()) {
                return "draw";
            } else {
                return OK;
            }
        }
    },
    BAG("bag") {
        @Override
        String execute(String argument, Game game) throws ParseException, RuleException {
            checkNoArgument(argument);
            return game.getAvailable();
        }
    },
    PRINT_ROW("rowprint") {
        @Override
        String execute(String argument, Game game) throws ParseException, RuleException {
            return game.rowToString(parseInt(argument));
        }
    },
    PRINT_COL("colprint") {
        @Override
        String execute(String argument, Game game) throws ParseException, RuleException {
            return game.colToString(parseInt(argument));
        }
    };

    public static Command matchCommand(String command) throws ParseException {
        for (Command cmd: Command.values()) {
            if (cmd.toString().equals(command)) {
                return cmd;
            }
        }
        throw new ParseException("no command of this name found");
    }

    public static final String COORDINATE_SEPERATOR = ";";
    abstract String execute(String argument, Game game) throws ParseException, RuleException;

    public static final String OK = "OK";

    private String string;

    Command(String string) {
        this.string = string;
    }

    private static void checkNoArgument(String argument) throws ParseException {
        if (!argument.isEmpty()) {
            throw new ParseException("unexpected argument");
        }
    }

    @Override
    public String toString() {
        return string;
    }

    private static int parseInt(String argument) throws ParseException {
        try {
            return Integer.parseInt(argument);
        } catch (NumberFormatException e) {
            throw new ParseException("could not parse number describing token");
        }
    }

    private static BoardType parseBoardType(String argument) throws ParseException {
        if (argument.equals("standard")) {
            return BoardType.STANDARD;
        } else if (argument.equals("torus")) {
            return BoardType.TORUS;
        } else {
            throw new ParseException("unknown board type");
        }
    }

    private static Token parseToken(String argument) throws ParseException {
        int number;
        try {
            number = Integer.parseInt(argument);
        } catch (NumberFormatException e) {
            throw new ParseException("could not parse number describing token");
        }
        if (number < 0 || number > NUMBER_OF_TOKENS) {
            throw new ParseException("invalid number describing token");
        }
        return Token.numberToToken(number);
    }

    private static Position parsePosition(String argument) throws ParseException {
        String[] split = argument.split(COORDINATE_SEPERATOR, -1);
        if (split.length != 2) {
            throw new ParseException("not exactly 2 coordinates specified");
        }
        int row;
        int col;
        try {
            row = Integer.parseInt(split[0]);
            col = Integer.parseInt(split[1]);
        } catch (NumberFormatException e) {
            throw new ParseException("could not parse coordinates");
        }
        return new Position(row, col);
    }
}