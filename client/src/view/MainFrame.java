package view;

import java.awt.Component;
import java.awt.Container;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.awt.CardLayout;

import javax.swing.JFrame;

import controller.SnakeGameController;

// Main frame
public class MainFrame extends JFrame
{
    // Cards used in the main frame
    public static enum Card
    {
        SIGN_IN,
        GAME_MODE_SELECTION,
        SINGLE_PLAYER_LEVEL_SELECTION,
        MULTI_PLAYER_LOBBY_MENU,
        MULTI_PLAYER_LOBBY_CREATION,
        MULTI_PLAYER_LOBBY,
        MULTI_PLAYER_LOBBY_SELECTION,
        PLAYING_LEVEL
    }

    // Store cards
    private Container container;

    // Allow to show a given card
    private CardLayout cardLayout;

    // Allow to get cards from the container
    private Map<String, Integer> cardsIndex;

    // Allow to show the previous card
    private Stack<Card> previous;

    public MainFrame(SnakeGameController snakeGameController)  
    {
        try
        {
            // Set the frame title
            setTitle("Snake 2");

            // Exit the program on frame closing
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // Cards index is empty
            cardsIndex = new HashMap<>();

            // No previous cards
            previous = new Stack<>();

            // Initialize the container and the layout
            cardLayout = new CardLayout();
            container = getContentPane();
            container.setLayout(cardLayout);

            // Show the sign in panel
            add(Card.SIGN_IN, new SignInPanel(snakeGameController));
            show(Card.SIGN_IN);

            // The frame is visible
            setVisible(true);
        }

        // Show the exception in a new frame
        catch (Exception exception) { new ErrorFrame(exception); }
    }

    // Show the given card
    public void show(Card card)
    {
        // Show the given card
        cardLayout.show(container, card.name());

        // Adjust the frame size to content
        pack();

        // Center the frame on the screen
        setLocationRelativeTo(null);

        // Add the given card to the previous card stack
        previous.add(card);
    }

    // Show the previous card
    public void showPreviousCard()
    {
        // Discard the current card from the previous card stack
        previous.pop();

        // Show the previous card and discard it from the previous card stack
        show(previous.pop());
    }

    // Add a card to the main frame
    public void add(Card card, Component component)
    {
        cardsIndex.put(card.name(), container.getComponentCount());
        container.add(card.name(), component);
    }

    // Get a card from the main frame
    public Component get(Card card)
    {
        return container.getComponent(cardsIndex.get(card.name()));
    }
} 