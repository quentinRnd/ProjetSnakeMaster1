package model;

import java.lang.Runnable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.ServerCommunication;

// Create an abstract game
public abstract class Game implements Runnable
{
    protected static final Logger logger = LogManager.getLogger();

    // Store all games with their corresponding Ids
    protected static final Map<Integer, Game> games = new HashMap<Integer, Game>();

    // Game identifier
    private int id;

    // Communications to clients of players in the game
    protected ServerCommunication creator;
    protected List<ServerCommunication> otherPlayers;

    // Number of turn of the game
    protected int turn;

    // Maximum number of turn of the game allowed
    protected int maxTurn;

    // Whether the game is running
    protected boolean isRunning;

    // Thread that run the game with launch method
    private Thread thread;

    // Time in milliseconds between each step of the game when the thread is launched
    private long time = 200;

    /**
     * Construct a game with a maximum of turns
     * @param maxTurn Maximum of turns
     */
    public Game(ServerCommunication creator, int maxTurn)
    {
        id = games.size();

        this.creator = creator;
        this.otherPlayers = new ArrayList<>();

        games.put(id, this);

        this.maxTurn = maxTurn;
    }

    // Abstract method to be implemented to initialize the rest of the game
    protected abstract void initializeGame();

    public final void broadcast(String key, Object... values)
    {
        creator.send(key, values);
        for (ServerCommunication otherPlayer: otherPlayers)
            otherPlayer.send(key, values);
    }

    // Initialize the game
    public void init()
    {
        turn = 0;
        isRunning = true;
        initializeGame();
    }

    // Return the id of the game
    public int getId()
    {
        return id;
    }

    /**
     * @return the game continue
     */
    public abstract boolean gameContinue();

    // Abstract method to be implemented to do a turn of the game
    public abstract void takeTurn();

    // Print a message of game over
    public abstract void gameOver();

    // Do a game step
    public void step()
    {
        if (gameContinue() && turn < maxTurn)
        {
            takeTurn();

            try { Thread.sleep(time); } 
            catch (Exception exception) { logger.error("", exception); }
        }

        else
        {
            isRunning = false;
            gameOver();
        }
    }

    // Put the game in pause
    public void pause()
    {
        isRunning = false;
    }
    
    // Run the game
    @Override
    public void run()
    {
        while (isRunning)
            step();
    }

    // Run the game on a thread
    public void launch()
    {
        isRunning = true;
        thread = new Thread(this);
        thread.start();
    }

    // Set the time between each steps
    public void setTime(long time)
    {
        this.time = time;
    }

    public abstract void update();
}