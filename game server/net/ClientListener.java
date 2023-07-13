package net;

import java.io.EOFException;
import java.net.Socket;

public class ClientListener extends Thread
{

    public Socket clientSocket;

    public ClientListener(Socket clientSocket)
    {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run()
    {
        // Create a server communication with the server and start reception loop
        System.out.println("New connection with a snake client");
        try { new ServerCommunication(clientSocket).receptionLoop(); } 
        catch (EOFException ioException) { System.out.println("Client disconnected"); } 
    }
}