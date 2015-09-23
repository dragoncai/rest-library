package pop.rtbi.labs.controller;

import com.murex.rtbi.CollectionResource;
import pop.rtbi.labs.*;
import pop.rtbi.labs.model.query.WorksQueryBean;
import pop.rtbi.labs.resource.AuthorResource;
import pop.rtbi.labs.resource.BookResource;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;

//@Path("{authorId}")
@Produces({APPLICATION_JSON, APPLICATION_XML})
@Consumes({APPLICATION_JSON, APPLICATION_XML})
public class AuthorController {
   UriInfo uriInfo;
   AuthorDAO authorDAO;
   WorkDAO workDAO;
//   @PathParam("authorId")
   String authorId;

   public AuthorController(UriInfo uriInfo, String authorId) {
      this.uriInfo = uriInfo;
      authorDAO = AuthorDAO.INSTANCE;
      workDAO = WorkDAO.INSTANCE;
      this.authorId = authorId;
   }

   @GET
   public Response getAuthor() {
      IAuthor entity = authorDAO.readAuthor(this.authorId);
      if (entity == null) {
         throw new NotFoundException();
      }
      AuthorResource authorResource = new AuthorResource(uriInfo, entity);
      return Response.ok().entity(authorResource).build();
   }

   @DELETE
   public Response deleteAuthor() {
      if (authorDAO.readAuthor(this.authorId) == null) {
         throw new NotFoundException();
      }
      workDAO.readBooks().entrySet().stream().filter(book -> book.getValue().getAuthor().getId().equals(authorId)).forEach(book1 -> workDAO.deleteBook(book1.getValue().getId()));

      authorDAO.deleteAuthor(this.authorId);
      return Response.ok().build();
   }

   @PUT
   public Response putAuthor(Novelist novelist) {
      if (authorDAO.readAuthor(this.authorId) == null) {
         throw new NotFoundException();
      }
      IAuthor author = authorDAO.updateAuthor(this.authorId, novelist);
      AuthorResource authorResource = new AuthorResource(uriInfo, author);
      return Response.ok(authorResource).build();
   }

   @GET
   @Path("books")
   public Response getBooks() {
      WorksQueryBean worksQueryBean = new WorksQueryBean();
      worksQueryBean.setAuthorId(authorId);
      Collection<Work> workCollection = workDAO.sortAndFilter(null, worksQueryBean.getMultivaluedMap()).values();
      List<BookResource> books = workCollection.stream().map(work -> new BookResource(uriInfo, work, null)).collect(Collectors.toList());
      return Response.ok(new CollectionResource(uriInfo, books, "books", books.size())).build();
   }
}
