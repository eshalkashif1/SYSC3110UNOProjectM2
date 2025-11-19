import java.util.EventObject;

/**
 * UnoFlipEvent - represents a state change notification from the UnoFlipModel.
 *
 * @author Emma Wong
 * @version 2.1
 */
public class UnoFlipEvent extends EventObject {
    // Private fields
    private final boolean roundOver;
    private final boolean gameOver;
    private final Player currentPlayer;
    private final Card topCard;
    private final Card.colortype forcedColour;

    /**
     * Constructs a UnoFlipEvent describing the updated model state.
     *
     * @param source the UnoFlipModel that fired this event
     * @param roundOver whether the current round is over
     * @param gameOver whether the entire match is over
     * @param currentPlayer the player whose turn it is after this update
     * @param topCard the current top card on the discard pile
     * @param forcedColour the active forced colour from a wild, otherwise null
     */
    public UnoFlipEvent(UnoFlipModel source, boolean roundOver, boolean gameOver, Player currentPlayer, Card topCard, Card.colortype forcedColour) {
        super(source);
        this.roundOver = roundOver;
        this.gameOver = gameOver;
        this.currentPlayer = currentPlayer;
        this.topCard = topCard;
        this.forcedColour = forcedColour;
    }

    public UnoFlipModel getModel() {
        return (UnoFlipModel) getSource();
    }

    public boolean isRoundOver() {
        return roundOver;
    }

    /**
     * Checks whether the game has ended.
     *
     * @return true if the game is over, otherwise false
     */
    public boolean isGameOver() {
        return gameOver;
    }

    /**
     * Gets the player whose turn it currently is.
     *
     * @return The current Player
     */
    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * Gets the top card of the discard pile.
     *
     * @return The Card at the top of the discard pile or null if empty
     */
    public Card getTopCard() {
        return topCard;
    }

    /**
     * Gets the active colour forced by a wild card.
     *
     * @return The forced colour if a wild effect is active, otherwise null
     */
    public Card.colortype getForcedColour() {
        return forcedColour;
    }
}
