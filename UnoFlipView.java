import java.util.EventListener;

/**
 * UnoFlipView Interface
 * Defines the contract for views that observe the UnoFlipModel
 *
 * @author Eshal Kashif
 * @version 2.1
 *
 */
public interface UnoFlipView extends EventListener {

    /**
     * Called when the model changes state
     * Views should update their display to reflect current game state
     *
     * @param event the UnoFlipEvent describing the update
     */
    void update(UnoFlipEvent event);

    /**
     * Called when the model wants to display a message
     */
    void displayMessage(String message);

    /**
     * Called when the model wants to display an error
     */
    void displayError(String message);
}
