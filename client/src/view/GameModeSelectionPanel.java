package view;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import javax.swing.JPanel;

import javax.swing.JButton;

import controller.SnakeGameController;
import controller.SnakeGameController.GameMode;

public class GameModeSelectionPanel extends JPanel
{
    public GameModeSelectionPanel(SnakeGameController snakeGameController)
    {
        // Set the panel layout
        setLayout(new GridLayout(2, 1));

        // Add an single player button
        JButton singlePlayerButton = new JButton("single player");
        singlePlayerButton.addActionListener((ActionEvent actionEvent) -> { snakeGameController.play(GameMode.SINGLE_PLAYER); });
        add(singlePlayerButton);

        // Add a multi player button
        JButton multiplayerButton = new JButton("multi player");
        multiplayerButton.addActionListener((ActionEvent actionEvent) -> { snakeGameController.play(GameMode.MULTI_PLAYER); });
        add(multiplayerButton);
    }
}