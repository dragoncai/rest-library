package pop.rtbi.labs;

import pop.rtbi.labs.controller.AuthorsController;
import pop.rtbi.labs.controller.PublishersController;
import pop.rtbi.labs.controller.WorksController;
import pop.rtbi.labs.exception.BadRequestExceptionMapper;
import pop.rtbi.labs.exception.ConflictExceptionMapper;
import pop.rtbi.labs.exception.DefaultExceptionMapper;
import pop.rtbi.labs.exception.NotFoundExceptionMapper;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: ycai
 * Date: 29/07/2015
 * Time: 16:59
 */
public class LibraryApplication extends Application {
   @Override
   public Set<Class<?>> getClasses() {
      Set<Class<?>> set = new HashSet<>();

      set.add(AuthorsController.class);
      set.add(WorksController.class);
      set.add(PublishersController.class);

      set.add(DefaultExceptionMapper.class);
      set.add(ConflictExceptionMapper.class);
      set.add(NotFoundExceptionMapper.class);
      set.add(BadRequestExceptionMapper.class);

      set.add(JsonProvider.class);
      set.add(XmlProvider.class);
      return set;
   }
}
