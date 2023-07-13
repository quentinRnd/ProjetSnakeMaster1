package utils;

import java.util.ArrayList;
import java.util.Random;

// Random strategy
public class RandomAgentStrategy extends AgentStrategy
{
    private static Random random;

    public RandomAgentStrategy(Snake snake)
    {
        super(snake);
        random = new Random();
    }

    @Override
    public AgentAction nextAction()
    {
        // The snake has a body
        if (snake.getPositions().size() > 1)
        {
            // Select a non opposite agent action for the snake
            ArrayList<AgentAction> agentActionsNotOpposite = new ArrayList<AgentAction>();
            for (AgentAction agentAction: AgentAction.values())
            {
                if (!agentAction.isOpposite(snake.getLastAction()))
                    agentActionsNotOpposite.add(agentAction);
            }
            return agentActionsNotOpposite.get(random.nextInt(3));
        }

        // The snake has no body
        else
        {
            // Select a random agent action for the snake
            return AgentAction.values()[random.nextInt(4)];
        }
    }   
}
