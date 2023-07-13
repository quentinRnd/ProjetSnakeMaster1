package view;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JLabel;

import java.awt.event.ActionEvent;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;

import controller.SnakeGameController;
import model.InputMap;

// Panel to wait for players joining the lobby in multi player
public class MultiPlayerLobbyPanel extends JPanel
{
    private static final int chatInputTextFieldColumns = 20;

    private SnakeGameController snakeGameController;

    private InputMap inputMap;

    // Count remaining players
    private int remainingPlayers;
    private JLabel remainingPlayersLabel;

    // Chat input and output updated by the controller
    private JTextArea chatOutputTextArea;
    private JTextField chatInputTextField;

    public MultiPlayerLobbyPanel(SnakeGameController snakeGameController, int id, InputMap inputMap, int remainingPlayers)
    {
        this.snakeGameController = snakeGameController;
        this.inputMap = inputMap;
        this.remainingPlayers = remainingPlayers;

        // Number of rows
        int rows = 0;

        // Set the panel layout
        setLayout(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;

        // Add a labbel to show the lobby id
        gridBagConstraints.gridy = rows++; gridBagConstraints.gridwidth = 1;
        gridBagConstraints.gridx = 0; add(new JLabel("lobby id "), gridBagConstraints);
        gridBagConstraints.gridx = 1; add(new JLabel(Integer.toString(id)), gridBagConstraints);

        // Add a labbel to show remaining players
        gridBagConstraints.gridy = rows++; gridBagConstraints.gridwidth = 1;
        gridBagConstraints.gridx = 0; add(new JLabel("remaining players "), gridBagConstraints);
        remainingPlayersLabel = new JLabel(Integer.toString(remainingPlayers));
        gridBagConstraints.gridx = 1; add(remainingPlayersLabel, gridBagConstraints);

        // Add a chat output scroll pane
        gridBagConstraints.gridy = rows++; gridBagConstraints.gridwidth = 2;
        chatOutputTextArea = new JTextArea(7, (int) Math.floor(chatInputTextFieldColumns * 1.25));
        chatOutputTextArea.setEditable(false);
        JScrollPane chatOutputScrollPane = new JScrollPane(chatOutputTextArea);
        gridBagConstraints.gridx = 0; add(chatOutputScrollPane, gridBagConstraints);

        // Add a chat input field
        gridBagConstraints.gridy = rows++; gridBagConstraints.gridwidth = 1;
        chatInputTextField = new JTextField(chatInputTextFieldColumns);
        gridBagConstraints.gridx = 0; add(chatInputTextField, gridBagConstraints);
        JButton sendButton = new JButton("send");
        sendButton.addActionListener
        (
            (ActionEvent actionEvent) -> 
            {
                snakeGameController.broadcast(chatInputTextField.getText());
                chatInputTextField.setText("");
            }
        );
        gridBagConstraints.gridx = 1; add(sendButton, gridBagConstraints);
    }

    public void append(String userName, String message)
    {
        chatOutputTextArea.append(String.format("%s - %s\n", userName, message));
    }

    public void joined(String userName)
    {
        chatOutputTextArea.append(String.format("Player %s joined the lobby\n", userName));
        remainingPlayersLabel.setText(Integer.toString(--remainingPlayers));

        // Start the game if all players are in the lobby
        if (remainingPlayers == 0)
            snakeGameController.playLevel(inputMap);
    }
}