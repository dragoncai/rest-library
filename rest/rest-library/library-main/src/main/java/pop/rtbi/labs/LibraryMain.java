package pop.rtbi.labs;

import com.murex.rtbi.GrizzlyEmbeddedServer;
import com.murex.rtbi.NettyEmbeddedServer;
import javassist.CannotCompileException;
import javassist.NotFoundException;
import org.glassfish.jersey.server.ServerProperties;
import pop.rtbi.labs.wadl.WadlGeneratorConfig;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: ycai
 * Date: 01/07/2015
 * Time: 11:43
 */
public class LibraryMain {
   private LibraryMain() {
   }

   public static void main(String[] args) throws IOException, NotFoundException, CannotCompileException {
      GrizzlyEmbeddedServer server = new GrizzlyEmbeddedServer(new LibraryApplication());
//      server.addServletInitParam(ServerProperties.WADL_GENERATOR_CONFIG, WadlGeneratorConfig.class.getName());
      server.setHost("0.0.0.0");
      server.setPort(LibraryPaths.PORT);
      server.start();
      while (true){

      }
   }

}
