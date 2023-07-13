package view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;

import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.ArrayList;
import javax.swing.JPanel;

import utils.*;

// Show the game panel with a map and a list of positional agents
public class SnakeGamePanel extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	protected Color groundColor= new Color(0,0,0);

	private int sizeX;
	private int sizeY;

	private int fenX;
	private int fenY;
	
	private double stepX;
	private double stepY;
		
	float[] contrast = { 0, 0, 0, 1.0f };

	protected ArrayList<FeaturesSnake> featuresSnakes = new ArrayList<FeaturesSnake>();	
	protected ArrayList<FeaturesItem> featuresItems = new ArrayList<FeaturesItem>();

	private boolean[][] walls;
	
	int cpt;

	public SnakeGamePanel(int sizeX, int sizeY, boolean[][] walls, ArrayList<FeaturesSnake> featuresSnakes, ArrayList<FeaturesItem> featuresItems) 
	{
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.walls = walls;	
		this.featuresSnakes = featuresSnakes;
		this.featuresItems = featuresItems;
	}

	@Override
	public void paint(Graphics g) 
	{
		fenX = getSize().width;
		fenY = getSize().height;

		this.stepX = fenX / (double)sizeX;
		this.stepY = fenY / (double)sizeY;
		
		g.setColor(groundColor);
		g.fillRect(0, 0, fenX, fenY);

		double positionX = 0;

		for (int x = 0; x < sizeX; x++)
		{
			double positionY = 0 ;
			
			for (int y = 0; y < sizeY; y++)
			{
				if (walls[x][y]){

					try {
						Image img = ImageIO.read(new File("view/images/wall.png"));
						g.drawImage(img, (int)positionX, (int)positionY, (int)stepX, (int)stepY, this);
						
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				positionY += stepY;				
			}
			positionX += stepX;
		}

		for(int i = 0; i < featuresSnakes.size(); i++){
			paint_Snake(g, featuresSnakes.get(i));	
		}

		for(int i = 0; i < featuresItems.size(); i++){
			paint_Item(g, featuresItems.get(i));	
		}
			
		cpt++;
	}

	void paint_Snake(Graphics g, FeaturesSnake featuresSnake)
	{
		ArrayList<Position> positions = featuresSnake.getPositions();
		
		AgentAction lastAction = featuresSnake.getLastAction();

		BufferedImage img = null;

		double posX;
		double posY;
		
		int imageCount = -1;
		
		for (int i = 0; i < positions.size(); i++) {
			
			posX = positions.get(i).getX() * stepX;
			posY = positions.get(i).getY() * stepY;

			// Head
			if (i == 0) 
			{
				// Single head
				if (positions.size() == 1)
				{
					switch (lastAction) 
					{
						case MOVE_UP:
							imageCount = 0;
						break;
						case MOVE_DOWN:
							imageCount = 1;	
						break;
						case MOVE_RIGHT:
							imageCount = 2;
						break;		
						case MOVE_LEFT:
							imageCount = 3;
						break;
					}
				}

				// The head is connected to a body
				else
				{
					switch (lastAction) 
					{
						case MOVE_UP:
							imageCount = 4;
						break;
						case MOVE_DOWN:
							imageCount = 5;	
						break;
						case MOVE_RIGHT:
							imageCount = 6;
						break;		
						case MOVE_LEFT:
							imageCount = 7;
						break;
					}
				}
			} 
			
			// Body
			else 
			{
				// Tail
				if (i + 1 >= positions.size())
				{
					// Horizontal
					if (positions.get(i).getY() == positions.get(i-1).getY())
					{
						// Right
						if (positions.get(i).getX() < positions.get(i-1).getX())
							imageCount = 16;

						// Left
						else
							imageCount = 17;
					}

					// Vertical
					else
					{
						// Down
						if (positions.get(i).getY() < positions.get(i-1).getY())
							imageCount = 15;

						// Up
						else
							imageCount = 14;
					}
				}

				// Middle body
				else
				{
					// First part is up
					if (positions.get(i-1).getY() < positions.get(i).getY())
					{
						// Second part is right
						if (positions.get(i).getX() < positions.get(i+1).getX())
							imageCount = 10;

						// Second part is down
						else if ((positions.get(i).getX() == positions.get(i+1).getX()))
							imageCount = 9;

						// Second part is left
						else
							imageCount = 13;
					}

					// First part is horizontal
					else if (positions.get(i-1).getY() == positions.get(i).getY())
					{
						// First part is left
						if (positions.get(i-1).getX() < positions.get(i).getX())
						{
							// Second part is bottom
							if (positions.get(i).getY() < positions.get(i+1).getY())
								imageCount = 12;

							// Second part is left
							else if (positions.get(i).getY() == positions.get(i+1).getY())
								imageCount = 8;

							// Second part is up
							else
								imageCount = 13;
						}

						// First part is right
						else 
						{
							// Second part is bottom
							if (positions.get(i).getY() < positions.get(i+1).getY())
								imageCount = 11;

							// Second part is left
							else if (positions.get(i).getY() == positions.get(i+1).getY())
								imageCount = 8;

							// Second part is up
							else
								imageCount = 10;
						}

					}

					// First part is down
					else
					{
						// Second part is right
						if (positions.get(i).getX() < positions.get(i+1).getX())
							imageCount = 11;

						// Second part is down
						else if ((positions.get(i).getX() == positions.get(i+1).getX()))
							imageCount = 9;

						// Second part is left
						else
							imageCount = 12;
					}
				}
			}

			// The snake color depend of the type
			String snakeColor;
			switch (featuresSnake.getSnakeType())
			{
				case PLAYER:
					snakeColor = "green";
				break;

				case RANDOM:
					snakeColor = "yellow";
				break;

				case ONE_STEP_AHEAD:
					snakeColor = "orange";
				break;

				default:
					snakeColor = "red";
				break;
			}

			try { img = ImageIO.read(new File("view/images/snake_" + snakeColor + "_" + imageCount + ".png")); }
			catch (IOException e) { e.printStackTrace(); }
			
			float [] scales = new float[]{1 ,1, 1, 1.0f };
			
			if (featuresSnake.isInvincible())
				scales = new float[]{3 ,0.75f, 3, 1.0f };
	
			if (featuresSnake.isSick())
				scales = new float[]{1.5f ,1.5f, 0.75f, 1.0f };
			
			RescaleOp op = new RescaleOp(scales, contrast, null);
			img = op.filter(img, null);
			
			if(img != null) 
				g.drawImage(img, (int)posX, (int)posY, (int)stepX, (int)stepY, this);
		}
	}

	void paint_Item(Graphics g, FeaturesItem featuresItem)
	{
		Position p = featuresItem.getPosition();

		double pos_x = p.getX() * stepX;
		double pos_y = p.getY() * stepY;

		if (featuresItem.getItemType() == ItemType.APPLE) 
		{
			try 
			{
				Image img = ImageIO.read(new File("view/images/apple.png"));
				g.drawImage(img, (int)pos_x, (int)pos_y, (int)stepX, (int)stepY, this);
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}

		if (featuresItem.getItemType() == ItemType.BOX) 
		{
			try 
			{
				Image img = ImageIO.read(new File("view/images/mysteryBox.png"));
				g.drawImage(img, (int)pos_x, (int)pos_y, (int)stepX, (int)stepY, this);
			} catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
		
		if (featuresItem.getItemType() == ItemType.SICK_BALL) 
		{
			try 
			{
				Image img = ImageIO.read(new File("view/images/sickBall.png"));
				g.drawImage(img, (int)pos_x, (int)pos_y, (int)stepX, (int)stepY, this);
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
		
		if (featuresItem.getItemType() == ItemType.INVINCIBILITY_BALL) 
		{
			try 
			{
				Image img = ImageIO.read(new File("view/images/invincibleBall.png"));
				g.drawImage(img, (int)pos_x, (int)pos_y, (int)stepX, (int)stepY, this);
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
	}

	public void updateInfoGame(ArrayList<FeaturesSnake> featuresSnakes, ArrayList<FeaturesItem> featuresItems) 
	{
		this.featuresSnakes = featuresSnakes;
		this.featuresItems = featuresItems;
	}

	public int getSizeX() 
	{
		return sizeX;
	}

	public int getSizeY() 
	{
		return sizeY;
	}
}