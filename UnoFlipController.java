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
    }

    /**
     * Enable/disable all card buttons in the hand
     * @param enabled True if the buttons should be enabled, false if they should be disabled
     */
    private void setHandButtonsEnabled(boolean enabled) {     // *** NEW
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
                }
                cardButton.setEnabled(!actionTakenThisTurn);
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
            if (model.isGameOver()) {
                view.displayMessage(model.getWinner().getName() + " wins the game!");
                handleGameOverIfNeeded();
            } else {
                view.displayMessage(currentPlayer.getName() + " played " + cardToPlay.getDescription());
            }
            // mark that this player has taken their action
            actionTakenThisTurn = true;                          // *** NEW

            // after playing, disable further plays/draws; enable Next Player
            setHandButtonsEnabled(false);
            view.getDrawCardButton().setEnabled(false);
            view.getNextPlayerButton().setEnabled(true);
        } else {
            view.displayError("Invalid move. Please try a different card.");
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
        if (actionTakenThisTurn) {                                // *** NEW
            view.displayError("You can only draw once, and only if you haven't played."); // *** NEW
            return;                                               // *** NEW
        }

        Player currentPlayer = model.getCurrentPlayer();
        String playerName = currentPlayer.getName();

        model.playerDrawsCard();
        view.displayMessage(playerName + " drew a card.");

        // after drawing, the action for this turn is done
        actionTakenThisTurn = true;

        // can't draw again or play more cards; must press Next Player
        setHandButtonsEnabled(false);
        view.getDrawCardButton().setEnabled(false);
        view.getNextPlayerButton().setEnabled(true);

        // Update card listeners for new hand
        setupCardListeners();
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
        if (!actionTakenThisTurn) {
            view.displayError("You must play a card or draw before ending your turn."); // *** NEW
            return;
        }

        model.advanceToNextPlayer();

        // new player's turn starts, so they haven't taken an action yet
        actionTakenThisTurn = false;

        // enable play/draw, disable Next Player until they act
        view.getDrawCardButton().setEnabled(true);
        view.getNextPlayerButton().setEnabled(false);
        setHandButtonsEnabled(true);
        setupCardListeners();
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

        view.getNextPlayerButton().setEnabled(true);

        return true;
    }

    /**
     * Called after actions that might end the game.
     * Uses the view to ask the user if they want to play again,
     * then starts a new game if requested.
     */
    private void handleGameOverIfNeeded() {
        if (!model.isGameOver()) {
            return;
        }

        Player winner = model.getWinner();

        // Ask user via the view (UI code stays in the view)
        boolean playAgain = view.promptPlayAgain(winner);

        if (!playAgain) {
            return; // do nothing, just leave final board on screen
        }

        // Ask for new player setup (also UI in view)
        List<String> playerNames = view.promptForGameSetup();
        if (playerNames == null || playerNames.isEmpty()) {
            return; // user cancelled
        }

        boolean success = startNewGame(playerNames);
        if (success) {
            view.displayMessage("New game started! " +
                    model.getCurrentPlayer().getName() + "'s turn.");
        } else {
            view.displayError("Failed to restart game.");
        }
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

