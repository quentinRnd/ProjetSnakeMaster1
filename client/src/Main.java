import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import controller.SnakeGameController;
import net.ClientCommunication;

public class Main
{
    private static final int port = 26847;

    private static final Logger logger = LogManager.getLogger();

    public static void main(String[] arguments) 
    {
        // Connect the socket to the server
        try (Socket serverSocket = new Socket("localhost", port))
        {
            // Create a client communication with the server
            ClientCommunication clientCommunication = new ClientCommunication(serverSocket);

            // Create a snake game controller
            SnakeGameController snakeGameController = new SnakeGameController(clientCommunication);

            // The the client communication interacts with a snake game controller
            clientCommunication.interactsWith(snakeGameController);

            // Start the client reception loop on the main thread
            clientCommunication.receptionLoop();
        }
        catch (UnknownHostException unknownHostException) { logger.error("Host unknown"); }
        catch (ConnectException connectException) { logger.error("Host not reachable"); }
        catch (IOException ioException) { logger.error("", ioException) ;}
    }
}