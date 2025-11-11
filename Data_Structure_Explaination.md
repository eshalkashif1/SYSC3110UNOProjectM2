# DATA STRUCTURE EXPLAINATION

From milestone 1 to milestone 2, the data structures updated from a text-based design to a MVC architecture with Model, View, and Controller components.

## Added Classes
- UnoFlipModel, UnoFlipView, and UnoFlipController were introduced to replace the UnoFlip class used in milestone 1. This change improves modularity and supports GUI interaction via Swing components.
- The UnoFlipModel now stores and updates the game state (players, deck, and current card) and uses the Observer pattern to notify the View of changes in the model. As a result, increasing cohesion and enabling automatic UI refreshes.

## Modified UML Relationships
The UML diagram was expanded and restructured to reflect the MVC architecture and new class interactions introduced in milestone 2.

  ### Model-View Relationship
    - The UnoFlipModel now maintains a list of registered UnoFlipView Observers
    - It notifies all views of state changes through notifyViews()
    - The UnoFlipFrame implements the UnoFlipView interface, allowing the UnoFlipModel to update one or more active views whenever the game state changes.

  ### Model-Controller Relationship
    - The UnoFlipController holds references to both the UnoFlipModel and the UnoFlipFrame, allowing for their communication.
    - The controller updates the model in response to user actions while the model triggers updates in the view when its state changes.
    - This ensures indirect interaction between the user interface and core logic. Thus, reducing coupling.

  ### View-Controller Relationship
    - The UnoFlipFrame delegates all user interactions to the UnoFlipController.
    - This establishes a relationship where the View depends on the Controller for handling events and game progression. 

## Unchanged Game Classes
- UnoFlipModel uses key game components Deck, Player, and Card to manage the overall game state.
- These classes remained structurally similar to milestone 1 but were integrated into the Model instead of being directly accessed by the main game class.
- Player maintains a list of Card objects representing its hand.
- Deck manages both draw and discard piles using ArrayList and ArrayDeque.
- Enumerations colortype and cardtype are referenced within the Card class to define attributes such as colour and function.

## Design Justification
  Controller-Based Feedback:
- The Controller is responsible for generating user feedback messages displayed on the View. While the Model triggers general updates through notifyViews(), it does not have context about what specific user action occurred.
- Therefore, the Controller composes of context-aware messages before sending them to the View, ensuring proper MVC separation.

## Reason for Change
The update to MVC architecture and GUI design in this milestone was necessary to support event driven interaction and graphical representation of the game. As a result, the project design is more scalable, testable, and aligned with object-oriented principles such as loose coupling and high cohesion.
