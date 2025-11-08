import java.util.*;

/**
 * UnoFlip.java is replaced by MVC components
 * Game logic -> Model
 * UI -> View
 * Input handling -> controller
 */

// NOTE TO IMPLEMENT:
// Need update() method in UnoFlipView for notifyViews()

public class UnoFlipModel {
    private static final int CARDS_PER_PLAYER = 7;

    // Game state
    private List<Player> players;
    private Deck deck;
    private int currentTurn;
    private int direction; // +1 for clockwise, -1 for counter-clockwise
    private Card.colortype forcedColour; // Active colour after Wild card
    private boolean gameOver;
    private Player winner;

    // List of views to notify
    private List<UnoFlipView> views;

    /**
     * Initializes the game model
     */
    public UnoFlipModel(){
        this.players = new ArrayList<>();
        this.deck = new Deck();
        this.currentTurn = 0;
        this.direction = 1;
        this.forcedColour = null;
        this.gameOver = false;
        this.winner = null;
        this.views = new ArrayList<>();
    }

    /**
     * Adds a view to be notified of changes to the model
     * @param view
     */
    public void addView(UnoFlipView view){
        this.views.add(view);
    }

    /**
     * Removes a view from the notification list
     * @param view
     */
    public void removeView(UnoFlipView view){
        this.views.remove(view);
    }

    /**
     * Notifies all subscribed views to update
     */
    private void notifyViews(){
        for(UnoFlipView view : this.views){
            //view.update();
        }
    }

    /**
     * Initializes the game with players
     * @param playerNames
     */
    public void initializeGame(List<String> playerNames){

        // Add players
        for (String name: playerNames){
            players.add(new Player(name));
        }

        // Add 7 cards to each player's hand
        for (Player p : players) {
            while (p.getHand().size() < CARDS_PER_PLAYER) {
                p.addCard(deck.drawCard());
            }
        }

        // Start discard pile with a NUMBER card
        while (true) {
            Card c = deck.drawCard();
            deck.discard(c);
            if (c.getType() == Card.cardtype.NUMBER) break;
        }

        currentTurn = 0;
        direction = 1;
        forcedColour = null;
        gameOver = false;
        winner = null;

        notifyViews();
    }

    /**
     * Attempts to play a card from the current player's hand
     * @param cardIndex
     * @param chosenColour
     * @return
     */
    public boolean playCard(int cardIndex, Card.colortype chosenColour){
        if (gameOver) return false;

        Player cur = getCurrentPlayer();
        List<Card> hand = cur.getHand();

        if (cardIndex < 0 || cardIndex >= hand.size()) {
            return false;
        }

        Card cardToPlay = hand.get(cardIndex);

        // Validate the move
        if (!isLegal(cardToPlay, chosenColour)) {
            return false;
        }

        // Execute the move
        cur.removeCard(cardIndex + 1);  // removeCard uses 1 based indexing
        deck.discard(cardToPlay);

        // Handle wild cards
        if (cardToPlay.getType() == Card.cardtype.WILD || cardToPlay.getType() == Card.cardtype.WILDTWO) {
            forcedColour = chosenColour;
        } else {
            forcedColour = null;
        }

        // Update score
        int gained = calculatePoints(cardToPlay);
        cur.increaseScore(gained);

        // Check for win condition
        if (cur.getHand().isEmpty()) {
            gameOver = true;
            winner = cur;
            notifyViews();
            return true;
        }

        // Handle special cards
        handleSpecialCard(cardToPlay);

        notifyViews();
        return true;
    }

    /**
     * Current player draws a card from the deck
     * @return
     */
    public void playerDrawsCard(){
        if (gameOver) return;

        Player cur = getCurrentPlayer();
        Card drawnCard = deck.drawCard();
        cur.addCard(drawnCard);

        advanceTurn(1);
        notifyViews();
    }

    /**
     * Checks if a card can bet legally played
     * @return
     */
    private boolean isLegal(Card card, Card.colortype chosenColour){
        Card topCard = deck.topCard();

        // Wild cards are always legal if colour is chosen
        if (card.getType() == Card.cardtype.WILD || card.getType() == Card.cardtype.WILDTWO) {
            return chosenColour != null && chosenColour != Card.colortype.ALL;
        }

        // Determine active colour (from wild or top card)
        Card.colortype activeColour = (forcedColour != null) ? forcedColour : topCard.getColor();

        // Check colour match, number match, and action card match
        boolean colourMatch = card.getColor() == activeColour;
        boolean numberMatch = card.getType() == Card.cardtype.NUMBER && topCard.getType() == Card.cardtype.NUMBER && card.getRank() == topCard.getRank();
        boolean actionMatch = card.getType() != Card.cardtype.NUMBER && card.getType() == topCard.getType();
        return colourMatch || numberMatch || actionMatch;
    }

    /**
     * Handles special card
     * @return
     */
    private void handleSpecialCard(Card card){
        Card.cardtype type = card.getType();

        switch (type){
            case SKIP:
                advanceTurn(2); // Skip next player
                break;

            case REVERSE:
                if (players.size() == 2) {
                    advanceTurn(2); // In 2 player, acts like SKIP
                } else {
                    direction = -direction; // Reverse direction
                    advanceTurn(1);
                }
                break;

            case DRAW_ONE:
                int victim1 = peekNextPlayerIndex(1);
                Player nextPlayer1 = players.get(victim1);
                Card drawnCard = deck.drawCard();
                nextPlayer1.addCard(drawnCard);
                advanceTurn(2); // Skip the player who drew
                break;

            case WILDTWO:
                int victim2 = peekNextPlayerIndex(1);
                Player nextPlayer2 = players.get(victim2);
                nextPlayer2.addCard(deck.drawCard());
                nextPlayer2.addCard(deck.drawCard());
                advanceTurn(2); // Skip the player who drew
                break;

            default:
                advanceTurn(1); // Regular card, normal turn advance
                break;
        }
    }

    /**
     * Calculates points for a played card
     * @return
     */
    private int calculatePoints(Card card){
        switch (card.getType()) {
            case NUMBER:    return Math.max(0, card.getRank()); // rank value
            case SKIP:
            case REVERSE:
                return 20;
            case DRAW_ONE:
                return 10; // if your enum is DRAW_ONE
            case WILD:
                return 40;
            case WILDTWO:
                return 50;
            default:
                return 0;
        }
    }

    /**
     * Advances the turn by specified steps in current direction
     * @return
     */
    private void advanceTurn(int steps){
        int n = players.size();
        currentTurn = ((currentTurn + steps * direction) % n + n) % n;
    }

    /**
     * Peeks at next player index without changing turn
     * @return
     */
    private int peekNextPlayerIndex(int steps) {
        int n = players.size();
        return ((currentTurn + steps * direction) % n + n) % n;
    }


    // Getters for view access
    public Player getCurrentPlayer() {
        return players.get(currentTurn);
    }

    public List<Player> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    public Card getTopCard() {
        return deck.topCard();
    }

    public Card.colortype getForcedColour() {
        return forcedColour;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public Player getWinner() {
        return winner;
    }

    public int getCurrentTurnIndex() {
        return currentTurn;
    }

    public int getDirection() {
        return direction;
    }

}
