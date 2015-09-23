package pop.rtbi.labs.wadl;

import org.glassfish.jersey.server.wadl.internal.generators.WadlGeneratorApplicationDoc;
import org.glassfish.jersey.server.wadl.internal.generators.WadlGeneratorGrammarsSupport;
import org.glassfish.jersey.server.wadl.internal.generators.resourcedoc.WadlGeneratorResourceDocSupport;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ycai
 * Date: 21/08/2015
 * Time: 13:47
 */
public class WadlGeneratorConfig extends org.glassfish.jersey.server.wadl.config.WadlGeneratorConfig {
   @Override
   public List configure() {
      return generator(WadlGeneratorApplicationDoc.class)
           .prop("applicationDocsStream", "application-doc.xml")
           .generator(WadlGeneratorGrammarsSupport.class)
           .prop("grammarsStream", "application-grammars.xml")
           .prop("overrideGrammars", true)
           .generator(WadlGeneratorResourceDocSupport.class)
           .prop("resourceDocStream", "resourcedoc.xml")
           .descriptions();
   }
}
