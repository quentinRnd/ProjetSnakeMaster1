package utils;

import java.util.ArrayList;

// Snake with a strategy
public class Snake extends FeaturesSnake
{
    // Play heuristic
    private AgentStrategy strategy;

    // The snake is growing
    private boolean isGrowing;

    // Duration of the current effect in number of turns
    private int effectDuration;

    // Save snake values before its last move
    private ArrayList<Position> undoPositions;
    private AgentAction undoLastAction;
    private int undoEffectDuration;
    private boolean undoIsInvincible;
    private boolean undoIsSick;

    private void setUndo()
    {
        undoPositions = positions;
        undoLastAction = lastAction;
        undoEffectDuration = effectDuration;
        undoIsInvincible = isInvincible;
        undoIsSick = isSick;
    }

    public Snake(ArrayList<Position> positions, AgentAction lastAction, SnakeType snakeType, boolean isInvincible, boolean isSick)
    {
        super(positions, lastAction, snakeType, isInvincible, isSick);
        isGrowing = false;
        effectDuration = 0;
        setUndo();
    }

    public AgentStrategy getStrategy()
    {
        return strategy;
    }

    public void setStrategy(AgentStrategy strategy)
    {
        this.strategy = strategy;
    }

    public boolean isGrowing()
    {
        return isGrowing;
    }

    public void setGrowing(boolean isGrowing)
    {
        this.isGrowing = isGrowing;
    }

    public int getEffectDuration()
    {
        return effectDuration;
    }

    public void setEffectDuration(int effectDuration)
    {
        this.effectDuration = effectDuration;
    }

    // Move a snake by an agent action
    public void move(AgentAction agentAction, Position untilValue)
    {
        setUndo();

        Position headPosition = positions.get(0);
        Position nextPosition = headPosition.move(agentAction, untilValue);
 
        ArrayList<Position> nextPositions = new ArrayList<Position>();
        for (Position snakePosition: positions)
        {
            nextPositions.add(nextPosition);
            nextPosition = snakePosition;
        }
 
        if (isGrowing)
            nextPositions.add(nextPosition);
         
        positions = nextPositions;
        lastAction = agentAction;

         // Decrement effect duration or clear all effects
         if (effectDuration > 0)
             --effectDuration;
         else
         {
             isInvincible = false;
             isSick = false;
         }
    }

    // Undo the last move performed
    public void undoLastMove()
    {
        positions = undoPositions;
        lastAction = undoLastAction;
        effectDuration = undoEffectDuration;
        isInvincible = undoIsInvincible;
        isSick = undoIsSick;
    }
}
