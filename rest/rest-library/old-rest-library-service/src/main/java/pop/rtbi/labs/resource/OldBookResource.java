package pop.rtbi.labs.resource;

import pop.rtbi.labs.Book;
import pop.rtbi.labs.Work;
import pop.rtbi.labs.WorkDAO;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.ok;

/**
 * Created with IntelliJ IDEA.
 * User: ycai
 * Date: 10/06/2015
 * Time: 10:03
 */
@Produces({APPLICATION_JSON, MediaType.APPLICATION_XML})
public class OldBookResource {
   private WorkDAO workDAO = WorkDAO.INSTANCE;

   @GET
   public Response getBook(@PathParam("id") String id) {
      Work book = workDAO.readBook(id);
      if (book != null) {
         return Response.ok(book).build();
      }
      return Response.noContent().build();
   }

   @PUT
   @Consumes({APPLICATION_JSON, MediaType.APPLICATION_XML})
   public Response putBook(Book newBook, @PathParam("id") String id) {
      Work book = workDAO.readBook(id);
      if (book == null) {
         return Response.noContent().entity("This book does not exist").build();
      }
      return Response.accepted().entity(workDAO.updateBook(id, newBook)).build();
   }

   @DELETE
   public Response deleteBook(@PathParam("id") String id) {
      if (workDAO.readBook(id) == null) {
         return Response.noContent().entity("This book does not exist").build();
      }
      workDAO.deleteBook(id);
      return ok("Successfully deleted").build();
   }
}
