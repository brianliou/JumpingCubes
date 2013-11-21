
package jump61;

import java.io.Reader;
import java.io.Writer;
import java.io.PrintWriter;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Random;

import static jump61.Color.*;
import static jump61.GameException.error;

/** Main logic for playing (a) game(s) of Jump61.
 *  @author Brian Liou
 */
class Game {

    /** Writer on which to print prompts for input. */
    private final PrintWriter _prompter;
    /** Scanner from current game input.  Initialized to return
     *  newlines as tokens. */
    private final Scanner _inp;
    /** Outlet for responses to the user. */
    private final PrintWriter _out;
    /** Outlet for error responses to the user. */
    private final PrintWriter _err;
    /** The board on which I record all moves. */
    private final Board _board;
    /** A readonly view of _board. */
    private final Board _readonlyBoard;

    /** A pseudo-random number generator used by players as needed. */
    private final Random _random = new Random();

    /** True iff a game is currently in progress. */
    private boolean _playing;

    /** Player One. */
    private Player _playerone;

    /** Player Two. */
    private Player _playertwo;

    /** _quit is 0 when game is over, _quit is negative when game is going. */
    private int _quit;

   /** Used to return a move entered from the console.  Allocated
    *  here to avoid allocations. */
    protected final int[] _move = new int[2];

    /** Name of resource containing help message. */
    private static final String HELP = "jump61/Help.txt";

    /** A new Game that takes command/move input from INPUT, prints
     *  normal output on OUTPUT, prints prompts for input on PROMPTS,
     *  and prints error messages on ERROROUTPUT. The Game now "owns"
     *  INPUT, PROMPTS, OUTPUT, and ERROROUTPUT, and is responsible for
     *  closing them when its play method returns. */
    Game(Reader input, Writer prompts, Writer output, Writer errorOutput) {
        _board = new MutableBoard(Defaults.BOARD_SIZE);
        _readonlyBoard = new ConstantBoard(_board);
        _prompter = new PrintWriter(prompts, true);
        _inp = new Scanner(input);
        _inp.useDelimiter("(?m)\\p{Blank}*$|^\\p{Blank}*|\\p{Blank}+");
        _out = new PrintWriter(output, true);
        _err = new PrintWriter(errorOutput, true);
        _playerone = new HumanPlayer(this, Color.RED);
        _playertwo = new AI(this, Color.BLUE);
        _quit = -1;
        _playing = false;
    }

    /** Returns a readonly view of the game board.  This board remains valid
     *  throughout the session. */
    Board getBoard() {
        return _readonlyBoard;
    }

    /** Play a session of Jump61.  This may include multiple games,
     *  and proceeds until the user exits.  Returns an exit code: 0 is
     *  normal; any positive quantity indicates an error.  */
    int play() {
        _out.println("Welcome to " + Defaults.VERSION);
        _out.flush();
        while (_quit < 0) {
            if (_playing) {
                try {
                    if (_board.whoseMove() == Color.RED) {
                        _playerone.makeMove();
                    } else {
                        _playertwo.makeMove();
                    }
                    checkForWin();
                } catch (GameException e) {
                    reportError(e.getMessage());
                }
            } else if (promptForNext()) {
                readExecuteCommand();
            } else {
                _quit = 0;
            }
        }
        _prompter.close();
        _out.close();
        _err.close();
        return _quit;
    }

    /** Get a move from my input and place its row and column in
     *  MOVE.  Returns true if this is successful, false if game stops
     *  or ends first. */
    boolean getMove(int[] move) {
        while (_playing && _move[0] == 0 && promptForNext()) {
            readExecuteCommand();
        }
        if (_move[0] > 0) {
            move[0] = _move[0];
            move[1] = _move[1];
            _move[0] = 0;
            return true;
        } else {
            return false;
        }
    }

    /** Add a spot to R C, if legal to do so. */
    void makeMove(int r, int c) {
        if (_board.isLegal(_board.whoseMove(), r, c)) {
            _board.addSpot(_board.whoseMove(), r, c);
        } else {
            reportError("move %d %d out of bounds", r, c);
        }
    }

    /** Add a spot to square #N, if legal to do so. */
    void makeMove(int n) {
        if (_board.isLegal(_board.whoseMove(), n)) {
            _board.addSpot(_board.whoseMove(), n);
        } else {
            reportError("move %d out of bounds", n);
        }
    }

    /** Return a random integer in the range [0 .. N), uniformly
     *  distributed.  Requires N > 0. */
    int randInt(int n) {
        return _random.nextInt(n);
    }

    /** Send a message to the user as determined by FORMAT and ARGS, which
     *  are interpreted as for String.format or PrintWriter.printf. */
    void message(String format, Object... args) {
        _out.printf(format, args);
    }

    /** Check whether we are playing and there is an unannounced winner.
     *  If so, announce and stop play. */
    private void checkForWin() {
        if (_playing) {
            if (_board.getWinner() != null) {
                _playing = false;
                announceWinner();
            }
        }
    }

    /** Send announcement of winner to my user output. */
    private void announceWinner() {
        _out.printf("%s wins.%n", _board.getWinner().toCapitalizedString());
    }

    /** Make PLAYER an AI for subsequent moves. */
    private void setAuto(Color player) {
        if (player == Color.RED) {
            _playerone = new AI(this, player);
        } else {
            _playertwo = new AI(this, player);
        }
        _playing = false;
    }

    /** Make PLAYER take manual input from the user for subsequent moves. */
    private void setManual(Color player) {
        if (player == Color.RED) {
            _playerone = new HumanPlayer(this, player);
        } else {
            _playertwo = new HumanPlayer(this, player);
        }
        _playing = false;
    }

    /** Stop any current game and clear the board to its initial
     *  state. */
    private void clear() {
        _playing = false;
        _board.clear(_board.size());
    }

    /** Print the current board using standard board-dump format. */
    private void dump() {
        _out.println(_board);
    }

    /** Print a help message. */
    private void help() {
        Main.printHelpResource(HELP, _out);
    }

    /** Stop any current game and set the move number to N. */
    private void setMoveNumber(int n) {
        _playing = false;
        _board.setMoves(n);
    }

    /** Seed the random-number generator with SEED. */
    private void setSeed(long seed) {
        _random.setSeed(seed);
    }

    /** Place SPOTS spots on square R:C and color the square red or
     *  blue depending on whether COLOR is "r" or "b".  If SPOTS is
     *  0, clears the square, ignoring COLOR.  SPOTS must be less than
     *  the number of neighbors of square R, C. */
    private void setSpots(int r, int c, int spots, String color) {
        Color curcolor;
        if (spots <= _board.neighbors(r, c)) {
            if (color.equals("r") || color.equals("R")) {
                curcolor = Color.RED;
            } else if (color.equals("b") || color.equals("B")) {
                curcolor = Color.BLUE;
            } else {
                curcolor = Color.WHITE;
            }
            _playing = false;
            _board.set(r, c, spots, curcolor);
        } else {
            reportError("Setting %d spots is greater than %d neighbors",
                spots, _board.neighbors(r, c));
        }
    }

    /** Stop any current game and set the board to an empty N x N board
     *  with numMoves() == 0.  */
    private void setSize(int n) {
        _playing = false;
        _board.clear(n);
    }

    /** Begin accepting moves for game.  If the game is won,
     *  immediately print a win message and end the game. */
    private void restartGame() {
        if (_board.getWinner() != null) {
            announceWinner();
            _playing = false;
        } else {
            _playing = true;
        }
    }

    /** Save move R C in _move.  Error if R and C do not indicate an
     *  existing square on the current board. */
    private void saveMove(int r, int c) {
        if (!_board.exists(r, c)) {
            reportError("move %d %d out of bounds", r, c);
        }
        _move[0] = r;
        _move[1] = c;
    }

    /** Returns a color (player) name from _inp: either RED or BLUE.
     *  Throws an exception if not present. */
    private Color readColor() {
        return Color.parseColor(_inp.next("[rR][eE][dD]|[Bb][Ll][Uu][Ee]"));
    }

    /** Read and execute one command.  Leave the input at the start of
     *  a line, if there is more input. */
    private void readExecuteCommand() {
        try {
            if (_inp.hasNextInt()) {
                saveMove(_inp.nextInt(), _inp.nextInt());
            } else if (!_inp.hasNext("\\r?\\n")) {
                String cmnd = _inp.next().toLowerCase();
                executeCommand(cmnd);
            }
        } catch (NoSuchElementException e) {
            reportError("Incorrect command");
        } catch (GameException e) {
            reportError(e.getMessage());
        }
        if (_inp.hasNextLine()) {
            _inp.nextLine();
        }
    }

    /** Gather arguments and execute command CMND.  Throws GameException
     *  on errors. */
    void executeCommand(String cmnd) {
        switch (cmnd) {
        case "\n": case "\r\n":
            return;
        case "#":
            break;
        case "clear":
            clear();
            _playing = false;
            break;
        case "start":
            restartGame();
            break;
        case "quit":
            _quit = 0;
            _playing = false;
            break;
        case "auto":
            setAuto(readColor());
            break;
        case "manual":
            setManual(readColor());
            break;
        case "size":
            setSize(_inp.nextInt());
            break;
        case "move":
            setMoveNumber(_inp.nextInt());
            break;
        case "set":
            setSpots(_inp.nextInt(), _inp.nextInt(),
                _inp.nextInt(), _inp.next("[bBRr]"));
            break;
        case "dump":
            dump();
            break;
        case "seed":
            setSeed(_inp.nextLong());
            break;
        case "help":
            help();
            break;
        default:
            throw error("bad command: '%s'", cmnd);
        }
    }

    /** Print a prompt and wait for input. Returns true iff there is another
     *  token. */
    private boolean promptForNext() {
        if (_playing) {
            _prompter.print(_board.whoseMove().toString());
        }
        _prompter.print("> ");
        _prompter.flush();
        return _inp.hasNext();
    }

    /** Send an error message to the user formed from arguments FORMAT
     *  and ARGS, whose meanings are as for printf. */
    void reportError(String format, Object... args) {
        _err.print("Error: ");
        _err.printf(format, args);
        _err.println();
        _err.flush();
    }

    /** Getter method to return player. */
    protected Player getPlayer() {
        return _playertwo;
    }

}
