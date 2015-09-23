package pop.rtbi.labs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.jaxrs.cfg.Annotations;
import com.fasterxml.jackson.jaxrs.xml.JacksonJaxbXMLProvider;

import javax.ws.rs.ext.Provider;

/**
 * Created with IntelliJ IDEA.
 * User: ycai
 * Date: 23/07/2015
 * Time: 17:10
 */
@Provider
public class XmlProvider extends JacksonJaxbXMLProvider {

   private XmlMapper objectMapper;

   public XmlProvider() {
      this(null, JacksonJaxbXMLProvider.DEFAULT_ANNOTATIONS);
   }

   public XmlProvider(Annotations... annotationsToUse) {
      this(null, annotationsToUse);
   }

   public XmlProvider(XmlMapper mapper, Annotations[] annotationsToUse) {
      super(mapper, annotationsToUse);
      objectMapper = this._mapperConfig.getDefaultMapper();
      objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
      objectMapper.addMixInAnnotations(Book.class, BookMixIn.class);
   }

   public ObjectMapper getObjectMapper() {
      return objectMapper;
   }
}