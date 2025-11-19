import java.util.EventObject;

public class UnoFlipEvent extends EventObject {
    private final boolean roundOver;
    private final boolean gameOver;
    private final Player currentPlayer;
    private final Card topCard;
    private final Card.colortype forcedColour;

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

    public boolean isGameOver() {
        return gameOver;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public Card getTopCard() {
        return topCard;
    }

    public Card.colortype getForcedColour() {
        return forcedColour;
    }
}
