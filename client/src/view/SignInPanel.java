package view;

import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.Color;
import java.awt.GridBagConstraints;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JPasswordField;

import javax.swing.JButton;

import controller.SnakeGameController;

public class SignInPanel extends JPanel
{
    private static final int textFieldsColumns = 7;

    // Error label updated upon failed sign in
    private JLabel errorLabel;

    public SignInPanel(SnakeGameController snakeGameController)
    {
        // Number of rows
        int rows = 0;

        // Set the panel layout
        setLayout(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        
        // Add a hidden error field
        gridBagConstraints.gridy = rows++; gridBagConstraints.gridwidth = 2;
        errorLabel = new JLabel();
        errorLabel.setForeground(Color.RED);
        errorLabel.setVisible(false);
        gridBagConstraints.gridx = 0; add(errorLabel, gridBagConstraints);

        // Add a user name field
        gridBagConstraints.gridy = rows++; gridBagConstraints.gridwidth = 1;
        gridBagConstraints.gridx = 0; add(new JLabel("user name "), gridBagConstraints);
        JTextField userNameTextField = new JTextField(textFieldsColumns);
        gridBagConstraints.gridx = 1; add(userNameTextField, gridBagConstraints);

        // Add a password field
        gridBagConstraints.gridy = rows++; gridBagConstraints.gridwidth = 1;
        gridBagConstraints.gridx = 0; add(new JLabel("password "), gridBagConstraints);
        JPasswordField passwordTextField = new JPasswordField(textFieldsColumns);
        gridBagConstraints.gridx = 1; add(passwordTextField, gridBagConstraints);

        // Add a sign in button
        gridBagConstraints.gridy = rows++; gridBagConstraints.gridwidth = 2;
        JButton signInButton = new JButton("sign in");
        signInButton.addActionListener
        (
            (ActionEvent actionEvent) ->
            {
                snakeGameController.signIn(userNameTextField.getText(), new String(passwordTextField.getPassword()));
            }
        );
        gridBagConstraints.gridx = 0; add(signInButton, gridBagConstraints);
    }

    public void showError (String message)
    {
        errorLabel.setText("Error: " + message);
        errorLabel.setVisible(true);
    }
}