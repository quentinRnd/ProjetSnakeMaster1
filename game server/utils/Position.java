package utils;

public class Position
{
	private int x;
	private int y;

	public Position(){}

	public Position(int x, int y) 
	{
		this.x = x;
		this.y = y;
	}
	
	public int getX() 
	{
		return x;
	}

	public void setX(int x) 
	{
		this.x = x;
	}

	public int getY() 
	{
		return y;
	}

	public void setY(int y) 
	{
		this.y = y;
	}

	// Move to the next modular position provided the action and the boundaries (dimensions of the tor)
	public Position move(AgentAction agentAction, Position untilValue)
    {
		Position offsetPosition;
        switch (agentAction)
        {
            case MOVE_UP:
				offsetPosition = new Position(0, untilValue.getY() - 1);
            break;
            case MOVE_DOWN:
				offsetPosition = new Position(0, 1);
            break;
            case MOVE_LEFT:
				offsetPosition = new Position(untilValue.getX() -  1, 0);
            break;
            case MOVE_RIGHT:
            default:
				offsetPosition = new Position(1, 0);
            break;
        }

        return new Position
        (
            (x + offsetPosition.getX()) % untilValue.getX(), 
            (y + offsetPosition.getY()) % untilValue.getY()
        );
    }

	// Return the number of cells a snake should travel to go from one position to another
	public int manhatanDistance(Position position)
	{
		Position distances = new Position
        (
            Math.abs(x - position.getX()),
            Math.abs(y - position.getY())
        );
        return distances.getX() + distances.getY();
	}

	@Override
	public boolean equals(Object object)
    {
		if (object instanceof Position)
		{
			Position position = (Position) object;
        	return x == position.x && y == position.y;
		}
		else
			return false;
    }

	@Override
	public String toString()
	{
		return String.format("(%d, %d)", x, y);
	}
}