package controller;

import utils.AgentAction;

import java.awt.event.KeyEvent;
import java.awt.Font;

import javax.swing.UIManager;
import com.formdev.flatlaf.FlatDarkLaf;

import net.Communication;

import model.InputMap;
import view.MainFrame;
import view.MultiPlayerLobbyCreationPanel;
import view.MultiPlayerLobbyMenuPanel;
import view.MultiPlayerLobbyPanel;
import view.MultiPlayerLobbySelectionPanel;
import view.PlayingLevelPanel;
import view.SignInPanel;
import view.SinglePlayerLevelSelectionPanel;
import view.MainFrame.Card;
import view.ErrorFrame;
import view.GameModeSelectionPanel;

// Snake game controller
public class SnakeGameController extends AbstractController
{
    public static enum GameMode { SINGLE_PLAYER, MULTI_PLAYER }

    private MainFrame mainFrame;

    public SnakeGameController(Communication communication)
    {
        super(communication);

        try
        {
            // Set the UI look and feel to a flat dark theme
            UIManager.setLookAndFeel(new FlatDarkLaf());

            // Use a big and bold font
            Font defaultFont = (Font) UIManager.getLookAndFeelDefaults().get("defaultFont");
            Font newFont = new Font(defaultFont.getName(), Font.BOLD, 20);
            UIManager.getLookAndFeelDefaults().put("defaultFont", newFont);

            // Create and show the main frame
            mainFrame = new MainFrame(this);
        }

        catch (Exception exception)
        {
            // Create and show the error
            ErrorFrame viewError = new ErrorFrame(exception);
            viewError.setVisible(true);
        }
    }

    @Override
    public void keyEvent(int keyEventCode)
    {
        // Notify the game of new agent action
        AgentAction nextPlayerAgentAction;
        switch (keyEventCode)
        {
            case KeyEvent.VK_UP:
                nextPlayerAgentAction = AgentAction.MOVE_UP;
            break;

            case KeyEvent.VK_DOWN:
                nextPlayerAgentAction = AgentAction.MOVE_DOWN;
            break;

            case KeyEvent.VK_LEFT:
                nextPlayerAgentAction = AgentAction.MOVE_LEFT;
            break;

            case KeyEvent.VK_RIGHT:
                nextPlayerAgentAction = AgentAction.MOVE_RIGHT;
            break;

            default:
                nextPlayerAgentAction = null;
        }

        if (nextPlayerAgentAction != null)
            communication.send("next player agent action", nextPlayerAgentAction);
    }

    // Send credentials to the game server to sign in to a user account
    public void signIn(String userName, String password)
    {
        communication.send("sign in", userName, password);
    }

    public void signInError(String message)
    {
        ((SignInPanel) mainFrame.get(Card.SIGN_IN)).showError(message);
        mainFrame.show(Card.SIGN_IN);
    }

    // Show the game mode selection panel after successful sign in
    public void signedIn()
    {
        mainFrame.add(Card.GAME_MODE_SELECTION, new GameModeSelectionPanel(this)); 
        mainFrame.show(Card.GAME_MODE_SELECTION);
    }

    // Show the respective panels to play with the given game mode
    public void play(GameMode gameMode)
    {
        switch (gameMode)
        {
            // Show the single player level selection panel
            case SINGLE_PLAYER:
                mainFrame.add(Card.SINGLE_PLAYER_LEVEL_SELECTION, new SinglePlayerLevelSelectionPanel(this)); 
                mainFrame.show(Card.SINGLE_PLAYER_LEVEL_SELECTION);
            break;

            // Show the multi player lobby menu panel
            case MULTI_PLAYER:
                mainFrame.add(Card.MULTI_PLAYER_LOBBY_MENU, new MultiPlayerLobbyMenuPanel(this)); 
                mainFrame.show(Card.MULTI_PLAYER_LOBBY_MENU);
            break;
        }
    }

    // Send the given information to the server to create a snake game
    public void createSinglePlayerGame(String versusAI, boolean walls)
    {            
        communication.send("create single player game", versusAI, walls);
    }

    // Show the previous card of the main frame
    public void back()
    {
        mainFrame.showPreviousCard();
    }

    // Show the online lobby creation panel
    public void lobbyCreation()
    {
        mainFrame.add(Card.MULTI_PLAYER_LOBBY_CREATION, new MultiPlayerLobbyCreationPanel(this));
        mainFrame.show(Card.MULTI_PLAYER_LOBBY_CREATION);
    }

    // Create a lobby with the given values
    public void createLobby(int numberOfPlayers, String versusAI, boolean walls)
    {
        communication.send("create lobby", numberOfPlayers, versusAI, walls);
    }

    // Show the created lobby with the given id and input map
    public void lobbyCreated(int id, InputMap inputMap)
    {
        mainFrame.add(Card.MULTI_PLAYER_LOBBY, new MultiPlayerLobbyPanel(this, id, inputMap, inputMap.getStart_snakes().size() - 1));
        mainFrame.show(Card.MULTI_PLAYER_LOBBY);
    }

    // Broadcast a chat message to other clients in the same lobby
    public void broadcast(String message)
    {
        communication.send("broadcast chat lobby message", message);
    }

    // Append a given message from the given user to the lobby
    public void appendToLobby(String userName, String message)
    {
        ((MultiPlayerLobbyPanel) mainFrame.get(Card.MULTI_PLAYER_LOBBY)).append(userName, message);
    }

    // Show the online lobby selection panel
    public void lobbySelection()
    {
        mainFrame.add(Card.MULTI_PLAYER_LOBBY_SELECTION, new MultiPlayerLobbySelectionPanel(this));
        mainFrame.show(Card.MULTI_PLAYER_LOBBY_SELECTION);
    }

    // Send a join lobby request to the server
    public void joinLobby(int id)
    {
        communication.send("join lobby", id);
    }

    // Show the joined lobby
    public void showLobby(int id, InputMap inputMap, int remainingPlayers)
    {
        mainFrame.add(Card.MULTI_PLAYER_LOBBY, new MultiPlayerLobbyPanel(this, id, inputMap, remainingPlayers));
        mainFrame.show(Card.MULTI_PLAYER_LOBBY);
    }

    // The given user just joined the lobby
    public void joinedLobby(String userName)
    {
        ((MultiPlayerLobbyPanel) mainFrame.get(Card.MULTI_PLAYER_LOBBY)).joined(userName);
    }

    // Show the playing level panel
    public void playLevel(InputMap inputMap)
    {
        mainFrame.add(Card.PLAYING_LEVEL, new PlayingLevelPanel(inputMap, this));
        mainFrame.show(Card.PLAYING_LEVEL);
        communication.send("play");
    }

    public PlayingLevelPanel getPlayingLevel()
    {
        return (PlayingLevelPanel) mainFrame.get(Card.PLAYING_LEVEL);
    }
}