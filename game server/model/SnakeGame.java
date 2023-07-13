package model;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import net.APIcommunication;
import net.ServerCommunication;
import utils.AgentAction;
import utils.FeaturesItem;
import utils.FeaturesSnake;
import utils.ItemType;
import utils.NoFeaturesItemException;
import utils.PlayerAgentStrategy;
import utils.Position;
import utils.SnakeFactory;
import utils.SnakeType;
import utils.Snake;

// Snake game
public class SnakeGame extends Game
{
    private static final int effectsDuration = 20;

    private static final ItemType[] bonusItemTypes = {ItemType.BOX, ItemType.SICK_BALL, ItemType.INVINCIBILITY_BALL};
    private static final ItemType[] boxItemTypes = {ItemType.SICK_BALL, ItemType.INVINCIBILITY_BALL};
    private static final double bonusItemProbability = 0.5;
    private static final Random random = new Random();

    public static SnakeGame join(ServerCommunication player, String userName, int id)
    {
        SnakeGame snakeGame = (SnakeGame) games.get(id);
        snakeGame.otherPlayers.add(player);
        player.send("show lobby", id, snakeGame.inputMap, snakeGame.snakes.size() - snakeGame.otherPlayers.size());
        snakeGame.broadcast("joined lobby", userName);
        return snakeGame;
    }

    private InputMap inputMap;

    private ArrayList<Snake> snakes;
    private ArrayList<FeaturesItem> featuresItems;
    private ArrayList<Snake> snakePlayers;

    private Map<ServerCommunication, Integer> playersRank;

    // Construct a snake game
    public SnakeGame(ServerCommunication creator, int maxTurn, InputMap inputMap)
    {
        super(creator, maxTurn);
        this.inputMap = inputMap;
        playersRank = new HashMap<>();
        init();
    }

    public SnakeGame(SnakeGame snakeGame)
    {
        super(snakeGame.creator, snakeGame.maxTurn);
        inputMap = snakeGame.inputMap;
        
        // Init
        turn = snakeGame.turn;
        isRunning = snakeGame.isRunning;
        
        // InitializeGame
        snakes = new ArrayList<>();
        SnakeFactory snakeFactory = new SnakeFactory(this);
        for (Snake snake: snakeGame.snakes)
            snakes.add(snakeFactory.createFromBase(snake));
        featuresItems = new ArrayList<>();
        for (FeaturesItem featuresItem: snakeGame.featuresItems)
            featuresItems.add(new FeaturesItem(featuresItem.getPosition(), featuresItem.getItemType()));
    }

    public ArrayList<FeaturesItem> getFeaturesItems()
    {
        return featuresItems;
    }

    public InputMap getInputMap()
    {
        return inputMap;
    }

    public ArrayList<Snake> getSnakes()
    {
        return snakes;
    }

    // The creator controls the first snake whereas the other players control what follows
    private int snakeIndex(ServerCommunication player)
        { return player == creator ? 0 : 1 + otherPlayers.indexOf(player); }
    private ServerCommunication player(int snakeIndex)
        { return snakeIndex == 0 ? creator : otherPlayers.get(snakeIndex - 1); }

    // Initialize the level
    @Override
    protected void initializeGame()
    {
        // Clone featuresSnakes from input map and initialize snakesEffectDuration
        snakes = new ArrayList<>();
        snakePlayers = new ArrayList<>();
        SnakeFactory snakeFactory = new SnakeFactory(this);
        for (FeaturesSnake featuresSnake: inputMap.getStart_snakes())
        {
            // Create the snake
            Snake snake = snakeFactory.createFromBase(featuresSnake);

            // Save players
            if (featuresSnake.getSnakeType() == SnakeType.PLAYER)
                snakePlayers.add(snake);

            // Store the snake
            snakes.add(snake);
        }

        // Clone featuresItems from input map
        featuresItems = new ArrayList<>();
        for (FeaturesItem featuresItem: inputMap.getStart_items())
            featuresItems.add(featuresItem);
    }

    @Override
    public boolean gameContinue()
    {
        // The game continue while there is still alive players
        return playersRank.size() < otherPlayers.size() + 1;
    }

    @Override
    public void takeTurn()
    {
        // Move each snake
        for (Snake snake: snakes)
        {
            // Strategy gives the next action
            AgentAction nextAction = snake.getStrategy().nextAction();

            // Use featuresItem at the next snake head position
            snake.setGrowing(false);
            Position headPosition = snake.getPositions().get(0);
            Position nextHeadPosition = headPosition.move(nextAction, inputMap.getSize());
            try { effect(useFeaturesItemAt(nextHeadPosition, snake), snake); }
            catch (NoFeaturesItemException illegalArgumentException){}

            // Move the snake
            snake.move(nextAction, inputMap.getSize());
        }

        // Record dead snakes
        ArrayList<Snake> deadSnakes = new ArrayList<Snake>();
        for (Snake snake: snakes)
            if (isDead(snake))
                deadSnakes.add(snake);

        // Remove dead enemy snakes and finish the game if the player died
        for (Snake deadSnake: deadSnakes)
        {
            if (deadSnake.getSnakeType() == SnakeType.PLAYER)
                playersRank.put(player(snakePlayers.indexOf(deadSnake)), snakePlayers.size());

            snakes.remove(deadSnake);
        }

        // Update the game
        update();
    }

    @Override
    public void gameOver()
    {
        // Save game results with the API
        int playerCount = otherPlayers.size() + 1;
        APIcommunication.Response apiResponse = APIcommunication.saveGame(new Date(), inputMap.getFilename(), playerCount, inputMap.getStart_snakes().size() - playerCount);
        switch (apiResponse.getCode())
        {
            // The game has been saved
            case HttpURLConnection.HTTP_CREATED:
                for (Map.Entry<ServerCommunication, Integer> playerRank: playersRank.entrySet())
                    APIcommunication.saveGameResult(apiResponse.getBody().get("gameId").asLong(), playerRank.getKey().getUserName() , playerRank.getValue());
            break;

            // The game has not been saved because the request is syntactically incorrect
            case HttpURLConnection.HTTP_BAD_REQUEST:
            break;

            // The game has not been saved because  the game server is not authorized to access the API
            case HttpURLConnection.HTTP_UNAUTHORIZED:
            break;
        }
    }

    @Override
    public void update() 
    {
        ArrayList<FeaturesSnake> featuresSnakes = new ArrayList<>();
        for (Snake snake: snakes)
            featuresSnakes.add(SnakeFactory.createFromSub(snake));
        broadcast("update game", featuresSnakes, featuresItems);
    }

    public void nextPlayerAgentAction(AgentAction nextPlayerAgentAction, ServerCommunication player)
    {
        ((PlayerAgentStrategy) snakePlayers.get(snakeIndex(player)).getStrategy()).setNextAction(nextPlayerAgentAction);
    }

    /**
     * @param snake The current snake
     * @return True if the given snake hit a wall
     */
    private boolean hitWall(Snake snake)
    {
        // The snake head will hit a wall and is not invisible
        Position headPosition = snake.getPositions().get(0);
        return inputMap.getWalls()[headPosition.getX()][headPosition.getY()] && !snake.isInvincible();
    }

    /**
     * @param snake The current snake
     * @return True if the given snake is eaten by a snake
     */
    private boolean isEaten(Snake snake)
    {
        // An invisible snake cannot be eaten
        if (snake.isInvincible())
            return false;

        // For each other snake
        int snakeSize = snake.getPositions().size();
        for (Snake otherSnake: snakes)
        {
            // The other snake is the same size or bigger
            int otherSnakeSize = otherSnake.getPositions().size();
            if (snakeSize <= otherSnakeSize)
            {
                // For each current snake position
                for (int snakePositionIndex = 0; snakePositionIndex < snakeSize; ++snakePositionIndex)
                {
                    // A head cannot hit itself
                    if (snake == otherSnake && snakePositionIndex == 0)
                        continue;

                    // The other snake head is on the current snake
                    Position otherSnakeHeadPosition = otherSnake.getPositions().get(0);
                    Position snakePosition = snake.getPositions().get(snakePositionIndex);
                    if (snakePosition.equals(otherSnakeHeadPosition))
                        return true;
                }
            }
        }

        // The current snake is not eaten
        return false;
    }

    /**
     * @param snake The current snake
     * @return True if the given snake has crashed into a snake
     */
    private boolean hasCrashed(Snake snake)
    {
        // An invisible snake cannot be crashed
        if (snake.isInvincible())
            return false;

        // For each other snake
        int snakeSize = snake.getPositions().size();
        for (Snake otherSnake: snakes)
        {
            // The other snake is the same size or bigger
            int otherSnakeSize = otherSnake.getPositions().size();
            if (snakeSize <= otherSnakeSize)
            {
                // For each current snake position
                for (int otherSnakePositionIndex = 0; otherSnakePositionIndex < otherSnakeSize; ++otherSnakePositionIndex)
                {
                    // A head cannot hit itself
                    if (snake == otherSnake && otherSnakePositionIndex == 0)
                        continue;

                    // The other snake head is on the current snake
                    Position snakeHeadPosition = snake.getPositions().get(0);
                    Position otherSnakePosition = otherSnake.getPositions().get(otherSnakePositionIndex);
                    if (otherSnakePosition.equals(snakeHeadPosition))
                        return true;
                }
            }
        }

        // The current snake has not crashed
        return false;
    }

    /**
     * @param snake The current snake
     * @return True if the given snake is dead (has hit a wall, is eaten or has crashed)
     */
    private boolean isDead(Snake snake)
    {
        return hitWall(snake) || isEaten(snake) || hasCrashed(snake);
    }

    // Return true if the snake action will kill it
    public boolean isLegalMove(Snake snake, AgentAction agentAction)
    {
        // The snake cannot return itself if it has a body
        if (snake.getPositions().size() > 1 && agentAction.isOpposite(snake.getLastAction()))
            return false;

        // Move temporarily the snake to define if the move is legal
        snake.move(agentAction, inputMap.getSize());
        
        // The snake will die
        boolean isLegal = true;
        if (isDead(snake))
            isLegal = false;

        // Undo the snake movement
        snake.undoLastMove();

        return isLegal;
    }

    /**
     * @param position The position where the featuresItem should be
     * @return The itemType of the used featuresItem at the given position
     */
    private ItemType useFeaturesItemAt(Position position, Snake snake) throws NoFeaturesItemException
    {
        // Snakes cannot use a featuresItem while being sick
        if (snake.isSick())
            throw new NoFeaturesItemException();

        // Returns the used featuresItem
        for (FeaturesItem featuresItem: featuresItems)
        {
            if (position.equals(featuresItem.getPosition()))
            {
                featuresItems.remove(featuresItem);
                return featuresItem.getItemType();
            }
        }
        
        // There is no featuresItem at the given position
        throw new NoFeaturesItemException();
    }

    private ArrayList<Position> freePositions()
    {
        ArrayList<Position> freePositions = new ArrayList<Position>();
        for (int x = 0; x < inputMap.getSize().getX(); ++x)
        {
            for (int y = 0; y < inputMap.getSize().getY(); ++y)
            {
                if (!inputMap.getWalls()[x][y])
                {
                    boolean freePosition = true;
                    for (FeaturesItem featuresItem: featuresItems)
                    {
                        if (featuresItem.getPosition().getX() == x && featuresItem.getPosition().getY() == y)
                        {
                            freePosition = false;
                            break;
                        }
                    }

                    if (freePosition)
                    {
                        for (Snake snake: snakes)
                        {
                            for (Position snakePosition: snake.getPositions())
                            {
                                if (snakePosition.getX() == x && snakePosition.getY() == y)
                                {
                                    freePosition = false;
                                    break;
                                }
                            }

                            if (!freePosition)
                                break;
                        }

                        if (freePosition)
                            freePositions.add(new Position(x, y));
                    }
                }
            }
        }

        return freePositions;
    }

    // Add an item at a random position on the game
    private void addItem(ItemType itemType)
    {
         // There is at least one free position
         ArrayList<Position> freePositions = freePositions();
         if (freePositions.size() > 0)
         {
             // Add a bonus item to the game
             int freePositionsRandomIndex = random.nextInt(freePositions.size());
             Position itemPosition = freePositions.get(freePositionsRandomIndex);
             FeaturesItem bonusItem = new FeaturesItem(itemPosition, itemType);
             featuresItems.add(bonusItem);
         }
    }

    private void effect(ItemType itemType, Snake snake)
    {
        switch (itemType)
        {
            // The used featuresItem was an apple
            case APPLE:

                // The apple makes the current snake grow
                snake.setGrowing(true);

                // Add a bonus item to the game
                addItem(ItemType.APPLE);

                // The game will spawn a bonus item
                if (random.nextDouble() <= bonusItemProbability)
                {
                    // Add a bonus item to the game
                    int bonusItemTypesRandomIndex = random.nextInt(bonusItemTypes.length);
                    ItemType bonusItemType = bonusItemTypes[bonusItemTypesRandomIndex];
                    addItem(bonusItemType);
                }
            break;

            // The used featuresItem was a box
            case BOX:
                int boxItemTypesRandomIndex = random.nextInt(boxItemTypes.length);
                ItemType boxItemType = boxItemTypes[boxItemTypesRandomIndex];
                effect(boxItemType, snake);
            break;

            // The used featuresItem was a sick ball
            case SICK_BALL:

                // The snake cannot superpose effects
                if (snake.getEffectDuration() == 0)
                {
                    // The snake is sick for a determined amount of turns         
                    snake.setSick(true);
                    snake.setEffectDuration(effectsDuration);
                }
            break;

            // The used featuresItem was an invincibility ball
            case INVINCIBILITY_BALL:

                // The snake cannot superpose effects
                if (snake.getEffectDuration() == 0)
                {
                    // The snake is invisible for a determined amount of turns
                    snake.setInvincible(true);
                    snake.setEffectDuration(effectsDuration);
                }
            break;
        }
    }

    // Test a turn on which only one snake play with a given action
    // Useful for AIs like A*
    public void testTurn(Snake snake, AgentAction nextAgentAction)
    {
        // Use featuresItem at the next snake head position
        snake.setGrowing(false);
        Position headPosition = snake.getPositions().get(0);
        Position nextHeadPosition = headPosition.move(nextAgentAction, inputMap.getSize());
        try { effect(useFeaturesItemAt(nextHeadPosition, snake), snake); }
        catch (NoFeaturesItemException illegalArgumentException){}

        // Move the snake
        snake.move(nextAgentAction, inputMap.getSize());

        // Record dead snakes
        ArrayList<Snake> deadSnakes = new ArrayList<Snake>();
        for (Snake otherSnake: snakes)
            if (isDead(otherSnake))
                deadSnakes.add(otherSnake);

        // Remove dead snakes
        for (Snake deadSnake: deadSnakes)
            snakes.remove(deadSnake);
    }
}