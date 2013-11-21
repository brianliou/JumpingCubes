package jump61;

/** A Player that gets its moves from manual input.
 *  @author Brian Liou
 */
class HumanPlayer extends Player {

    /** A new player initially playing COLOR taking manual input of
     *  moves from GAME's input source. */
    HumanPlayer(Game game, Color color) {
        super(game, color);
        _pmove = new int[2];
    }

    @Override
    void makeMove() {
        Game game = getGame();
        Board board = getBoard();
        if (game.getMove(_pmove)) {
            if (board.isLegal(getColor(), _pmove[0], _pmove[1])) {
                game.makeMove(_pmove[0], _pmove[1]);
            } else {
                throw new GameException("Invalid move: " + _pmove[0]
                    + " " + _pmove[1]);
            }
        }
    }
}
