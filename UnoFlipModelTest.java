import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    void isGameOver() {
        for(Player p : model.getPlayers()){
            for(int i = 0; i < 7; i++){
                p.removeCard(1);
            }
            p.addCard(new Card(1,5,6));
        }
        assertFalse(model.isGameOver());
        model.playerDrawsCard();
        model.advanceToNextPlayer();
        assertEquals("B", model.getCurrentPlayer().getName());
        model.playCard(0, Card.colortype.RED);
        model.advanceToNextPlayer();
        assertTrue(model.isGameOver());
    }

    @Test
    void getWinner() {
        for(Player p : model.getPlayers()){
            for(int i = 0; i < 7; i++){
                p.removeCard(1);
            }
            p.addCard(new Card(1,5,6));
        }
        assertNull(model.getWinner());
        assertFalse(model.isGameOver());
        model.playCard(0, Card.colortype.RED);
        model.advanceToNextPlayer();
        assertTrue(model.isGameOver());
        assertEquals("A", model.getWinner().getName());
    }

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
}
