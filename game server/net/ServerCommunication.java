package net;

import java.net.HttpURLConnection;
import java.net.Socket;

import com.fasterxml.jackson.core.type.TypeReference;

import model.InputMap;
import model.SnakeGame;
import utils.AgentAction;

public final class ServerCommunication extends Communication
{
    private String userName;
    private SnakeGame snakeGame;

	public ServerCommunication(Socket clientSocket) 
    {
		super(clientSocket);
	}

	@Override
	protected void process(Message message)
    {
        switch (message.getKey())
        {
            case "sign in":
            {
                userName = message.getValue(0, new TypeReference<String>(){});
                String password = message.getValue(1, new TypeReference<String>(){});

                // Sign in to the API
                APIcommunication.Response apiResponse = APIcommunication.signIn(userName, password);

                switch (apiResponse.getCode())
                {
                    // Successfully signed in
                    case HttpURLConnection.HTTP_OK:
                        send("signed in");
                    break;

                    // User not found
                    case HttpURLConnection.HTTP_NOT_FOUND:
                        send("sign in error", String.format("User \"%s\" does not exists", userName));
                    break;

                    // Invalid password
                    case HttpURLConnection.HTTP_FORBIDDEN:
                        send("sign in error", "Invalid password");
                    break;

                    // Server down
                    // Hide to the client the fact that the game server is not authorized to access the API
                    case HttpURLConnection.HTTP_UNAUTHORIZED:
                        send("sign in error", "Server down");
                    break;
                }
            }
            break;
            case "create single player game":
            {
                // Create the input map related to the layout map
                InputMap inputMap = new InputMap
                (
                        "model/layouts/single_player/"
                    +   (message.getValue(0, new TypeReference<String>(){}).equals("none") ? "no-AI" : (message.getValue(0, new TypeReference<String>(){})))
                    +   "_" + (message.getValue(1, new TypeReference<Boolean>(){}) ? "walls" : "no-walls")
                    +   ".lay"
                );
            
                // Create and run the game
                snakeGame = new SnakeGame(this, 10000, inputMap);
                send("single player game created", inputMap);
            } break;
            case "create lobby":
            {
                // Create the input map related to the given information
                InputMap inputMap = new InputMap
                (
                        "model/layouts/multi_player/"
                    +   message.getValue(0, new TypeReference<Integer>(){})
                    +   "_" + (message.getValue(1, new TypeReference<String>(){}).equals("none") ? "no-AI" : (message.getValue(1, new TypeReference<String>(){})))
                    +   "_" + (message.getValue(2, new TypeReference<Boolean>(){}) ? "walls" : "no-walls")
                    +   ".lay"
                );
            
                // Create the game
                snakeGame = new SnakeGame(this, 10000, inputMap);
                send("lobby created", snakeGame.getId(), inputMap);
            } break;
            case "broadcast chat lobby message":
            {
                snakeGame.broadcast("new chat lobby message", userName, message.getValue(0, new TypeReference<String>(){}));
            } break;
            case "join lobby":
            {
                snakeGame = SnakeGame.join(this, userName, message.getValue(0, new TypeReference<Integer>(){}));
            } break;
            case "next player agent action":
            {
                snakeGame.nextPlayerAgentAction(message.getValue(0, new TypeReference<AgentAction>(){}), this);
            } break;
            case "play":
            {
                snakeGame.launch();
            } break;
        }
	}

    public String getUserName()
    {
        return userName;
    }
}