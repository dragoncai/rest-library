package pop.rtbi.labs;

import com.murex.rtbi.NettyEmbeddedServer;
import javassist.CannotCompileException;
import javassist.NotFoundException;

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
      NettyEmbeddedServer server = new NettyEmbeddedServer(new LibraryApplication());
//      server.addServletInitParam(ServerProperties.WADL_GENERATOR_CONFIG, WadlGeneratorConfig.class.getName());
      server.setHost(LibraryPaths.BASE_URI);
      server.setPort(LibraryPaths.PORT);
      server.start();
   }

}
