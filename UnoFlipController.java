import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * UnoFlipController - Controller component of the MVC architecture
 * Handles user input from View and updates Model
 * Has references to both Model and View
 *
 * @author Eshal Kashif
 * @version 2.0
 */

public class UnoFlipController {
    private UnoFlipModel model;
    private UnoFlipFrame view;
    private boolean actionTakenThisTurn; // (played one card OR drawn one card) this turn
    private boolean drewCardThisTurn;   // track if player drew a card
    private int drawnCardIndexThisTurn = -1;
    /**
     * Constructor for UnoFlipController
     *
     * @param model The game model
     * @param view  The game view
     */
    public UnoFlipController(UnoFlipModel model, UnoFlipFrame view) {
        this.model = model;
        this.view = view;

        // Set up event listeners
        initController();

    }

    /*
     * Initializes controller by setting up all event listeners
     */
    private void initController() {
        // Draw card button listener
        view.getDrawCardButton().addActionListener(e -> handleDrawCard());

        // Next player button listener
        view.getNextPlayerButton().addActionListener(e -> handleNextPlayer());

        actionTakenThisTurn = false;
        drewCardThisTurn = false;
    }

    /**
     * Enable/disable all card buttons in the hand
     * @param enabled True if the buttons should be enabled, false if they should be disabled
     */
    private void setHandButtonsEnabled(boolean enabled) {
        JPanel handPanel = view.getPlayerHandPanel();
        Component[] components = handPanel.getComponents();

        for (Component comp : components) {
            if (comp instanceof JButton) {
                comp.setEnabled(enabled);
            }
        }
    }

    /**
     * Sets up listeners for card buttons in the player's hand
     * Called after hand is updated
     */
    public void setupCardListeners() {
        JPanel handPanel = view.getPlayerHandPanel();
        Component[] components = handPanel.getComponents();

        for (Component comp : components) {
            if (comp instanceof JButton) {
                JButton cardButton = (JButton) comp;
                // Remove old listeners
                for (ActionListener al : cardButton.getActionListeners()) {
                    cardButton.removeActionListener(al);
                }

                // Add new listener
                String buttonName = cardButton.getName();
                if (buttonName != null && buttonName.startsWith("card_")) {
                    int cardIndex = Integer.parseInt(buttonName.substring(5));
                    cardButton.addActionListener(e -> handleCardClick(cardIndex));

                    boolean enable;
                    if (drewCardThisTurn && drawnCardIndexThisTurn >= 0) {
                        // after drawing, only the drawn card may be played
                        enable = (cardIndex == drawnCardIndexThisTurn);
                    } else {
                        // Normal rule - enabled if you haven't taken an action yet
                        enable = !actionTakenThisTurn;
                    }
                    cardButton.setEnabled(enable);

                }

            }
        }
    }

    /**
     * Handles clicking on a card to play it
     */
    private void handleCardClick(int cardIndex) {
        if (model.isGameOver()) {
            view.displayError("Game is over!");
            return;
        }
        // don't allow playing a second card in the same turn
        if (actionTakenThisTurn) {
            view.displayError("You already played or drew this turn.");
            return;
        }

        Player currentPlayer = model.getCurrentPlayer();
        List<Card> hand = currentPlayer.getHand();

        if (cardIndex < 0 || cardIndex >= hand.size()) {
            view.displayError("Invalid card index.");
            return;
        }

        // If the player drew this turn, they may only play the drawn card
        if (drewCardThisTurn && cardIndex != drawnCardIndexThisTurn) {
            view.displayError("After drawing, you may only play the drawn card or skip.");
            return;
        }

        Card cardToPlay = hand.get(cardIndex);
        Card.colortype chosenColour = null;

        // If wild card, prompt for colour (View handles the UI)
        if (cardToPlay.getType() == Card.cardtype.WILD ||
                cardToPlay.getType() == Card.cardtype.WILDTWO) {
            chosenColour = view.promptForColour();
            if (chosenColour == null) {
                return; // User cancelled
            }
        }

        // Attempt to play the card through the model
        boolean success = model.playCard(cardIndex, chosenColour);

        if (success) {
            if (model.isRoundOver()) {
                // Someone emptied their hand; scores updated in model
                handleEndOfRoundOrGame();  // handle both round and match end
                return;
            } else {
                String msg;

                // SPECIAL MESSAGES FOR DRAW_ONE AND WILDTWO
                if (cardToPlay.getType() == Card.cardtype.DRAW_ONE) {
                    Player victim = model.getNextPlayer();
                    if (victim != null) {
                        msg = currentPlayer.getName() + " played DRAW ONE, "
                                + victim.getName() + " picked up one card.";
                    } else {
                        msg = currentPlayer.getName() + " played DRAW ONE.";
                    }
                } else if (cardToPlay.getType() == Card.cardtype.WILDTWO) {
                    Player victim = model.getNextPlayer();
                    if (victim != null) {
                        msg = currentPlayer.getName() + " played WILD +2, "
                                + victim.getName() + " picked up two cards.";
                    } else {
                        msg = currentPlayer.getName() + " played WILD +2.";
                    }
                } else {
                    // default message for all other cards (unchanged)
                    msg = currentPlayer.getName() + " played " + cardToPlay.getDescription();
                }

                view.displayMessage(msg);
            }

            // *** ONLY on success should this turn be considered "used"
            actionTakenThisTurn = true;
            drewCardThisTurn = false;
            drawnCardIndexThisTurn = -1;
            setHandButtonsEnabled(false);
            view.getDrawCardButton().setEnabled(false);
            view.getNextPlayerButton().setEnabled(true);

        } else {
            // On invalid move: show error and keep the turn active
            view.displayError("Invalid move. Please try a different card.");

            if (drewCardThisTurn) {
                // After drawing, you may only play the drawn card or skip.
                // Keep only the drawn card enabled; allow skipping; do NOT allow drawing again.
                setHandButtonsEnabled(false);
                enableOnlyDrawnCardButton();

                view.getDrawCardButton().setEnabled(false);
                view.getNextPlayerButton().setEnabled(true);
                // actionTakenThisTurn stays false, so player can still either play the drawn card or skip
            } else {
                // Normal (pre-draw) invalid: let them try a different card or draw one
                actionTakenThisTurn = false;
                setHandButtonsEnabled(true);
                view.getDrawCardButton().setEnabled(true);
                view.getNextPlayerButton().setEnabled(false);
            }
            // Player should still be allowed to choose another card or draw:
            //actionTakenThisTurn = false;
            //setHandButtonsEnabled(true);
            //view.getDrawCardButton().setEnabled(true);
           // view.getNextPlayerButton().setEnabled(false);
        }

        // Update card listeners for new hand
        setupCardListeners();
    }

    /**
     * Handles draw card button click
     */
    private void handleDrawCard() {
        if (model.isGameOver()) {
            view.displayError("Game is over!");
            return;
        }

        // don't allow drawing more than once per turn,
        // or drawing after already playing.
        if (actionTakenThisTurn) {
            view.displayError("You can only draw once, and only if you haven't played.");
            return;
        }

        Player currentPlayer = model.getCurrentPlayer();
        String playerName = currentPlayer.getName();

        //Draw one card and add it to the hand
        Card drawn = model.playerDrawsCard();
        if (drawn == null) {
            view.displayError("Cannot draw right now.");
            return;
        }
        view.displayMessage(playerName + " drew a card.");

        // player may only play the drawn card or skip to next player
        drewCardThisTurn = true;
        actionTakenThisTurn = false;

        // determine the index of the drawn card
        List<Card> hand = currentPlayer.getHand();
        drawnCardIndexThisTurn =  hand.size() - 1;

        // Disable all card buttons, then enable only the drawn card button
        setHandButtonsEnabled(false);
        enableOnlyDrawnCardButton();

        // cannot draw again; can play drawn card or press Next Player
        view.getDrawCardButton().setEnabled(false);
        view.getNextPlayerButton().setEnabled(true);

        // Update card listeners for new hand
        setupCardListeners();
    }

    /**
     * Enables only the drawn card button in the player's hand.
     * Called after drawing or when re-enforcing the draw-only rule.
     */
    private void enableOnlyDrawnCardButton() {
        JPanel handPanel = view.getPlayerHandPanel();
        for (Component comp : handPanel.getComponents()) {
            if (comp instanceof JButton) {
                JButton b = (JButton) comp;
                String name = b.getName();
                if (name != null && name.startsWith("card_")) {
                    try {
                        int index = Integer.parseInt(name.substring(5));
                        // Only the drawn card remains enabled
                        b.setEnabled(index == drawnCardIndexThisTurn);
                    } catch (NumberFormatException ignore) {}
                }
            }
        }
    }


    /**
     * *** Handles Next Player button click
     */
    private void handleNextPlayer() {
        if (model.isGameOver()) {
            view.displayError("Game is over!");
            return;
        }
        //  prevent skipping your turn by pressing Next Player immediately
        if (!actionTakenThisTurn && !drewCardThisTurn) {
            view.displayError("You must play a card or draw before ending your turn.");
            return;
        }

        model.advanceToNextPlayer();

        // new player's turn starts, so they haven't taken an action yet
        actionTakenThisTurn = false;

        // clear any drawn card constraint for the next player
        drewCardThisTurn = false;
        drawnCardIndexThisTurn = -1;

        // enable play/draw, disable Next Player until they act
        view.getDrawCardButton().setEnabled(true);
        view.getNextPlayerButton().setEnabled(false);
        setHandButtonsEnabled(true);
        setupCardListeners();

        Player current = model.getCurrentPlayer();
        if (current != null) {
            view.displayMessage(current.getName() + "'s turn.");
        }
    }

    /**
     * Starts a new game with specified player names
     *
     * @param playerNames List of player names
     * @return true if game started successfully
     */
    public boolean startNewGame(List<String> playerNames) {
        if (playerNames == null || playerNames.size() < 2 || playerNames.size() > 4) {
            return false;
        }

        // Validate player names
        for (String name : playerNames) {
            if (name == null || name.trim().isEmpty()) {
                return false;
            }
        }

        model.initializeGame(playerNames);

        // first player's turn, no action taken yet
        actionTakenThisTurn = false;
        view.getDrawCardButton().setEnabled(true);
        view.getNextPlayerButton().setEnabled(false);

        // Set up card listeners after initial deal
        setupCardListeners();

       // view.getNextPlayerButton().setEnabled(true);

        return true;
    }

    /**
     * Called after a move that might end the round or the whole match.
     */
    private void handleEndOfRoundOrGame() {
        if (!model.isRoundOver()) {
            return; // nothing special to do
        }

        // Disable inputs until we know whether we're continuing
        setHandButtonsEnabled(false);
        view.getDrawCardButton().setEnabled(false);
        view.getNextPlayerButton().setEnabled(false);

        // If the match (to 500) is over:
        if (model.isGameOver()) {
            Player matchWinner = model.getWinner();
            boolean newMatch = view.promptNewMatch(matchWinner);

            if (!newMatch) {
                // user chose not to restart; leave final state
                return;
            }

            // Start a brand new game from scratch (fresh scores, maybe new players)
            List<String> playerNames = view.promptForGameSetup();
            if (playerNames == null || playerNames.isEmpty()) {
                return; // user cancelled
            }

            boolean success = startNewGame(playerNames);
            if (success) {
                view.displayMessage("New game started! " +
                        model.getCurrentPlayer().getName() + "'s turn.");
            } else {
                view.displayError("Failed to start new game.");
            }
            return;
        }

        // Otherwise only the round is over, but match continues
        Player roundWinner = model.getRoundWinner();
        boolean continueGame = view.promptNextRound(roundWinner);

        if (!continueGame) {
            // Player chose to stop after this round
            return;
        }

        // Start next round (keep scores)
        model.startNewRound();

        // Reset controller turn state
        actionTakenThisTurn = false;
        view.getDrawCardButton().setEnabled(true);
        view.getNextPlayerButton().setEnabled(false);
        setHandButtonsEnabled(true);

        setupCardListeners();
    }


    /**
     * Main method to start the application
     * Creates the MVC components and starts the game
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Create Model
            UnoFlipModel model = new UnoFlipModel();

            // Create View with Model
            UnoFlipFrame view = new UnoFlipFrame(model);

            // Create Controller with both Model and View
            UnoFlipController controller = new UnoFlipController(model, view);

            view.setVisible(true);

            // View prompts for game setup (UI responsibility)
            List<String> playerNames = view.promptForGameSetup();

            if (playerNames != null) {
                // Controller handles starting the game (logic responsibility)
                boolean success = controller.startNewGame(playerNames);

                if (success) {
                    view.displayMessage("Game started! " + model.getCurrentPlayer().getName() + "'s turn.");
                } else {
                    view.displayError("Failed to start game.");
                }
            } else {
                System.exit(0); // User cancelled
            }
        });
    }
}

