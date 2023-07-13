package model;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import utils.AgentAction;
import utils.FeaturesItem;
import utils.FeaturesSnake;
import utils.ItemType;
import utils.Position;
import utils.SnakeType;

public class InputMap
{
	protected static final Logger logger = LogManager.getLogger();

	private String filename;
	private Position size;
	
	private boolean walls[][];

	private ArrayList<FeaturesSnake> start_snakes;
	private ArrayList<FeaturesItem> start_items;
	
	private BufferedReader buffer;
	
	public InputMap(){}

	public InputMap(String filename)
	{	
		this.filename = filename;
		
		try
		{
			InputStream flux = new FileInputStream(filename); 
			InputStreamReader lecture =new InputStreamReader(flux);
			buffer = new BufferedReader(lecture);
			
			String line;

			int nbX = 0;
			int nbY = 0;

			while ((line = buffer.readLine())!=null)
			{
				line = line.trim();
				if (nbX==0) {nbX = line.length();}
				else if (nbX != line.length()) throw new Exception("Toutes les lignes doivent avoir la même longueur");
				nbY++;
			}			
			buffer.close(); 

			size = new Position(nbX, nbY);

			walls = new boolean [size.getX()][size.getY()];
		
			flux = new FileInputStream(filename); 
			lecture = new InputStreamReader(flux);
			buffer = new BufferedReader(lecture);
			int y=0;
		
			start_snakes = new ArrayList<FeaturesSnake>();
			start_items = new ArrayList<FeaturesItem>();

			while ((line = buffer.readLine()) != null)
			{
				line = line.trim();

				for(int x = 0; x < line.length(); x++)
				{
					if (line.charAt(x) == '%')
						walls[x][y] = true; 

					else walls[x][y] = false;

					// Playable snake
					if (line.charAt(x) == 'P') 
					{
						ArrayList<Position> pos = new ArrayList<Position>();
						pos.add(new Position(x, y));

						start_snakes.add(new FeaturesSnake(pos, AgentAction.MOVE_DOWN, SnakeType.PLAYER, false, false));	
					}

					// Random snake
					else if (line.charAt(x) == 'R') 
					{
						ArrayList<Position> pos = new ArrayList<Position>();
						pos.add(new Position(x, y));

						start_snakes.add(new FeaturesSnake(pos, AgentAction.MOVE_DOWN, SnakeType.RANDOM, false, false));
					}

					// One step ahead snake
					else if (line.charAt(x) == 'O') 
					{
						ArrayList<Position> pos = new ArrayList<Position>();
						pos.add(new Position(x, y));

						start_snakes.add(new FeaturesSnake(pos, AgentAction.MOVE_DOWN, SnakeType.ONE_STEP_AHEAD, false, false));
					}

					// A* stake
					else if (line.charAt(x) == 'S') 
					{
						ArrayList<Position> pos = new ArrayList<Position>();
						pos.add(new Position(x, y));

						start_snakes.add(new FeaturesSnake(pos, AgentAction.MOVE_DOWN, SnakeType.A_STAR, false, false));
					}

					else if (line.charAt(x) == 'A') 
					{
						start_items.add(new FeaturesItem(new Position(x,y), ItemType.APPLE));
					}

					else if (line.charAt(x) == 'B') 
					{
						start_items.add(new FeaturesItem(new Position(x,y), ItemType.BOX));
					}

					else if (line.charAt(x) == 'Y') 
					{
						start_items.add(new FeaturesItem(new Position(x,y), ItemType.SICK_BALL));
					}

					else if (line.charAt(x) == 'M') 
					{
						start_items.add(new FeaturesItem(new Position(x,y), ItemType.INVINCIBILITY_BALL));
					}	
				}
				y++;
			}	

			buffer.close();
		}
		catch (IOException ioException) { logger.error("", ioException); } 
		catch (Exception exception) { logger.error(exception.getMessage()); }
	}

	public Position getSize()							{ return size; 			}
	public String getFilename() 						{ return filename;		}
	public boolean[][] getWalls() 						{ return walls;			}
	public ArrayList<FeaturesSnake> getStart_snakes() 	{ return start_snakes; 	}
	public ArrayList<FeaturesItem> getStart_items() 	{ return start_items;	}
}