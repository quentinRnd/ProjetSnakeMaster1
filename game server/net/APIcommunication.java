package net;

import java.util.Date;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class APIcommunication
{
	// Environment variables
	private static final String apiURL = "http://localhost:8080/snake_web_server/api";
	private static final String serverAuthenticationToken = "7F/w38qc,C&2[G!mZQtcuZjupoKwBnt^vSY6$VEnWBsI6QTcFnvl&KEI<X!=r8,}eBb%b^PrigET}s16u}IT,s?e?b.fwKNi:T|OYEuf7{t<t5jWW>nxl89'UvzEP:^wcuSHF#KdHQ0xJr5a;3sU%+";
    
	private static final Logger logger = LogManager.getLogger();
    protected static final ObjectMapper objectMapper = new ObjectMapper();

	public static class Response
	{
		private int code;
		private JsonNode body;
		
		public Response(int code, JsonNode body)
		{
			this.code = code;
			this.body = body;
		}

		public int		getCode()	{ return code;	}
		public JsonNode	getBody()	{ return body;	}
	}

	private static Response send(String method, Map.Entry<String, ObjectNode> requestProperty) 
  	{
		int responseCode = HttpURLConnection.HTTP_INTERNAL_ERROR; 
		JsonNode responseBody = null;

		try
		{
			String log = String.format("%s %s %s", method, requestProperty.getKey(), requestProperty.getValue());
			requestProperty.getValue().put("serverAuthenticationToken", serverAuthenticationToken);

			// Send the JSON request
			URL url = new URL(apiURL);
			HttpURLConnection httpURLconnection = (HttpURLConnection) url.openConnection();
			httpURLconnection.setRequestMethod(method);
			httpURLconnection.setRequestProperty(requestProperty.getKey(), requestProperty.getValue().toString());
			responseCode = httpURLconnection.getResponseCode();

			logger.error("message: " + httpURLconnection.getResponseMessage());

			log += String.format(" %s", httpURLconnection.getResponseMessage());
			
			// Read the response body if provided
			try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLconnection.getInputStream())))
			{
				StringBuffer buffer = new StringBuffer();
				for (String line; (line = bufferedReader.readLine()) != null;)
					buffer.append(line);
				
				responseBody = objectMapper.readTree(buffer.toString());
			}

			log += String.format(" %s", responseBody);
			
			logger.error(log);
		}
		catch (IOException ioException) { logger.error("", ioException); }

		return new Response(responseCode, responseBody);
	}

	public static Response signIn(String userName, String password)
	{
		ObjectNode request = objectMapper.createObjectNode();
		
		request.put("userName", userName);
		request.put("password", password);

		return send("HEAD", Map.entry("sign-in", request));
	}

	public static Response saveGame(Date date, String level, int playerCount, int aiCount)
	{	
		ObjectNode request = objectMapper.createObjectNode();

		request.put("date", date.getTime());
		request.put("level", level);
		request.put("playerCount", playerCount);
		request.put("aiCount", aiCount);

		return send("POST", Map.entry("save-game", request));
	}

	public static Response saveGameResult(long gameId, String userName, int rank)
	{
		ObjectNode request = objectMapper.createObjectNode();

		request.put("gameId", gameId);
		request.put("userName", userName);
		request.put("rank",rank);

		return send("POST", Map.entry("save-game-result", request));
	}
}