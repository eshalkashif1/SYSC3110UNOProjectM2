import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

/**
 * UnoFlipFrame - Main GUI window for the Uno Flip game
 * Implements the View component of MVC architecture
 *
 * @author Eshal Kashif
 * @version 2.0
 */

public class UnoFlipFrame extends JFrame implements UnoFlipView {

    private UnoFlipModel model;

    // GUI Components
    private JPanel topCardPanel;
    private JLabel topCardLabel;
    private JLabel currentPlayerLabel;
    private JLabel statusLabel;
    private JPanel playerHandPanel;
    private JButton nextPlayerButton;
    private JButton drawCardButton;
    private JTextArea scoreboardArea;

    // Card dimensions
    private static final int CARD_WIDTH = 100;
    private static final int CARD_HEIGHT = 140;

    /**
     * Constructor - initializes the GUI components
     *
     * @param model The game model to observe
     */
    public UnoFlipFrame(UnoFlipModel model) {
        super("UnoFlip Game");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLayout(new BorderLayout(10, 10));

        this.model = model;
        model.addView(this);

        initializeComponents();
        //setVisible(true);
    }

    /**
     * Initializes all GUI components
     */
    private void initializeComponents() {
        // Top card display
        topCardPanel = new JPanel();
        topCardPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        topCardPanel.setBorder(BorderFactory.createTitledBorder("Top Card"));
        topCardLabel = new JLabel("No card");
        topCardLabel.setPreferredSize(new Dimension(CARD_WIDTH, CARD_HEIGHT));
        topCardLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        topCardLabel.setHorizontalAlignment(SwingConstants.CENTER);
        topCardLabel.setOpaque(true);
        topCardLabel.setBackground(Color.WHITE);
        topCardPanel.add(topCardLabel);

        // Current player info
        JPanel infoPanel = new JPanel(new GridLayout(2, 1));
        currentPlayerLabel = new JLabel("Current Player: None");
        currentPlayerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        statusLabel = new JLabel("Status Message:");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        infoPanel.add(currentPlayerLabel);
        infoPanel.add(statusLabel);

        // Player hand panel
        playerHandPanel = new JPanel();
// 0 rows = “as many rows as needed”, 7 columns across, with gaps 5x5
        playerHandPanel.setLayout(new GridLayout(0, 7, 5, 5));
        JScrollPane handScrollPane = new JScrollPane(
                playerHandPanel,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
        );
        handScrollPane.setPreferredSize(new Dimension(1150, 250));
        handScrollPane.setBorder(BorderFactory.createTitledBorder("Your Hand"));


        // Control buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        drawCardButton = new JButton("DRAW CARD");
        drawCardButton.setFont(new Font("Arial", Font.BOLD, 14));

        nextPlayerButton = new JButton("NEXT PLAYER");
        nextPlayerButton.setFont(new Font("Arial", Font.BOLD, 14));
        //nextPlayerButton.setEnabled(false);

        buttonPanel.add(drawCardButton);
        buttonPanel.add(nextPlayerButton);

        // Scoreboard
        scoreboardArea = new JTextArea(5, 20);
        scoreboardArea.setEditable(false);
        scoreboardArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scoreScrollPane = new JScrollPane(scoreboardArea);
        scoreScrollPane.setBorder(BorderFactory.createTitledBorder("Scoreboard"));

        // Combine info and buttons
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(topCardPanel, BorderLayout.NORTH);
        topPanel.add(infoPanel, BorderLayout.CENTER);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(handScrollPane, BorderLayout.CENTER);
        centerPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(scoreScrollPane, BorderLayout.EAST);
    }

    /**
     * Gets the draw card button (for controller to add listener)
     *
     * @return The draw card JButton
     */
    public JButton getDrawCardButton() {
        return drawCardButton;
    }

    /**
     * Gets the next player button (for controller to add listener)
     *
     * @return The next player JButton
     */
    public JButton getNextPlayerButton() {
        return nextPlayerButton;
    }

    /**
     * Gets the player hand panel (for controller to add card listeners)
     *
     * @return the player's hand JPanel
     */
    public JPanel getPlayerHandPanel() {
        return playerHandPanel;
    }

    /**
     * Prompts user for game setup and returns player names
     * This is a UI responsibility, so it belongs in the View
     * @return List of player names, or null if cancelled
     */
    public List<String> promptForGameSetup() {
        // Get number of players
        String numPlayersStr = JOptionPane.showInputDialog(this,
                "Enter number of players (2-4):",
                "Game Setup",
                JOptionPane.QUESTION_MESSAGE);

        if (numPlayersStr == null) {
            return null; // User cancelled
        }

        int numPlayers;
        try {
            numPlayers = Integer.parseInt(numPlayersStr.trim());
            if (numPlayers < 2 || numPlayers > 4) {
                displayError("Number of players must be between 2 and 4.");
                return promptForGameSetup(); // Try again
            }
        } catch (NumberFormatException e) {
            displayError("Invalid input. Please enter a number.");
            return promptForGameSetup(); // Try again
        }

        // Get player names
        List<String> playerNames = new ArrayList<>();
        for (int i = 1; i <= numPlayers; i++) {
            String name = JOptionPane.showInputDialog(this,
                    "Enter name for Player " + i + ":",
                    "Player Setup",
                    JOptionPane.QUESTION_MESSAGE);

            if (name == null) {
                return null; // User cancelled
            }

            if (name.trim().isEmpty()) {
                displayError("Player name cannot be empty.");
                return promptForGameSetup(); // Try again
            }

            playerNames.add(name.trim());
        }

        return playerNames;
    }


    /**
     * Updates the view to reflect current game state
     * Called by the model when state changes
     */
    @Override
    public void update() {
        updateTopCard();
        updateCurrentPlayer();
        updatePlayerHand();
        updateScoreboard();

        // Check if game is over
        if (model.isGameOver()) {
            drawCardButton.setEnabled(false);
            nextPlayerButton.setEnabled(false);
            /*
            Player winner = model.getWinner();
            JOptionPane.showMessageDialog(this,
                    winner.getName() + " wins the game!\nFinal Score: " + winner.getScore(),
                    "Game Over",
                    JOptionPane.INFORMATION_MESSAGE);

             */
        }
    }


    /**
     * Show who won the round and ask if we should continue to the next round.
     * @param roundWinner the player who won the round
     * @return true if user wants to continue, false to stop
     */
    public boolean promptNextRound(Player roundWinner) {
        String message = roundWinner.getName() + " won this round!\n"
                + "Points this round: " + model.getLastRoundPoints() + "\n"
                + "Total score: " + roundWinner.getScore() + "\n\n"
                + "Continue to the next round?";
        int choice = JOptionPane.showConfirmDialog(
                this,
                message,
                "Round Over",
                JOptionPane.YES_NO_OPTION
        );
        return choice == JOptionPane.YES_OPTION;
    }

    /**
     * Show final match winner and ask if we should start a new game.
     * @param matchWinner the overall winner (500+ points)
     * @return true if user wants to start a brand new game
     */
    public boolean promptNewMatch(Player matchWinner) {
        String message = matchWinner.getName() + " wins the game!\nFinal Score: "
                + matchWinner.getScore() + "\n\nStart a new game?";
        int choice = JOptionPane.showConfirmDialog(
                this,
                message,
                "Game Over",
                JOptionPane.YES_NO_OPTION
        );
        return choice == JOptionPane.YES_OPTION;
    }




    /**
     * Updates the top card display
     */
    private void updateTopCard() {
        Card topCard = model.getTopCard();
        if (topCard != null) {
            String displayText;
            Card.colortype forcedColour = model.getForcedColour();
            if (forcedColour != null) {
                displayText = forcedColour + " (from WILD)";
            } else {
                displayText = topCard.getDescription();
            }
            topCardLabel.setText("<html><center>" + displayText + "</center></html>");
            topCardLabel.setBackground(getColorForCard(topCard, forcedColour));
        }
    }

    /**
     * Updates current player display
     */
    private void updateCurrentPlayer() {
        Player currentPlayer = model.getCurrentPlayer();
        if (currentPlayer != null) {
            currentPlayerLabel.setText("Current Player: " + currentPlayer.getName());
        }
    }

    /**
     * Updates the player's hand display
     */
    public void updatePlayerHand() {
        playerHandPanel.removeAll();
        Player currentPlayer = model.getCurrentPlayer();
        if (currentPlayer != null) {
            List<Card> hand = currentPlayer.getHand();

            for (int i = 0; i < hand.size(); i++) {
                Card card = hand.get(i);
                JButton cardButton = createCardButton(card, i);
                playerHandPanel.add(cardButton);
            }
        }
        playerHandPanel.revalidate();
        playerHandPanel.repaint();
    }

    /**
     * Creates a button representing a card
     * @param card The new card to represent
     * @param index The card's index in the hand
     */
    private JButton createCardButton(Card card, int index) {
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(CARD_WIDTH, CARD_HEIGHT));
        button.setBackground(getColorForCard(card, null));
        button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        button.setName("card_" + index); // Set name so controller can identify which card

        // Multi-line text for card
        String cardText = "<html><center><b>" + card.getColor() + "</b><br><br>";
        if (card.getType() == Card.cardtype.NUMBER) {
            cardText += "<font size='+3'>" + card.getRank() + "</font>";
        } else {
            cardText += card.getType();
        }
        cardText += "</center></html>";
        button.setText(cardText);

        return button;
    }

    /**
     * Updates the scoreboard display
     */
    private void updateScoreboard() {
        StringBuilder sb = new StringBuilder();
        sb.append("Scoreboard:\n");
        sb.append("\n");

        for (Player p : model.getPlayers()) {
            sb.append(String.format("%-15s: %d\n", p.getName(), p.getScore()));
        }

        scoreboardArea.setText(sb.toString());
    }

    /**
     * Displays a message to the user
     * @param message The message to display
     */
    @Override
    public void displayMessage(String message) {
        statusLabel.setText("Status: " + message);
    }

    /**
     * Displays an error message
     * @param message The error message to display
     */
    @Override
    public void displayError(String message) {
        statusLabel.setText("Error: " + message);
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Gets the appropriate background color for a card
     * @param card The card to get the background colour for
     * @param forcedColour The card's colour
     */
    private Color getColorForCard(Card card, Card.colortype forcedColour) {
        Card.colortype colorToUse = (forcedColour != null) ? forcedColour : card.getColor();

        switch (colorToUse) {
            case RED:
                return new Color(255, 100, 100);
            case BLUE:
                return new Color(100, 150, 255);
            case GREEN:
                return new Color(100, 255, 100);
            case YELLOW:
                return new Color(255, 255, 100);
            case ALL:
                return new Color(200, 200, 200);
            default:
                return Color.WHITE;
        }
    }

    /**
     * Prompts user to select a colour for wild cards
     */
    public Card.colortype promptForColour() {
        String[] colours = {"RED", "BLUE", "GREEN", "YELLOW"};
        String choice = (String) JOptionPane.showInputDialog(
                this,
                "Choose a colour:",
                "Wild Card",
                JOptionPane.QUESTION_MESSAGE,
                null,
                colours,
                colours[0]
        );
        if (choice == null) return null;
        return parseColour(choice);
    }

    /**
     * Parses a colour string to enum
     * @param s The inputted colour
     */
    private Card.colortype parseColour(String s) {
        if (s == null) return null;
        s = s.toUpperCase().trim();

        switch (s) {
            case "RED":
                return Card.colortype.RED;
            case "BLUE":
                return Card.colortype.BLUE;
            case "GREEN":
                return Card.colortype.GREEN;
            case "YELLOW":
                return Card.colortype.YELLOW;
            default:
                return null;
        }
    }

}
