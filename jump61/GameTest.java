package jump61;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import org.junit.Test;
import static org.junit.Assert.*;

/** Unit tests for Game.
 *  @author Brian Liou
 */
public class GameTest {

    /** Instance variable. */
    Game game;

    /** Initialization for following tests. */
    private void setupGame() {
        Writer output = new OutputStreamWriter(System.out);
        game = new Game(new InputStreamReader(System.in),
                             output, output,
                             new OutputStreamWriter(System.err));
        game.executeCommand("start");
    }

    @Test
    public void testMakeMove() {
        setupGame();
        game.makeMove(1);
        Board b = game.getBoard();
        int numcolor = b.numOfColor(Color.RED);
        assertTrue("does not work", b.color(1) == Color.RED
            && b.spots(1) == 1);
        assertEquals("does not work", 1, numcolor);
    }

    @Test
    public void testMakeMove2() {
        setupGame();
        Board b = game.getBoard();
        int numcolor = b.numOfColor(Color.RED);
        assertTrue("does not work", b.color(1) == Color.WHITE
            && b.spots(1) == 0);
        assertEquals("does not work", 0, numcolor);
    }

    @Test
    public void testClear() {
        setupGame();
        game.makeMove(1);
        game.makeMove(3);
        game.makeMove(5);
        game.executeCommand("clear");
        Board b = game.getBoard();
        assertTrue("does not work", b.color(1) == Color.WHITE
            && b.spots(1) == 0);
        assertTrue("does not work", b.color(3) == Color.WHITE
            && b.spots(3) == 0);
        assertTrue("does not work", b.color(5) == Color.WHITE
            && b.spots(5) == 0);
    }
}
