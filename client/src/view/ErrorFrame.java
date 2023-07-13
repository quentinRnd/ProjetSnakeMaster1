package view;

import javax.swing.JFrame;
import javax.swing.JLabel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// Frame for display errors
public class ErrorFrame extends JFrame 
{
    private static final Logger logger = LogManager.getLogger();

    public ErrorFrame(Exception exception)
    {
        // Set the frame title
        setTitle("Error");

        // Exit the program on frame closing
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Add a label to the frame
        JLabel messageLabel = new JLabel(exception.toString());
        add(messageLabel);
        
        logger.error("", exception);

        // Adjust the frame size to content
        pack();

        // Center the frame on the screen
        setLocationRelativeTo(null);

        // The frame is visible
        setVisible(true);
    }
}