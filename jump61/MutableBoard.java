package jump61;


import static jump61.Color.*;
import java.util.Stack;

/** A Jump61 board state.
 *  @author Brian Liou
 */
class MutableBoard extends Board {

    /** Total combined number of moves by both sides. */
    protected int _moves;
    /** Convenience variable: size of board (squares along one edge). */
    private int _N;
    /** A stack of Board objects. */
    protected Stack<Board> _boards;


    /** An N x N board in initial configuration. */
    MutableBoard(int N) {
        _N = N;
        _squares = new String[N][N];
        for (int i = 0; i < _N; i += 1) {
            _squares[i] = new String[N];
        }
        for (int i = 0; i < _N; i += 1) {
            for (int j = 0; j < _N; j += 1) {
                _squares[i][j] = "--";
            }
        }
        _boards = new Stack<Board>();
        _moves = 1;

    }

    /** A board whose initial contents are copied from BOARD0. Clears the
     *  undo history. */
    MutableBoard(Board board0) {
        _N = board0.size();
        _squares = new String[_N][_N];
        for (int i = 0; i < _N; i += 1) {
            _squares[i] = new String[_N];
        }
        copy(board0);
        _boards = new Stack<Board>();
    }

    @Override
    void clear(int N) {
        for (int i = 0; i < _N; i += 1) {
            for (int j = 0; j < _N; j += 1) {
                _squares[i][j] = "--";
            }
        }
        _N = N;
        _moves = 1;
    }

    /** Copies what is in BOARD into current board. */
    void copy(Board board) {
        _N = board.size();
        for (int r = 1; r < _N + 1; r += 1) {
            for (int c = 1; c < _N + 1; c += 1) {
                Color color = board.color(r, c);
                String sqcol = "";
                if (color == Color.RED) {
                    sqcol = "r";
                    String spotnum = String.valueOf(board.spots(r, c));
                    _squares[r - 1][c - 1] = spotnum + sqcol;
                } else if (color == Color.BLUE) {
                    sqcol = "b";
                    String spotnum = String.valueOf(board.spots(r, c));
                    _squares[r - 1][c - 1] = spotnum + sqcol;
                } else if (color == Color.WHITE) {
                    sqcol = "-";
                    _squares[r - 1][c - 1] = sqcol + sqcol;
                }
            }
        }
        _moves = board.numMoves();
    }

    @Override
    int size() {
        return _N;
    }

    @Override
    int spots(int r, int c) {
        String square = _squares[r - 1][c - 1];
        if (square.charAt(0) == '-') {
            return 0;
        } else {
            return Character.getNumericValue(square.charAt(0));
        }
    }

    @Override
    int spots(int n) {
        int row = row(n);
        int col = col(n);
        String square = _squares[row - 1][col - 1];
        if (square.charAt(0) == '-') {
            return 0;
        } else {
            return Character.getNumericValue(square.charAt(0));
        }
    }

    @Override
    Color color(int r, int c) {
        String square = _squares[r - 1][c - 1];
        switch (square.charAt(1)) {
        case 'r':
            return Color.RED;
        case 'b':
            return Color.BLUE;
        default:
            return Color.WHITE;
        }
    }

    @Override
    Color color(int n) {
        int row = row(n);
        int col = col(n);
        String square = _squares[row - 1][col - 1];
        switch (square.charAt(1)) {
        case 'r':
            return Color.RED;
        case 'b':
            return Color.BLUE;
        default:
            return Color.WHITE;
        }
    }

    @Override
    int numMoves() {
        return _moves;
    }

    @Override
    int numOfColor(Color color) {
        int counter = 0;
        char sqcol = ' ';
        if (color == Color.RED) {
            sqcol = 'r';
        } else if (color == Color.BLUE) {
            sqcol = 'b';
        } else if (color == Color.WHITE) {
            sqcol = '-';
        }
        for (int i = 0; i < _N; i += 1) {
            for (int j = 0; j < _N; j += 1) {
                String square = _squares[i][j];
                if (square.charAt(1) == sqcol) {
                    counter += 1;
                }

            }
        }
        return counter;
    }

    @Override
    void addSpot(Color player, int r, int c) {
        Board snapshot = new MutableBoard(this);
        _boards.push(snapshot);
        String sqcol = "";
        if (player == Color.RED) {
            sqcol = "r";
        } else if (player == Color.BLUE) {
            sqcol = "b";
        }
        String stringspotnum = String.valueOf(spots(r, c) + 1);
        _squares[r - 1][c - 1] = stringspotnum + sqcol;
        int squarenum = sqNum(r, c);
        _moves += 1;
        jump(squarenum);
    }

    @Override
    void addSpot(Color player, int n) {
        addSpot(player, row(n), col(n));
    }

    @Override
    void set(int r, int c, int num, Color player) {
        String sqcol = "";
        if (player == Color.RED) {
            sqcol = "r";
        } else if (player == Color.BLUE) {
            sqcol = "b";
        } else if (player == Color.WHITE) {
            sqcol = "--";
        }
        if (num == 0) {
            _squares[r - 1][c - 1] = "--";
        } else {
            String spotnum = String.valueOf(num);
            _squares[r - 1][c - 1] = spotnum + sqcol;
        }
    }

    @Override
    void set(int n, int num, Color player) {
        String sqcol = "";
        if (player == Color.RED) {
            sqcol = "r";
        } else if (player == Color.BLUE) {
            sqcol = "b";
        } else if (player == Color.WHITE) {
            sqcol = "--";
        }
        int row = row(n);
        int col = col(n);
        if (num == 0) {
            _squares[row - 1][col - 1] = "--";
        } else {
            String spotnum = String.valueOf(num);
            _squares[row - 1][col - 1] = spotnum + sqcol;
        }
    }

    @Override
    void setMoves(int num) {
        assert num > 0;
        _moves = num;
    }

    @Override
    void undo() {
        copy((MutableBoard) _boards.pop());
    }

    /** Do all jumping on this board, assuming that initially, S is the only
     *  square that might be over-full. */
    private void jump(int S) {
        if (getWinner() != null) {
            return;
        }
        Color n = color(S);
        int row = row(S);
        int col = col(S);
        int spotnum = spots(S);
        int neighbornum = neighbors(S);
        if (spotnum > neighbornum) {
            if ((0 < row - 1) && (row - 1 < _N + 1)) {
                jumpDecreasespot(row, col, n);
                jumpAddspot(row - 1, col, n);
            }
            if ((0 < row + 1) && (row + 1 < _N + 1)) {
                jumpDecreasespot(row, col, n);
                jumpAddspot(row + 1, col, n);
            }
            if ((0 < col - 1) && (col - 1 < _N + 1)) {
                jumpDecreasespot(row, col, n);
                jumpAddspot(row, col - 1, n);
            }
            if ((0 < col + 1) && (col + 1 < _N + 1)) {
                jumpDecreasespot(row, col, n);
                jumpAddspot(row, col + 1, n);
            }
            if ((0 < row - 1) && (row - 1 < _N + 1)) {
                jump(sqNum(row - 1, col));
            }
            if ((0 < row + 1) && (row + 1 < _N + 1)) {
                jump(sqNum(row + 1, col));
            }
            if ((0 < col - 1) && (col - 1 < _N + 1)) {
                jump(sqNum(row, col - 1));
            }
            if ((0 < col + 1) && (col + 1 < _N + 1)) {
                jump(sqNum(row, col + 1));
            }
        }
    }

    /** For the jump method, add a spot and convert color to row R and col C
     *  and color N to a neighbor. */
    private void jumpAddspot(int r, int c, Color n) {
        String sqcol = "";
        if (n == Color.RED) {
            sqcol = "r";
        } else {
            sqcol = "b";
        }
        String spotnum = String.valueOf(spots(r, c) + 1);
        _squares[r - 1][c - 1] = spotnum + sqcol;
    }

    /** Decreases your current spot row R and col C by one for the jump method.
     *  and color N. */
    private void jumpDecreasespot(int r, int c, Color n) {
        String sqcol = "";
        if (n == Color.RED) {
            sqcol = "r";
        } else {
            sqcol = "b";
        }
        String mysquare = _squares[r - 1][c - 1];
        int spotnum = Character.getNumericValue(mysquare.charAt(0)) - 1;
        _squares[r - 1][c - 1] = String.valueOf(spotnum) + sqcol;
    }
}


