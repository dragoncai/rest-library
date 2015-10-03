package pop.rtbi.labs.resource;

import com.murex.rtbi.LinkedResource;
import pop.rtbi.labs.IAuthor;
import pop.rtbi.labs.controller.AuthorsController;

import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: ycai
 * Date: 29/07/2015
 * Time: 17:02
 */
@XmlRootElement(name = "author")
public class AuthorResource extends LinkedResource {
   public AuthorResource(UriInfo uriInfo, IAuthor author) {
      super(uriInfo.getBaseUriBuilder().clone().path(AuthorsController.class).path(author.getId()).build());
      Map<String, Object> entityMap = new LinkedHashMap<>();
      entityMap.put("id", author.getId());
      entityMap.put("name", author.getName());
      entityMap.put("type", author.getClass().getSimpleName().toLowerCase());
      entityMap.put("books", new LinkedResource(uriInfo.getBaseUriBuilder().clone().path(AuthorsController.class).path(author.getId()).path("books").build()));
      put(ENTITY, entityMap);
   }
}
