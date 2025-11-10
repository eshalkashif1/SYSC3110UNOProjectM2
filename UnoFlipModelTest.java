import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


/**
* Tests the functionality of the UnoFlipModel class.
* This test suite makes sure all public methods in the UnoFlipModel Class
 * return the proper data.
 *
 * @author Matthew Sanii
 * @version 1
*/
class UnoFlipModelTest {

    UnoFlipModel model;
    List<String> players;

    @BeforeEach
    public void setUp(){
        model = new UnoFlipModel();
        players = new ArrayList<>();
        players.add("A");
        players.add("B");
        model.initializeGame(players);
    }

    @AfterEach
    public void tearDown(){
    }

    /**
    * Verifies that the initializeGame method correctly starts a new game.
    */
    @Test
    void initializeGame(){
        List<Player> plays = new ArrayList<>();
        Player a = new Player("A");
        Player b = new Player("B");
        plays.add(a);
        plays.add(b);
        int x = 0;
        for(Player c: model.getPlayers()){
            assertEquals(c.getName(), plays.get(x).getName());
            x+=1;
        }
    }

    /**
    * Verifies that the isRoundOver method correctly states that the current round is over.
    */
    @Test
    void isRoundOver() {
        for(Player p : model.getPlayers()){
            for(int i = 0; i < 7; i++){
                p.removeCard(1);
            }
            p.addCard(new Card(1,5,6));
        }
        assertFalse(model.isRoundOver());
        model.playerDrawsCard();
        model.advanceToNextPlayer();
        assertEquals("B", model.getCurrentPlayer().getName());
        model.playCard(0, Card.colortype.RED);
        model.advanceToNextPlayer();
        assertTrue(model.isRoundOver());
    }

    /**
    * Verifies that the getRoundWinner method correctly states who the winner of the current round is.
    */
    @Test
    void getRoundWinner() {
        for(Player p : model.getPlayers()){
            for(int i = 0; i < 7; i++){
                p.removeCard(1);
            }
            p.addCard(new Card(1,5,6));
        }
        assertNull(model.getRoundWinner());
        assertFalse(model.isRoundOver());
        model.playCard(0, Card.colortype.RED);
        model.advanceToNextPlayer();
        assertTrue(model.isRoundOver());
        assertEquals("A", model.getRoundWinner().getName());
    }

    /**
    * Verifies that the getDirection method correctly states the current turn order direction.
    */
    @Test
    void getDirection() {
        for(Player p : model.getPlayers()){
            for(int i = 0; i < 7; i++){
                p.removeCard(1);
            }
            p.addCard(new Card(1,5,6));
            p.addCard(new Card(0, 3, 4));
        }
        assertEquals(1, model.getDirection());
        model.playCard(0, Card.colortype.RED);
        model.advanceToNextPlayer();
        assertEquals(1, model.getDirection());
        assertEquals("B", model.getCurrentPlayer().getName());
        System.out.println(model.getForcedColour());
        System.out.print(model.getCurrentPlayer().getHandDescription());
        assertTrue(model.playCard(1, Card.colortype.RED));
        assertEquals(-1, model.getDirection());
    }

    /**
    * Verifies that the scoring scheme triggers when the round is over.
    */
    @Test
    void getScore(){
        Player p = model.getCurrentPlayer();
        Player c = model.getPlayers().get(1);
        for(int i = 0; i < 7; i++){
            p.removeCard(1);
            c.removeCard(1);
        }
        p.addCard(new Card(0,5,6));
        c.addCard(new Card(0,0,10));
        assertEquals("B", c.getName());
        assertEquals(0, model.getCurrentPlayer().getScore());
        model.playCard(0, Card.colortype.RED);
        model.advanceToNextPlayer();
        assertEquals(10, model.getCurrentPlayer().getScore());
    }

    /**
    * Verifies that the getWinner and isGameOver methods correctly state that the game is over, and who the winner of the current game is.
    */
    @Test
    void winnerTests() {
        Player p = model.getCurrentPlayer();
        for(int i = 0; i < 7; i++){
            p.removeCard(1);
        }
        p.addCard(new Card(1,5,6));
        Player b = model.getPlayers().get(1);
        for(int j = 0; j < 10; j++){
            b.addCard(new Card(0, 6, 1));
        }
        assertNull(model.getWinner());
        assertFalse(model.isGameOver());
        model.playCard(0, Card.colortype.RED);
        model.advanceToNextPlayer();
        assertTrue(model.isGameOver());
        assertEquals("A", model.getWinner().getName());
    }
}
