package net;

import java.util.Arrays;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Message
{
    protected static final Logger logger = LogManager.getLogger();
    protected static final ObjectMapper objectMapper = new ObjectMapper();

    private String key;
    private Object[] values;

    public Message(){}

    public Message(String key, Object... values)
    {
        this.key = key;
        this.values = values;
    }

    public String getKey()
    {
        return key;
    }

    public Object[] getValues()
    {
        return values;
    }

    public <T> T getValue(int index, TypeReference<T> valueType)
    {
        try { return objectMapper.readValue(objectMapper.writeValueAsString(values[index]), valueType); } 
        catch (JsonProcessingException jsonProcessingException) { logger.error("", jsonProcessingException); }

        return null;
    }

    @Override
    public String toString() 
    {
        return String.format("%s: %s", key, Arrays.toString(values));
    }
}