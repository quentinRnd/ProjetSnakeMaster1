package utils;

import java.io.Serializable;

public class FeaturesItem implements Serializable
{
	private Position position;
	private ItemType itemType;
	
	public FeaturesItem(){}

	public FeaturesItem(Position position, ItemType itemType) 
	{
		this.position = position;
		this.itemType = itemType;
	}
	
	public Position getPosition() 
	{
		return position;
	}

	public void setPosition(Position position) 
	{
		this.position = position;
	}

	public ItemType getItemType() 
	{
		return itemType;
	}

	public void setItemType(ItemType itemType) 
	{
		this.itemType = itemType;
	}

	@Override
	public String toString()
	{
		return String.format("%s: %s", itemType, position);
	}
}