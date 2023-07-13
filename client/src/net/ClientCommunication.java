package net;

import java.net.Socket;
import java.util.ArrayList;

import com.fasterxml.jackson.core.type.TypeReference;

import controller.SnakeGameController;
import model.InputMap;
import utils.FeaturesItem;
import utils.FeaturesSnake;

public final class ClientCommunication extends Communication
{
    private SnakeGameController controllerSnakeGame;

	public ClientCommunication(Socket serverSocket) 
    {
		super(serverSocket);
	}

    public void interactsWith(SnakeGameController controllerSnakeGame)
    {
        this.controllerSnakeGame = controllerSnakeGame;
    }

	@Override
	protected void process(Message message)
    {
        switch (message.getKey())
        {
            case "sign in error":
            {
                controllerSnakeGame.signInError(message.getValue(0, new TypeReference<String>(){}));
            } break;
            case "signed in":
            {
                controllerSnakeGame.signedIn();
            } break;
            case "single player game created":
            {
                controllerSnakeGame.playLevel(message.getValue(0, new TypeReference<InputMap>(){}));
            } break;
            case "lobby created":
            {
                controllerSnakeGame.lobbyCreated
                (
                    message.getValue(0, new TypeReference<Integer>(){}), 
                    message.getValue(1, new TypeReference<InputMap>(){})
                );
            } break;
            case "new chat lobby message":
            {
                controllerSnakeGame.appendToLobby
                (
                    message.getValue(0, new TypeReference<String>(){}),
                    message.getValue(1, new TypeReference<String>(){})
                );
            } break;
            case "show lobby":
            {
                controllerSnakeGame.showLobby
                (
                    message.getValue(0, new TypeReference<Integer>(){}),
                    message.getValue(1, new TypeReference<InputMap>(){}),
                    message.getValue(2, new TypeReference<Integer>(){})
                );
            } break;
            case "joined lobby":
            {
                controllerSnakeGame.joinedLobby(message.getValue(0, new TypeReference<String>(){}));
            } break;
            case "update game":
            {
                controllerSnakeGame.getPlayingLevel().update
                (
                    message.getValue(0, new TypeReference<ArrayList<FeaturesSnake>>(){}),
                    message.getValue(1, new TypeReference<ArrayList<FeaturesItem>>(){})
                );
            } break;
        }
	}
}