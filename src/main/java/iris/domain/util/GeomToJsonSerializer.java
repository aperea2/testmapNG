package iris.domain.util;

import java.io.IOException;

import com.bedatadriven.jackson.datatype.jts.JtsModule;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.vividsolutions.jts.geom.Geometry;

public class GeomToJsonSerializer extends JsonSerializer<Geometry> {

	@Override
	public void serialize(Geometry value, JsonGenerator jgen, SerializerProvider provider)
			throws IOException, JsonProcessingException {

		// To use JTS geometry datatypes with Jackson, you will first need to
		// register the module first
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JtsModule());
		
		String jsonValue = mapper.writeValueAsString(value);
		
		jgen.writeString(jsonValue);
	}

}
