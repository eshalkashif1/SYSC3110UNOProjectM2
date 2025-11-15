import java.util.ArrayList;
import java.util.Collections;
import java.util.ArrayDeque;

/**
 * Class Deck - a deck of UNO cards
 *
 * This class is part of the UnoFlip game application.
 *
 * A "Deck" represents a deck of UNO cards including a draw pile and discard pile.
 * It contains numbered cards (1 to 9) and special action cards (SKIP, ONE, REVERSE)
 * in four colours (RED, BLUE, GREEN, YELLOW), as well as wild cards (WILD, WILDTWO).
 *
 * @author Emma Wong
 * @version 1
 */
public class Deck {
    private ArrayList<Card> cards;   // cards where you draw from
    private ArrayDeque<Card> discards;   // cards that have already been played
    private ArrayList<CardFlipStats> flipstats;

    public static final int DUPLICATE = 2;
    public static final int MAX_COLOURS = 4;
    public static final int MAX_RANK = 9;

    //Colour type
    public static final int ALL_TYPE = 4;

    //Card type
    public static final int NUMBER_TYPE = 0;
    public static final int SKIP_TYPE = 1;
    public static final int DRAW_ONE_TYPE = 2;
    public static final int REVERSE_TYPE = 3;
    public static final int FLIP_TYPE = 4;
    public static final int WILD_TYPE = 5;
    public static final int WILDDRAW_TYPE = 6;
    public static final int NO_RANK = -1;

    /**
     * Deck() contructs a new Deck with a full set of UNO cards.
     * The deck is created and shuffled when constructed.
     */
    public Deck(){
        cards = new ArrayList<>();
        discards = new ArrayDeque<>();
        flipstats = new ArrayList<>();

        createDeck();
        shuffle();
    }

    /**
     * Builds the full deck of cards.
     * Creates:
     * - 2 copies of numbered cards (1 to 9) for each of colour
     * - 2 copies of SKIP, ONE, and REVERSE cards for each colour
     * - 4 WILD cards
     * - 4 WILDTWO cards
     * Total: 104 cards
     */
    private void createDeck(){
        for(int color = 0; color < MAX_COLOURS; color++){
            for(int i = 0; i < DUPLICATE; i++){
                // create cards 1 to 9 (number type)
                for(int rank = 1; rank <= MAX_RANK; rank++){
                    flipstats.add(new CardFlipStats(color, NUMBER_TYPE, rank));
                }

                // add special cards
                flipstats.add(new CardFlipStats(color, SKIP_TYPE, NO_RANK));
                flipstats.add(new CardFlipStats(color, DRAW_ONE_TYPE, NO_RANK));
                flipstats.add(new CardFlipStats(color, REVERSE_TYPE, NO_RANK));
            }
        }

        // add wild cards
        for(int i = 0; i < (DUPLICATE*DUPLICATE); i++){
            flipstats.add(new CardFlipStats(ALL_TYPE, WILD_TYPE, NO_RANK));
            flipstats.add(new CardFlipStats(ALL_TYPE, WILDDRAW_TYPE, NO_RANK));
        }
        Collections.shuffle(flipstats);
        int x = 0;
        // for each colour
        for(int color = 0; color < MAX_COLOURS; color++){
            for(int i = 0; i < DUPLICATE; i++){
                // create cards 1 to 9 (number type)
                for(int rank = 1; rank <= MAX_RANK; rank++){
                    cards.add(new Card(color, NUMBER_TYPE, rank, flipstats.get(x)));
                    x+=1;
                }

                // add special cards
                cards.add(new Card(color, SKIP_TYPE, NO_RANK, flipstats.get(x)));
                x+=1;
                cards.add(new Card(color, DRAW_ONE_TYPE, NO_RANK, flipstats.get(x)));
                x+=1;
                cards.add(new Card(color, REVERSE_TYPE, NO_RANK, flipstats.get(x)));
                x+=1;
            }
        }

        // add wild cards
        for(int i = 0; i < (DUPLICATE*DUPLICATE); i++){
            cards.add(new Card(ALL_TYPE, WILD_TYPE, NO_RANK, flipstats.get(x)));
            x+=1;
            cards.add(new Card(ALL_TYPE, WILDDRAW_TYPE, NO_RANK, flipstats.get(x)));
            x+=1;
        }
    }

    /**
     * Shuffles the deck of cards randomly.
     */
    public void shuffle(){
        Collections.shuffle(cards);
    }

    /**
     * Reshuffles the discard pile back into the draw pile when the draw pile is empty.
     * The top card of the discard pile is saved to remain at the top of the discards after reshuffle.
     * All the other cards from the discard pile are moved to the draw pile and shuffled.
     */
    private void reshuffleDiscards(){
        // save the top card
        Card topCard = discards.pop();

        cards.addAll(discards);
        discards.clear();
        discards.push(topCard);     // place the top card back on discards
        shuffle();
    }

    /**
     * Draw a card from the top of the draw pile.
     * If the draw pile is empty, reshuffle the discard pile to refill
     * the draw pile before drawing a card.
     *
     * @return The card drawn from the top of the draw pile
     */
    public Card drawCard(){
        if(cards.isEmpty()){
            reshuffleDiscards();
        }
        return cards.remove(0);

    }

    /**
     * Adds a card to the top of the discard pile.
     *
     * @param card The card to add to the discard pile
     */
    public void discard(Card card){
        discards.push(card);
    }

    /**
     * Returns the top card of the discard pile without removing it.
     * This represents the last played card that the other players must match,
     *
     * @return The card at the top of the discard pile or null if empty.
     */
    public Card topCard(){
        return discards.peek();
    }
}
