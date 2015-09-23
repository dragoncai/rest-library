package pop.rtbi.labs;

import pop.rtbi.labs.resource.OldAuthorsResource;
import pop.rtbi.labs.resource.OldBooksResource;
import pop.rtbi.labs.resource.RootResource;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: ycai
 * Date: 28/05/15
 * Time: 10:24
 */
public class OldLibraryService extends Application {
   @Override
   public Set<Class<?>> getClasses() {
      Set<Class<?>> set = new HashSet<>();
      set.add(OldAuthorsResource.class);
      set.add(OldBooksResource.class);
      set.add(RootResource.class);

      set.add(JsonProvider.class);
      set.add(XmlProvider.class);
      return set;
   }
}
