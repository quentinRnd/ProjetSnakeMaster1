package view;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;

import javax.swing.JPanel;
import javax.swing.JButton;

import controller.SnakeGameController;

public class MultiPlayerLobbyMenuPanel extends JPanel
{
    public MultiPlayerLobbyMenuPanel(SnakeGameController snakeGameController)
    {
        // Number of rows
        int rows = 0;

        // Set the panel layout
        setLayout(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;

        // Add a create lobby button
        gridBagConstraints.gridy = rows++; gridBagConstraints.gridwidth = 1;
        JButton createLobbyButton = new JButton("create lobby");
        createLobbyButton.addActionListener((ActionEvent actionEvent) -> { snakeGameController.lobbyCreation(); });
        gridBagConstraints.gridx = 0; add(createLobbyButton, gridBagConstraints);

        // Add a select lobby field
        gridBagConstraints.gridy = rows++; gridBagConstraints.gridwidth = 1;
        JButton joinLobbyButton = new JButton("select lobby");
        joinLobbyButton.addActionListener((ActionEvent actionEvent) -> { snakeGameController.lobbySelection(); });
        gridBagConstraints.gridx = 0; add(joinLobbyButton, gridBagConstraints);

        // Add a back button
        gridBagConstraints.gridy = rows++; gridBagConstraints.gridwidth = 1;
        JButton backButton = new JButton("back");
        backButton.addActionListener((ActionEvent actionEvent) -> { snakeGameController.back(); });
        gridBagConstraints.gridx = 0; add(backButton, gridBagConstraints);
    }
}