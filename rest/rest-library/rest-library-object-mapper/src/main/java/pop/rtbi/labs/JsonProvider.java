package pop.rtbi.labs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.jaxrs.cfg.Annotations;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;

import javax.ws.rs.ext.Provider;

/**
 * Created with IntelliJ IDEA.
 * User: ycai
 * Date: 23/07/2015
 * Time: 17:02
 */
@Provider
public class JsonProvider extends JacksonJaxbJsonProvider {

   private ObjectMapper objectMapper;

   public JsonProvider() {
      this(null, JacksonJaxbJsonProvider.DEFAULT_ANNOTATIONS);
   }

   public JsonProvider(Annotations... annotationsToUse) {
      super(null, annotationsToUse);
   }

   public JsonProvider(ObjectMapper mapper, Annotations[] annotationsToUse) {
      super(mapper, annotationsToUse);
      objectMapper = this._mapperConfig.getDefaultMapper();
      objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
      objectMapper.addMixInAnnotations(Book.class, BookMixIn.class);
   }

   public ObjectMapper getObjectMapper() {
      return objectMapper;
   }
}
