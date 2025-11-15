public class CardFlipStats {
    private int rank;
    private int type;
    private int color;

    public CardFlipStats(int rank, int type, int color){
        this.rank = rank;
        this.type = type;
        this.color = color;
    }

    /**
     * Get the card's color
     * @return The card's color
     */
    public int getColor() {
        return this.color;
    }

    /**
     * Get the card's type
     * @return The card's type
     */
    public int getType(){
        return this.type;
    }

    /**
     * Get the card's rank
     * @return The card's rank
     */
    public int getRank(){
        return this.rank;
    }
}
