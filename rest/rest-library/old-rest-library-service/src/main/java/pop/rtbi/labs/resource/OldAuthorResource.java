package pop.rtbi.labs.resource;

import pop.rtbi.labs.*;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import static java.util.stream.Collectors.toList;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.ok;

/**
 * Created with IntelliJ IDEA.
 * User: ycai
 * Date: 09/06/2015
 * Time: 17:03
 */
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class OldAuthorResource {
   UriInfo uriInfo;
   AuthorDAO authorDAO = AuthorDAO.INSTANCE;
   WorkDAO workDAO = WorkDAO.INSTANCE;

   public OldAuthorResource(UriInfo uriInfo) {
      this.uriInfo = uriInfo;
   }

   @GET
   public Response getAuthor(@PathParam("id") String id) {
      IAuthor author = authorDAO.readAuthor(id);
      if (author != null) {
         return Response.ok(author).build();
      }
      return Response.noContent().build();
   }

   @GET
   @Path(OldLibraryPaths.BOOKS_PATH)
   public Response getAuthorBooks(@QueryParam("title") String title, @PathParam("id") String id) {
      UriBuilder uriBuilder = UriBuilder.fromUri(uriInfo.getBaseUri()).path(OldLibraryPaths.BOOKS_PATH).queryParam(Book.AUTHOR, "\"" + authorDAO.readAuthor(id).getName() + "\"");
      if (title != null) {
         uriBuilder.queryParam(Book.TITLE, title);
      }
      return Response.temporaryRedirect(uriBuilder.build()).build();
   }

   @PUT
   @Consumes({APPLICATION_JSON, MediaType.APPLICATION_XML})
   public Response putAuthor(Novelist newNovelist, @PathParam("id") String id) {
      if (authorDAO.readAuthor(id) == null) {
         return Response.noContent().entity("This resource does not exist").build();
      }
      return Response.accepted(authorDAO.updateAuthor(id, newNovelist)).build();
   }

   @DELETE
   public Response deleteAuthor(@PathParam("id") String id) {
      if (authorDAO.readAuthor(id) == null) {
         return Response.noContent().entity("This resource does not exist").build();
      }
      workDAO.readBooks().values().stream().collect(toList()).stream().filter(book -> book.getAuthor().getId().equals(id)).forEach(book1 -> workDAO.deleteBook(book1.getId()));

      authorDAO.deleteAuthor(id);
      return ok("Successfully deleted").build();
   }
}
