import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Class Player - an individual in the UnoFlip game.
 *
 * This class is part of the UnoFlip game application.
 *
 * A "Player" represents a person in the game. The game keeps
 * track of 2-4 players as per the guidelines, each with a name,
 * score, and hand of cards.
 *
 * @author Eshal Kashif
 * @version 1
 */
public class Player {

    private String name;
    private int score;
    private List<Card> hand;

    /**
     * Create a Player with a name. Initialize their score to 0
     * and create an empty ArrayList for their hand.
     * @param name The player's name
     */
    public Player(String name){
        // Ensure the player has a valid name
        if(name==null){
            throw new IllegalArgumentException("Player name cannot be empty.");
        }
        this.name = name;
        score = 0;
        hand = new ArrayList<Card>();
    }

    /**
     * Get the player's name
     * @return The player's name
     */
    public String getName(){return name;}

    /**
     * Get the player's current score
     * @return The player's score
     */
    public int getScore(){return score;}

    /**
     * Increment/update the player's score by a certain amount
     * @param add The amount to increment the player's score by
     */
    public void increaseScore(int add){
        if(add < 0){throw new IllegalArgumentException("Score increment must be >= 0");}
        score += add;
    }

    /**
     * Get an unmodifiable list of the player's current hand/cards
     * @return The list of cards the player has
     */
    public List<Card> getHand(){
        return Collections.unmodifiableList(hand);
    }

    /**
     * Add a card to the player's hand
     * @param card The card to be added
     */
    public void addCard(Card card){
        hand.add(card);
    }

    /**
     * Remove a card from the player's hand
     * @param i The index of the card to be removed in the ArrayList
     * @return The card that was removed
     */
    public Card removeCard(int i){
        return hand.remove(i-1);
    }

    /**
     * Print a description of all the cards in a player's hand
     * @return The description as a String
     */
    public String getHandDescription(int flipped){
        StringBuilder sb = new StringBuilder();
        sb.append(name).append("'s cards: ").append("\n");
        if(hand.isEmpty()) return name + "'s hand is empty";
        for(int i=1; i<=hand.size(); i++){
            sb.append(i).append(": ").append(hand.get(i-1).getDescription(flipped)).append("\n");
        }
        return sb.toString();
    }
}
