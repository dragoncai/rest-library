package pop.rtbi.labs.resource;

import pop.rtbi.labs.AuthorDAO;
import pop.rtbi.labs.IAuthor;
import pop.rtbi.labs.Novelist;
import pop.rtbi.labs.OldLibraryPaths;
import pop.rtbi.labs.representation.AuthorsRepresentation;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.ok;

/**
 * Created with IntelliJ IDEA.
 * User: ycai
 * Date: 22/05/15
 * Time: 11:56
 */

@Path(OldLibraryPaths.AUTHORS_PATH)
@Produces({APPLICATION_JSON, MediaType.APPLICATION_XML})
public class OldAuthorsResource {
   private AuthorDAO authorDAO;

   @Context
   UriInfo uriInfo;

   public OldAuthorsResource() {
      authorDAO = AuthorDAO.INSTANCE;
   }

   @Path("/{id}")
   public OldAuthorResource getAuthorResource() {
      return new OldAuthorResource(uriInfo);
   }

   @GET
   public Response getAuthors() {
      List<IAuthor> entity = authorDAO.sortAndFilter(uriInfo.getQueryParameters().getFirst("sort-by"), uriInfo.getQueryParameters()).values().stream().collect(toList());

      return ok(new AuthorsRepresentation(entity)).build();
   }

   @POST
   @Consumes({APPLICATION_JSON, MediaType.APPLICATION_XML})
   public Response postAuthor(Novelist novelist) {
      IAuthor duplicatedAuthor = authorDAO.getDuplicatedAuthor(novelist);
      if (duplicatedAuthor != null) {
         return Response.status(Response.Status.CONFLICT).entity(duplicatedAuthor).build();
      } else {
         String id = authorDAO.createAuthor(novelist);
         return Response.created(uriInfo.getBaseUriBuilder().path(OldLibraryPaths.AUTHORS_PATH).path(id).build()).entity(authorDAO.readAuthor(id)).build();
      }
   }
}
