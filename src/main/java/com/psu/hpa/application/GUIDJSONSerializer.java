package com.psu.hpa.application;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

public class GUIDJSONSerializer extends JsonSerializer<GUID> {

	@Override
	public void serialize(GUID value, JsonGenerator jgen,
			SerializerProvider provider) throws IOException,
			JsonProcessingException {
		// Always send GUIDs as URL representations for safe insertion into URLs.
		jgen.writeString(value.urlRepresentation);
	}
}
