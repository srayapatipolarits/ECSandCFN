/**
 * 
 */
package com.sp.web.controller.deserializer;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;

import java.io.IOException;
import java.time.LocalDate;

/**
 * @author Dax Abraham
 * This is the deserializer class.
 */
public class EntityDeserializers {

	public static final class LocalDateDeserializer extends JsonDeserializer<LocalDate> {
		@Override
		public LocalDate deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
			JsonNode tree = jp.readValueAsTree();
			int day = tree.get("day").getIntValue();
			int month = tree.get("month").getIntValue();
			int year = tree.get("year").getIntValue();
			return LocalDate.of(year, month, day);
		}
	}
	
}
