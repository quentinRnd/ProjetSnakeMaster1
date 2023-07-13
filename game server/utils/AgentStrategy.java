package utils;

public abstract class AgentStrategy
{
    protected Snake snake;

    public AgentStrategy(Snake snake)
    {
        this.snake = snake;
    }

    public abstract AgentAction nextAction();
}