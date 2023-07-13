package utils;

// Player strategy
public class PlayerAgentStrategy extends AgentStrategy
{    
    private AgentAction nextAgentAction;

    public PlayerAgentStrategy(Snake snake)
    {
        super(snake);
        nextAgentAction = this.snake.getLastAction();
    }
    
    public void setNextAction(AgentAction nextAgentAction)
    {
        this.nextAgentAction = nextAgentAction;
    }

    @Override
    public AgentAction nextAction()
    {
        // The snake has a body
        if (snake.getPositions().size() > 1)
        {
            // Next player agent action is impossible
            if (nextAgentAction.isOpposite(snake.getLastAction()))
            {
                // Repeat last action
                return snake.getLastAction();
            }

            // Next player agent action is possible
            else
            {
                // Play the next player agent action
                return nextAgentAction;
            }
        }

        // The snake has no body
        else
        {
            // Play the next player agent action
            return nextAgentAction;
        }
    }   
}
