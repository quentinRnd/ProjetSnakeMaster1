package view;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

import java.util.ArrayList;

import controller.AbstractController;
import model.InputMap;
import utils.FeaturesItem;
import utils.FeaturesSnake;

// Panel for the game
public class PlayingLevelPanel extends SnakeGamePanel
{
    private static int[] moveKeyBindings =
    {
        KeyEvent.VK_UP,
        KeyEvent.VK_DOWN,
        KeyEvent.VK_LEFT,
        KeyEvent.VK_RIGHT
    };

    // Send the key event related to a move key binding to the controller
    private class MoveAction extends AbstractAction
    {
        private int keyEventCode;

        public MoveAction(int keyEventCode)
        {
            this.keyEventCode = keyEventCode;
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) 
        {
            abstractController.keyEvent(keyEventCode);
        }

    }

    // Abstract controller
    private AbstractController abstractController;

    public PlayingLevelPanel(InputMap inputMap, AbstractController abstractController)
    {
        super(inputMap.getSize().getX(), inputMap.getSize().getY(), inputMap.getWalls(), inputMap.getStart_snakes(), inputMap.getStart_items());
        setPreferredSize(new Dimension(inputMap.getSize().getX() * 50, inputMap.getSize().getY() * 50));

        // Set the abstract controller
        this.abstractController = abstractController;

        // Add key bindings to move the snake
        for (int moveKeyBinding: moveKeyBindings)
        {
            getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(moveKeyBinding, 0), moveKeyBinding);
            getActionMap().put(moveKeyBinding, new MoveAction(moveKeyBinding));
        }
    }

    // Update the panel information and repaint it
    public void update(ArrayList<FeaturesSnake> featuresSnakes, ArrayList<FeaturesItem> featuresItems)
    {
        updateInfoGame(featuresSnakes, featuresItems);
        repaint();
    }
}