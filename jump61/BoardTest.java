
package jump61;

import static jump61.Color.*;
import org.junit.Test;
import static org.junit.Assert.*;

/** Unit tests of Boards.
 *  @author Brian Liou
 */
public class BoardTest {

    private static final String NL = System.getProperty("line.separator");

    @Test
    public void testRow() {
        Board a = new MutableBoard(3);
        int rowone = a.row(4);
        int rowtwo = a.row(7);
        int rowthree = a.row(8);
        assertEquals("Does not work", 2, rowone);
        assertEquals("Does not work", 3, rowtwo);
        assertEquals("Does not work", 3, rowthree);
    }

    @Test
    public void testCol() {
        Board a = new MutableBoard(3);
        int one = a.col(4);
        int two = a.col(7);
        int three = a.col(8);
        assertEquals("Does not work", 2, one);
        assertEquals("Does not work", 2, two);
        assertEquals("Does not work", 3, three);
    }

    @Test
    public void testSqNum() {
        Board a = new MutableBoard(3);
        int one = a.sqNum(1, 1);
        int two = a.sqNum(2, 2);
        int three = a.sqNum(2, 3);
        assertEquals("Does not work", 0, one);
        assertEquals("Does not work", 4, two);
        assertEquals("Does not work", 5, three);
    }

    @Test
    public void testNeighbors() {
        Board a = new MutableBoard(3);
        int one = a.neighbors(1, 1);
        int two = a.neighbors(2, 2);
        int three = a.neighbors(2, 3);
        int four = a.neighbors(3, 3);
        int five = a.neighbors(3, 1);
        assertEquals("Does not work", 2, one);
        assertEquals("Does not work", 4, two);
        assertEquals("Does not work", 3, three);
        assertEquals("Does not work", 2, four);
        assertEquals("Does not work", 2, five);
    }

    @Test
    public void testNeighborsN() {
        Board a = new MutableBoard(3);
        int one = a.neighbors(0);
        int two = a.neighbors(3);
        int three = a.neighbors(4);
        assertEquals("Does not work", 2, one);
        assertEquals("Does not work", 3, two);
        assertEquals("Does not work", 4, three);
    }

    @Test
    public void testSpots() {
        Board a = new MutableBoard(3);
        int numspotone = a.spots(2, 2);
        a.addSpot(Color.RED, 2, 2);
        a.addSpot(Color.RED, 2, 2);
        a.addSpot(Color.RED, 2, 2);
        int numspot = a.spots(2, 2);
        assertEquals("Does not work", 0, numspotone);
        assertEquals("Does not work", 3, numspot);
    }

    @Test
    public void testSpotsN() {
        Board a = new MutableBoard(3);
        int numspotone = a.spots(4);
        a.addSpot(Color.BLUE, 4);
        a.addSpot(Color.BLUE, 4);
        a.addSpot(Color.BLUE, 4);
        int numspot = a.spots(4);
        assertEquals("Does not work", 0, numspotone);
        assertEquals("Does not work", 3, numspot);
    }

    @Test
    public void testSize() {
        Board B = new MutableBoard(5);
        assertEquals("bad length", 5, B.size());
        ConstantBoard C = new ConstantBoard(B);
        assertEquals("bad length", 5, C.size());
        Board D = new MutableBoard(C);
        assertEquals("bad length", 5, C.size());
    }

    @Test
    public void testSet() {
        Board B = new MutableBoard(5);
        B.set(2, 2, 1, RED);
        B.setMoves(1);
        assertEquals("wrong number of spots", 1, B.spots(2, 2));
        assertEquals("wrong color", RED, B.color(2, 2));
        assertEquals("wrong count", 1, B.numOfColor(RED));
        assertEquals("wrong count", 0, B.numOfColor(BLUE));
        assertEquals("wrong count", 24, B.numOfColor(WHITE));
    }

    @Test
    public void testColor() {
        Board B = new MutableBoard(3);
        B.addSpot(RED, 1, 1);
        Color mycolor = B.color(1, 1);
        assertEquals("Does not work", Color.RED, mycolor);
    }

    @Test
    public void testMove() {
        Board B = new MutableBoard(6);
        B.addSpot(RED, 1, 1);
        checkBoard("#1", B, 1, 1, 1, RED);
        B.addSpot(BLUE, 2, 1);
        checkBoard("#2", B, 1, 1, 1, RED, 2, 1, 1, BLUE);
        B.addSpot(RED, 1, 1);
        checkBoard("#3", B, 1, 1, 2, RED, 2, 1, 1, BLUE);
        B.addSpot(BLUE, 2, 1);
        checkBoard("#4", B, 1, 1, 2, RED, 2, 1, 2, BLUE);
        B.addSpot(RED, 1, 1);
        checkBoard("#5", B, 1, 1, 1, RED, 2, 1, 3, RED, 1, 2, 1, RED);
        B.undo();
        checkBoard("#4U", B, 1, 1, 2, RED, 2, 1, 2, BLUE);
        B.undo();
        checkBoard("#3U", B, 1, 1, 2, RED, 2, 1, 1, BLUE);
        B.undo();
        checkBoard("#2U", B, 1, 1, 1, RED, 2, 1, 1, BLUE);
        B.undo();
        checkBoard("#1U", B, 1, 1, 1, RED);
    }

    @Test
    public void testNumofColor() {
        Board b = new MutableBoard(6);
        b.addSpot(RED, 1, 1);
        b.addSpot(RED, 1, 6);
        b.addSpot(RED, 2, 3);
        b.addSpot(RED, 3, 5);
        int count = b.numOfColor(RED);
        assertEquals("Does not work", 4, count);
    }

    @Test
    public void testJump() {
        Board b = new MutableBoard(5);
        b.set(3, 3, 4, Color.BLUE);
        b.addSpot(BLUE, 3, 3);
        int count = b.numOfColor(BLUE);
        assertEquals("Does not work", 5, count);
    }

    private void checkBoard(String msg, Board B, Object... contents) {
        for (int k = 0; k < contents.length; k += 4) {
            String M = String.format("%s at %d %d", msg, contents[k],
                                     contents[k + 1]);
            assertEquals(M, (int) contents[k + 2],
                         B.spots((int) contents[k], (int) contents[k + 1]));
            assertEquals(M, contents[k + 3],
                         B.color((int) contents[k], (int) contents[k + 1]));
        }
        int c;
        c = 0;
        for (int i = B.size() * B.size() - 1; i >= 0; i -= 1) {
            assertTrue("bad white square #" + i,
                       (B.color(i) == WHITE) == (B.spots(i) == 0));
            if (B.color(i) != WHITE) {
                c += 1;
            }
        }
        assertEquals("extra squares filled", contents.length / 4, c);
    }

}
