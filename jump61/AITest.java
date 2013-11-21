package jump61;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import org.junit.Test;
import static org.junit.Assert.*;

/** Unit tests for AI.
 *  @author Brian Liou
 */
public class AITest {

    /** A game. */
    Game game;

    /** A player. */
    AI player;

    /** Initialization for following tests. */
    private void setupGame() {
        Writer output = new OutputStreamWriter(System.out);
        game = new Game(new InputStreamReader(System.in),
                             output, output,
                             new OutputStreamWriter(System.err));
        player = new AI(game, Color.RED);
    }

    @Test
    public void testStaticEval() {
        setupGame();
        Board b = new MutableBoard(5);
        b.set(2, 2, 2, Color.RED);
        b.set(2, 3, 2, Color.RED);
        b.set(2, 4, 2, Color.RED);
        b.set(1, 1, 3, Color.RED);
        b.set(5, 5, 3, Color.BLUE);
        int num = player.staticEval(Color.RED, b);
        assertEquals("does not work", 3, num);
    }
    @Test
    public void testStaticEval2() {
        setupGame();
        Board b = new MutableBoard(5);
        b.set(2, 2, 2, Color.RED);
        b.set(2, 3, 2, Color.RED);
        b.set(2, 4, 2, Color.RED);
        b.set(1, 1, 3, Color.RED);
        b.set(4, 4, 3, Color.BLUE);
        b.set(5, 5, 3, Color.BLUE);
        int num = player.staticEval(Color.RED, b);
        assertEquals("does not work", 2, num);
    }
}

