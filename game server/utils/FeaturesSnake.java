package utils;

import java.io.Serializable;
import java.util.ArrayList;

public class FeaturesSnake implements Serializable
{
	protected ArrayList<Position> positions;
	
	protected AgentAction lastAction;
	
	protected SnakeType snakeType;
	
	protected boolean isInvincible;
	protected boolean isSick;

	public FeaturesSnake(ArrayList<Position> positions, AgentAction lastAction, SnakeType snakeType, boolean isInvincible, boolean isSick) 
	{	
		this.positions = positions;
		this.lastAction = lastAction;
		this.snakeType = snakeType;
		this.isInvincible = isInvincible;
		this.isSick = isSick;
	}

	public ArrayList<Position> getPositions() 
	{
		return positions;
	}

	public void setPositions(ArrayList<Position> positions) 
	{
		this.positions = positions;
	}

	public SnakeType getSnakeType() 
	{
		return snakeType;
	}

	public void setSnakeType(SnakeType snakeType) 
	{
		this.snakeType = snakeType;
	}

	public boolean isInvincible() 
	{
		return isInvincible;
	}

	public void setInvincible(boolean isInvincible) 
	{
		this.isInvincible = isInvincible;
	}

	public boolean isSick() 
	{
		return isSick;
	}

	public void setSick(boolean isSick) 
	{
		this.isSick = isSick;
	}

	public AgentAction getLastAction() 
	{
		return lastAction;
	}

	public void setLastAction(AgentAction lastAction) 
	{
		this.lastAction = lastAction;
	}

	@Override
    public String toString()
    {
        String string = new String("Snake:\n- positions: ");
        for (Position position: positions)
            string += position.toString();
        string += String.format("\n- lastAction: %s", lastAction);
        string += String.format("\n- snakeType: %s", snakeType);
        string += String.format("\n- isInvincible: %b", isInvincible);
        string += String.format("\n- isSick: %b", isSick);

        return string;
    }
}