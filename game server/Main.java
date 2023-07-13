import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;

import net.ClientListener;

public class Main
{
    private static final Logger logger = LogManager.getLogger();
    private static final int port = 26847;

    public static void main(String[] arguments) 
    {
        try (ServerSocket serverSocket = new ServerSocket(port))
        {
            System.out.printf("Server socket opened on port %s\n", serverSocket.getLocalPort());
            for (;;)
            {
                // Wait for a connection with a client
                Socket clientSocket = serverSocket.accept();

                // Start a new client listener in parallel
                new ClientListener(clientSocket).start();
            }
        }
        catch (IOException ioException) { logger.error("", ioException); }
    }
}