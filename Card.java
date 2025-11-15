/**
 * Class Card - one of the Cards in the UnoFlip Deck.
 *
 * This class is part of the UnoFlip game application.
 *
 * A "Card" represents a single card in the game. The game keeps
 * track of a total of 116 as per the guidelines, each with a color,
 * type, and rank.
 *
 * @author Matthew Sanii
 * @version 1
 */

public class Card {

    public enum colortype{ //The four main card colors, plus ALL to represent the WILD cards
        RED,
        BLUE,
        GREEN,
        YELLOW,
        ALL
    }

    public enum flipcolor{
        PINK,
        TEAL,
        ORANGE,
        PURPLE,
        ALL
    }

    public enum cardtype{ //All the different card types in UnoFlip
        NUMBER,
        SKIP,
        DRAW_ONE,
        REVERSE,
        FLIP,
        WILD,
        WILDTWO
    }

    public enum flipType {
        NUMBER,
        SKIP_ALL,
        DRAW_FIVE,
        REVERSE,
        FLIP,
        WILD,
        WILD_DRAW
    }

    private colortype col;
    private flipcolor flcol;
    private flipType fltype;
    private cardtype type;
    private int rank;
    private int flipRank;

    /**
     * Create a Card with a color, type, and rank.
     * @param color The Color of the card
     * @param type The type of card it is (wild, draw, skip)
     * @param rank The numerical rank of the card.
     */
    public Card(int color, int type, int rank, CardFlipStats flipInfo){
        if(type == 5 || type == 6){ //If card type is one of the two Wild cards, set 'color' to ALL
            this.type = cardtype.values()[type];
            this.col = colortype.values()[4];
        }
        else{
            this.type = cardtype.values()[type];
            this.col = colortype.values()[color];
            this.rank = rank;
        }
        this.fltype = flipType.values()[flipInfo.getType()];
        this.flcol = flipcolor.values()[flipInfo.getColor()];
        this.flipRank = flipInfo.getRank();
    }

    /**
     * Get the card's color
     * @return The card's color
     */
    public colortype getColor() {
        return this.col;
    }

    /**
     * Get the card's type
     * @return The card's type
     */
    public cardtype getType(){
        return this.type;
    }

    /**
     * Get the card's rank
     * @return The card's rank
     */
    public int getFlipRank(){
        return this.flipRank;
    }

    public flipcolor getFlipColor() {
        return this.flcol;
    }

    /**
     * Get the card's type
     * @return The card's type
     */
    public flipType getFlipType(){
        return this.fltype;
    }

    /**
     * Get the card's rank
     * @return The card's rank
     */
    public int getRank(){
        return this.rank;
    }

    /**
     * Return a description of the card, stating color, type, and rank where applicable.
     * @return The description of the card as a String
     */
    public String getDescription(int flipped){
        // Handle wilds first
        if(flipped == 0){
            if (this.type == cardtype.WILD) {
                return "WILD";
            }
            if (this.type == cardtype.WILDTWO) {
                return "WILD DRAW TWO";
            }

            // Handle action cards (SKIP, DRAW_ONE, REVERSE, FLIP)
            if (this.type != cardtype.NUMBER) {
                return this.col + " " + this.type;
            }

            // Handle regular number cards (e.g., RED 5)
            return this.col + " " + this.rank;
    }else{
            if (this.fltype == flipType.WILD) {
                return "WILD";
            }
            if (this.fltype == flipType.WILD_DRAW) {
                return "WILD STACK DRAW";
            }

            // Handle action cards (SKIP, DRAW_ONE, REVERSE, FLIP)
            if (this.fltype != flipType.NUMBER) {
                return this.flcol + " " + this.fltype;
            }

            // Handle regular number cards (e.g., RED 5)
            return this.flcol + " " + this.flipRank;
        }
    }
}
