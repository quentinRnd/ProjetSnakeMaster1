package utils;

import java.util.ArrayList;
import java.util.Random;

import model.SnakeGame;

// Random strategy with a one step ahead lookup to avoid actions that leads to a death
public class OneStepAheadAgentStrategy extends AgentStrategy
{
    protected SnakeGame snakeGame;
    private static Random random;

    public OneStepAheadAgentStrategy(Snake snake, SnakeGame snakeGame)
    {
        super(snake);
        this.snakeGame = snakeGame;
        random = new Random();
    }

    @Override
    public AgentAction nextAction()
    {
        // Get legal actions
        ArrayList<AgentAction> legalActions = new ArrayList<AgentAction>();
        for (AgentAction agentAction: AgentAction.values())
        {
            if (snakeGame.isLegalMove(snake, agentAction))
                legalActions.add(agentAction);
        }

        // No action is legal, dumb snake will die
        if (legalActions.size() == 0)
        {
            //System.out.println("No legal nextAction, current snake will die");
            return snake.getLastAction();
        }

        // Search an apple
        ArrayList<FeaturesItem> featuresItems = snakeGame.getFeaturesItems();
        for (FeaturesItem featuresItem: featuresItems)
        {
            // Found an apple
            if (featuresItem.getItemType() == ItemType.APPLE)
            {
                Position applePosition = featuresItem.getPosition();
                Position snakeHeadPosition = snake.getPositions().get(0);
                
                // Select the best move toward the apple
                int bestDistance = -1;
                AgentAction bestlegalAction = legalActions.get(0);
                for (AgentAction legalAction: legalActions)
                {
                    Position nextSnakeHeadPosition = snakeHeadPosition.move(legalAction, snakeGame.getInputMap().getSize());
                    int distance = nextSnakeHeadPosition.manhatanDistance(applePosition);

                    if (bestDistance < 0 || distance < bestDistance)
                    {
                        bestDistance = distance;
                        bestlegalAction = legalAction;
                    }
                }

                // Return the best legal action
                return bestlegalAction;
            }
        }

        // No apple found, return a random legal action
        return legalActions.get(random.nextInt(legalActions.size()));
    }   
}
