package utils;

public enum AgentAction 
{
	MOVE_UP,
	MOVE_DOWN,
	MOVE_LEFT,
	MOVE_RIGHT;

	// Return the opposite of agentAction
    public static final AgentAction oppositeOf(AgentAction agentAction)
    {
        switch (agentAction)
        {
            case MOVE_UP:
                return AgentAction.MOVE_DOWN;
            case MOVE_DOWN:
                return AgentAction.MOVE_UP;
            case MOVE_LEFT:
                return AgentAction.MOVE_RIGHT;
            case MOVE_RIGHT:
            default:
                return AgentAction.MOVE_LEFT;
        }
    }

    // Return true if the opposite of agentAction
    public final boolean isOpposite(AgentAction agentAction)
    {
        return this == oppositeOf(agentAction);
    }
}
