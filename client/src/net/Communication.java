package net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
//import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

public abstract class Communication 
{
    protected static final Logger logger = LogManager.getLogger();
    protected static final ObjectMapper objectMapper = new ObjectMapper();
    protected static final TypeFactory typeFactory = objectMapper.getTypeFactory();
    
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;

    public Communication(Socket socket)
    {
        try
        {
            this.dataInputStream = new DataInputStream(socket.getInputStream());
            this.dataOutputStream = new DataOutputStream(socket.getOutputStream());
        }
        catch (IOException exception) { logger.error("", exception); }
    }

    protected abstract void process(Message message);

    public void receptionLoop() throws EOFException
    {
        for (;;)
        {
            try 
            {
                // Receive and process the next sent message
                Message message = objectMapper.readValue(dataInputStream.readUTF(), Message.class);
                //logger.error(String.format("[RECEIVE MESSAGE] %s", message));
                process(message);
            }
            catch (EOFException eofException) { throw eofException; }
            catch (IOException ioException) { logger.error("", ioException); }
        }
    }

    public final void send(String key, Object... values)
    {
        //logger.error(String.format("[SEND    MESSAGE] %s: %s", key, Arrays.toString(values)));
        try { dataOutputStream.writeUTF(objectMapper.writeValueAsString(new Message(key, values))); }
        catch (IOException ioException) { logger.error("", ioException); }
    }
}