# SYSC3110UNOProject - Milestone 2

## Deliverables 
This milestone introduces the Graphical User Interface (GUI) and MVC architecture for the UNO Flip game, transitioning from a text-based version (milestone 1) to an interactive windowed application using Java Swing. 
The game now supports full mouse-based interaction, visual card displays, and improved modularity through Model-View-Controller separation.

Key features implemented:
- Graphical interface within a JFrame (card layout, buttons, and player hand panels)
- Player setup and range selection (2-4 players)
- Visualization of player hands, draw pile, and top card of the discard pile
- Action buttons to play a card, draw a card, next player, and select colour for wild cards
- MVC pattern implementation to ensure loose coupling and high cohesion
- Integration of the Java Event Model to update the View when the Model state changes
- Unit tests for the Model using JUnit
- Updated UML class and sequence diagrams showing MVC communication

## Team Member Contributions
- Eshal Kashif: Developed "UnoFlipFrame.java", "UnoFlipView", and "UnoFlipController.java" 
- Emma Wong: Developed "UnoFlipModel.java", "Data_Structure_Explanation.md", README.
- Anita Gaffuri Kasbiy: Created the UML diagram and sequence diagrams.
- Matthew Sanii: Developed "UnoFlipModelTest.java"
  
## Class Descriptions & Explanations

### UnoFlipModel.java
- Responsibility: manages core game logic including player turns, deck statem action card effects, and scoring.
- Implements the Model in MVC; notifies registered Views of changes through the Observer pattern

### UnoFlipFrame.java
- Responsibility: provides the graphical representation of the game state. Displays cards, player names, and action buttons inside a JFrame.
- Implements the View component of MVC, updating automatically when the Model changes.

### UnoFlipView.java
- Responsibility: provides the interface for the Frame to implement speccific methods.
- Implements the View Interface component of the MVC that allows the model to call structured methods.

### UnoFlipController.java
- Responsibility: handles user input from the GUI (mouse clicks/button presses) and updates the Model accordingly.
- Implements the Controller component of MVC, ensuring proper communication between Model and View

### Card.java
- Responsibility: represent a single card’s immutable attributes: color, type, rank; provide a human-readable description used by the console UI.
- Uses enums (colortype and cardtype) to represent card attributes effectively

### Deck.java
- Responsibility: build and manage the draw pile and the discard pile (the discard and draw pile make up the deck, hence why they are in the same class), shuffle, draw, and expose the current top discard.

### Player.java
- Responsibility: track a player’s identity, score, and hand.



## Future Work (M3+)
- Implement Flip functionality (lightside and darkside of cards)
- AI Player Capability
- Refine GUI layout like animations for card flipping

## Changes to Past Work (M1->M2)
- Please refer to Data_Structure_Explanation.md for descriptions and justifications of changes made between M1 and M2.
