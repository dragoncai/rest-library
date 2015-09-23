package pop.rtbi.labs.resource;

import com.murex.rtbi.LinkedResource;
import pop.rtbi.labs.Work;
import pop.rtbi.labs.controller.AuthorsController;
import pop.rtbi.labs.controller.WorksController;

import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: ycai
 * Date: 04/08/2015
 * Time: 11:12
 */
@XmlRootElement(name = "book")
public class BookResource extends LinkedResource {
   public BookResource(UriInfo uriInfo, Work work, List<String> expand) {
      super(uriInfo.getBaseUriBuilder().clone().path(WorksController.class).path(work.getId()));
      Map<String, Object> entityMap = new LinkedHashMap<>();
      entityMap.put("id", work.getId());
      entityMap.put("title", work.getTitle());
      if (expand != null && expand.contains("author")) {
         entityMap.put("author", new AuthorResource(uriInfo, work.getAuthor()));
      } else {
         entityMap.put("author", new LinkedResource(uriInfo.getBaseUriBuilder().clone().path(AuthorsController.class).path(work.getAuthor().getId())));
      }
      put(ENTITY, entityMap);
   }
}