package com.enseed.mksdk.core.json;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.lang.reflect.Type;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.common.io.Closeables;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.stream.JsonReader;

public class JSONParser {
	
	public final static class DateTimeDeserializer implements JsonDeserializer<DateTime>, JsonSerializer<DateTime>
	{
	   static final DateTimeFormatter DATE_TIME_FORMATTER =
	      ISODateTimeFormat.dateTime().withZone(DateTimeZone.UTC);

	   @Override
	   public DateTime deserialize(final JsonElement je, final Type type,
	                           final JsonDeserializationContext jdc) throws JsonParseException
	   {
	      return je.getAsString().length() == 0 ? null : new DateTime(je.getAsString());
	   }

	   @Override
	   public JsonElement serialize(final DateTime src, final Type typeOfSrc,
	                                final JsonSerializationContext context)
	   {
	      return new JsonPrimitive(src == null ? StringUtils.EMPTY :DATE_TIME_FORMATTER.print(src)); 
	   }
	}
	
	public static String toString(Object obj)
	{
		JSONParser parser = new JSONParser();
		return parser.toJSON(obj);
	}
	
	private Gson m_gson;

	public JSONParser()
	{
		m_gson = new GsonBuilder()
			.setPrettyPrinting()
			.registerTypeAdapter(DateTime.class, new DateTimeDeserializer())
			.setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
			.create();
	}
	
	public String toJSON(Object obj)
	{
		JsonElement elem = m_gson.toJsonTree(obj);
		JsonObject document = new JsonObject();
		document.add("d",  elem);
		return m_gson.toJson(document);
	}

	public <T> T fromJSON(String json, Class<T> type)
	{
		JsonReader reader = new JsonReader(new StringReader(json));
		reader.setLenient(true);
		JsonObject obj = m_gson.fromJson(reader,  JsonObject.class);
		return m_gson.fromJson(obj.get("d").getAsJsonObject(), type);
		
	}

	public <T> T fromJSON(InputStream jsonStream, Class<T> type) throws IOException
	{
		String content = CharStreams.toString(new InputStreamReader(jsonStream, Charsets.UTF_8));
		Closeables.closeQuietly(jsonStream);
		
		return fromJSON(content, type);
	}
}
