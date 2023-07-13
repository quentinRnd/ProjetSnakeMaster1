package view;

import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;

import controller.SnakeGameController;

// Panel to select levels in single player mode
public class SinglePlayerLevelSelectionPanel extends JPanel
{
    public SinglePlayerLevelSelectionPanel(SnakeGameController snakeGameController)
    {
        // Number of rows
        int rows = 0;

        // Set the panel layout
        setLayout(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;

        // Add a versus AI field
        gridBagConstraints.gridy = rows++; gridBagConstraints.gridwidth = 1;
        gridBagConstraints.gridx = 0; add(new JLabel("versus AI "), gridBagConstraints);
        JComboBox<String> versusAIcommboBox = new JComboBox<String>(new String[]{"none", "easy", "medium", "hard"});
        gridBagConstraints.gridx = 1; add(versusAIcommboBox, gridBagConstraints);
            
        // Add a walls check box
        gridBagConstraints.gridy = rows++; gridBagConstraints.gridwidth = 1;
        gridBagConstraints.gridx = 0; add(new JLabel("walls "), gridBagConstraints);
        JCheckBox wallsCheckBox = new JCheckBox();
        gridBagConstraints.gridx = 1; add(wallsCheckBox, gridBagConstraints);

        // Add a create in button
        gridBagConstraints.gridy = rows++; gridBagConstraints.gridwidth = 2;
        JButton createButton = new JButton("create");
        createButton.addActionListener
        (
            (ActionEvent actionEvent) ->
            {
                snakeGameController.createSinglePlayerGame
                (
                    (String) versusAIcommboBox.getSelectedItem(),
                    wallsCheckBox.isSelected()
                );
            }
        );
        gridBagConstraints.gridx = 0; add(createButton, gridBagConstraints);

        // Add a back button
        gridBagConstraints.gridy = rows++; gridBagConstraints.gridwidth = 2;
        JButton backButton = new JButton("back");
        backButton.addActionListener((ActionEvent actionEvent) -> { snakeGameController.back(); });
        gridBagConstraints.gridx = 0; add(backButton, gridBagConstraints);
    }
}