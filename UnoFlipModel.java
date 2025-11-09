import java.util.*;

/**
 * UnoFlipModel - Model component of the MVC architecture for UnoFlip game.
 *
 * This class manages the game state and logic for the UnoFlip game.
 * It maintains all game data including player, deck, turn order, and game rules.
 * The model notifies subscribed views when the game state changes.
 *
 * @author Emma Wong
 * @version 2.0
 */

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

    // Pending steps to advance when "Next Player" is pressed
    private int pendingAdvanceSteps;

    // List of views to notify
    private List<UnoFlipView> views;

    /**
     * Constructs a new UnoFlipModel with default initial state.
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
        this.pendingAdvanceSteps = 1;
    }

    /**
     * Adds a view to be notified of changes to the model.
     * @param view The UnoFlipView to register for updates
     */
    public void addView(UnoFlipView view){
        this.views.add(view);
    }

    /**
     * Removes a view from the notification list.
     * @param view The UnoFlipView to unregister
     */
    public void removeView(UnoFlipView view){
        this.views.remove(view);
    }

    /**
     * Notifies all subscribed views to update that the model state has changed.
     */
    private void notifyViews(){
        for(UnoFlipView view : this.views){
            view.update();
        }
    }

    /**
     * Initializes a new game with specified players.
     * @param playerNames List of player names
     */
    public void initializeGame(List<String> playerNames){

        players.clear(); // clear any previous players

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
        pendingAdvanceSteps = 1;

        notifyViews();
    }

    /**
     * Attempts to play a card from the current player's hand.
     *
     * @param cardIndex The index of the card in the player's hand
     * @param chosenColour The colour chosen if playing a WILD or WILDTWO card, null for regular cards
     * @return true if the card was successfully played, otherwise false if the move is illegal
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

        // Check for win condition
        if (cur.getHand().isEmpty()) {
            gameOver = true;
            winner = cur;
            handleSpecialCard(cardToPlay);

           // int x = players.indexOf(getCurrentPlayer());
            for(Player p: players){
                //if(players.indexOf(p) != x){
                if (p != cur){
                    for(Card c: p.getHand()){
                        cur.increaseScore(calculatePoints(c));
                    }
                }
            }
            notifyViews();
            return true;
        }

        // Handle special cards
        handleSpecialCard(cardToPlay);
        notifyViews();
        return true;
    }

    /**
     * Current player draws a card from the deck and ends their turn.
     */
    public void playerDrawsCard(){
        if (gameOver) return;

        Player cur = getCurrentPlayer();
        Card drawnCard = deck.drawCard();
        cur.addCard(drawnCard);

        pendingAdvanceSteps = 1;
        notifyViews();
    }

    /**
     * Checks if a card can be legally played on the current top card.
     *
     * @param card The card to validate
     * @param chosenColour The colour chosen for wild cards, ignored for regular cards
     * @return true if the card can be legally played, otherwise false
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
     * Handles special card after they are played.
     *
     * @param card The card whose effects should be applied
     */
    private void handleSpecialCard(Card card){
        Card.cardtype type = card.getType();

        pendingAdvanceSteps = 1;
        switch (type){
            case SKIP:
                pendingAdvanceSteps = 2;
                break;

            case REVERSE:
                // Reverse direction
                if (players.size() == 2) {
                    pendingAdvanceSteps = 0;
                }
                direction = -direction;
                break;

            case DRAW_ONE:
                int victim1 = peekNextPlayerIndex(1);
                Player nextPlayer1 = players.get(victim1);
                Card drawnCard = deck.drawCard();
                nextPlayer1.addCard(drawnCard);
                pendingAdvanceSteps = 2;
                break;

            case WILDTWO:
                int victim2 = peekNextPlayerIndex(1);
                Player nextPlayer2 = players.get(victim2);
                nextPlayer2.addCard(deck.drawCard());
                nextPlayer2.addCard(deck.drawCard());
                pendingAdvanceSteps = 2;
                break;

            default:
                break;
        }
    }

    /**
     * Calculates points for a played card.
     *
     * @param card The card to calculate points for
     * @return The point value of the card
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
     * Advances the turn by specified steps in current direction.
     *
     * @param steps The number of player positions to advance
     */
    private void advanceTurn(int steps){
        int n = players.size();
        currentTurn = ((currentTurn + steps * direction) % n + n) % n;
    }

    /**
     * Peeks at next player index without changing the current turn.
     *
     * @param steps The number of positions ahead to look
     * @return The index of the player at that position
     */
    private int peekNextPlayerIndex(int steps) {
        int n = players.size();
        return ((currentTurn + steps * direction) % n + n) % n;
    }

    /**
     * Advances to the next player based on pending advance steps.
     */
    public void advanceToNextPlayer() {
        if (gameOver)
            return;
        advanceTurn(pendingAdvanceSteps);
        pendingAdvanceSteps = 1;
        notifyViews();
    }

    /**
     * Gets the player whose turn it currently is.
     *
     * @return The current Player
     */
    public Player getCurrentPlayer() {
        return players.get(currentTurn);
    }

    /**
     * Gets an unmodified list of all players in the game.
     *
     * @return Unmodifieable list of all Players in turn order
     */
    public List<Player> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    /**
     * Gets the top card of the discard pile.
     *
     * @return The Card at the top of the discard pile or null if empty
     */
    public Card getTopCard() {
        return deck.topCard();
    }

    /**
     * Gets the active colour forced by a wild card.
     *
     * @return The forced colour if a wild effect is active, otherwise null
     */
    public Card.colortype getForcedColour() {
        return forcedColour;
    }

    /**
     * Checks whethehr the game has ended.
     *
     * @return true if the game is over, otherwise false
     */
    public boolean isGameOver() {
        return gameOver;
    }

    /**
     * Gets the player who won the game.
     *
     * @return The winning Player, otherwise null if the game is not over
     */
    public Player getWinner() {
        return winner;
    }

    /**
     * Gets the index of the current player in the player list.
     *
     * @return The current turn index
     */
   // public int getCurrentTurnIndex() {
     //   return currentTurn;
    //}

    /**
     * Gets the current direction of play.
     *
     * @return +1 for clockwise, -1 for counter-clockwise
     */
    public int getDirection() {
        return direction;
    }
}
