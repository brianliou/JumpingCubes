package jump61;

/** An automated Player for JumpingCubes.
 *  @author Brian Liou
 */
class AI extends Player {

    /** A new player of GAME initially playing COLOR that chooses
     *  moves automatically.
     */
    AI(Game game, Color color) {
        super(game, color);
    }

    @Override
    void makeMove() {
        Game game = getGame();
        Board board = new MutableBoard(getBoard());
        int move = minmax(getColor(), board, 3, 3);
        game.makeMove(move);
        game.message("%s moves %d %d.%n", getColor().toCapitalizedString(),
                board.row(move), board.col(move));
    }

    /** Return the minmax value at a DEPTH and when it equals ORIGDEPTH
     *  for P and board B. */
    private int minmax(Color P, Board b, int depth, int origdepth) {
        int bestvalue;
        int val;
        int bestmove;
        int movenum = b.size() * b.size();
        if (depth < 0) {
            return staticEval(P, b);
        }
        if (P == getColor()) {
            bestvalue = Integer.MIN_VALUE;
            bestmove = 0;
            for (int i = 0; i < movenum; i += 1) {
                if (b.isLegal(P, i)) {
                    b.addSpot(P,  i);
                    val = minmax(P.opposite(), b, depth - 1, origdepth);
                    b.undo();
                    if (val > bestvalue) {
                        bestvalue = val;
                        bestmove = i;
                    }
                }
            }
            if (depth == origdepth || depth == origdepth - 1) {
                if (bestvalue == Integer.MIN_VALUE) {
                    for (int i = 0; i < movenum; i += 1) {
                        if (b.isLegal(P, i)) {
                            return i;
                        }
                    }
                } else {
                    return bestmove;
                }
            }
            return bestvalue;
        } else {
            bestvalue = Integer.MAX_VALUE;
            for (int i = 0; i < movenum; i += 1) {
                if (b.isLegal(P, i)) {
                    b.addSpot(P, i);
                    val = minmax(P.opposite(), b, depth - 1, origdepth);
                    b.undo();
                    if (val < bestvalue) {
                        bestvalue = val;
                    }
                }
            }
            return bestvalue;
        }
    }

    /** Returns heuristic value of board B for player P.
     *  Higher is better for P. */
    protected int staticEval(Color p, Board b) {
        return (b.numOfColor(getColor()) - b.numOfColor(getColor().opposite()));
    }

}


