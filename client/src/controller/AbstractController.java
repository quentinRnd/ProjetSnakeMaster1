package controller;

import net.Communication;

// Abstract game controller
public abstract class AbstractController 
{
    protected Communication communication;

    public AbstractController(Communication communication)
    {
        this.communication = communication;
    }

    public abstract void keyEvent(int keyEventCode);
}