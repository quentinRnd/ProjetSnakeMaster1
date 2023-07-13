package utils;

import model.SnakeGame;

// Allow to create snakes with different strategy on a game
public class SnakeFactory
{
    public static FeaturesSnake createFromSub(Snake snake)
    {
        return new FeaturesSnake
        (
            snake.getPositions(),
            snake.getLastAction(),
            snake.getSnakeType(),
            snake.isInvincible(),
            snake.isSick()
        );
    }

    private SnakeGame snakeGame;

    public SnakeFactory(SnakeGame snakeGame)
    {
        this.snakeGame = snakeGame;
    }

    public Snake createFromBase(FeaturesSnake featuresSnake)
    {
        // Create the snake
        Snake snake  = new Snake
        (
            featuresSnake.getPositions(), 
            featuresSnake.getLastAction(), 
            featuresSnake.getSnakeType(), 
            featuresSnake.isInvincible(), 
            featuresSnake.isSick()
        );

        // Set the snake strategy
        AgentStrategy strategy;
        switch (featuresSnake.getSnakeType())
        {
            case RANDOM:
                strategy = new RandomAgentStrategy(snake);
            break;

            case ONE_STEP_AHEAD:
                strategy = new OneStepAheadAgentStrategy(snake, snakeGame);
            break;

            case A_STAR:
                strategy = new AStarAgentStrategy(snake, snakeGame);
            break;

            case PLAYER:
            default:
                strategy = new PlayerAgentStrategy(snake);
        }
        snake.setStrategy(strategy);

        // Return the snake
        return snake;
    }
}
