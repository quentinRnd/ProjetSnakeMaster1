package view;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JButton;

import java.awt.event.ActionEvent;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;

import controller.SnakeGameController;

// Panel to join an existing lobby in multi player
public class MultiPlayerLobbySelectionPanel extends JPanel
{
    public MultiPlayerLobbySelectionPanel(SnakeGameController snakeGameController)
    {
        // Number of rows
        int rows = 0;

        // Set the panel layout
        setLayout(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();

        // Add a lobby id text field
        gridBagConstraints.gridwidth = 1; gridBagConstraints.gridx = 0;
        JTextField lobbyIdTextField = new JTextField();
        gridBagConstraints.gridy = rows; add(lobbyIdTextField, gridBagConstraints);

        // Add a join lobby button
        gridBagConstraints.gridwidth = 1; gridBagConstraints.gridx = 1;
        JButton joinLobbyButton = new JButton("join lobby");
        joinLobbyButton.addActionListener((ActionEvent actionEvent) -> { snakeGameController.joinLobby(Integer.parseInt(lobbyIdTextField.getText())); });
        gridBagConstraints.gridy = rows++; add(joinLobbyButton, gridBagConstraints);
    }
}