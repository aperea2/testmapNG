package iris.domain.util;

import java.io.IOException;
import com.bedatadriven.jackson.datatype.jts.JtsModule;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vividsolutions.jts.geom.Geometry;

public class JsonToGeomDeserializer extends JsonDeserializer<Geometry>{

	@Override
	public Geometry deserialize(JsonParser jp, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {

		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JtsModule());

		String text = jp.getText();				
		
		Geometry geom = mapper.readValue(text, Geometry.class);
		
		return geom;
	}

}
