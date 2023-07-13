package model;

import java.util.ArrayList;

import utils.FeaturesItem;
import utils.FeaturesSnake;
import utils.Position;

public class InputMap
{
	private String filename;
	private Position size;
	
	private boolean walls[][];

	private ArrayList<FeaturesSnake> start_snakes;
	private ArrayList<FeaturesItem> start_items;
	
	public InputMap(){}

	public Position getSize()							{ return size; 			}
	public String getFilename() 						{ return filename;		}
	public boolean[][] getWalls() 						{ return walls;			}
	public ArrayList<FeaturesSnake> getStart_snakes() 	{ return start_snakes; 	}
	public ArrayList<FeaturesItem> getStart_items() 	{ return start_items;	}	
}