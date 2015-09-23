package pop.rtbi.labs.controller;

import pop.rtbi.labs.AuthorDAO;
import pop.rtbi.labs.Book;
import pop.rtbi.labs.Work;
import pop.rtbi.labs.WorkDAO;
import pop.rtbi.labs.model.query.DefaultQueryBean;
import pop.rtbi.labs.resource.BookResource;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;

@Path("{bookId}")
@Produces({APPLICATION_JSON, APPLICATION_XML})
@Consumes({APPLICATION_JSON, APPLICATION_XML})
public class WorkController {
   UriInfo uriInfo;
   WorkDAO workDAO;
   @PathParam("bookId")
   String bookId;
   private AuthorDAO authorDAO;

   public WorkController(UriInfo uriInfo, String bookId) {
      this.uriInfo = uriInfo;
      workDAO = WorkDAO.INSTANCE;
      authorDAO = AuthorDAO.INSTANCE;
      this.bookId = bookId;
   }

   @GET
   public Response getBook(@BeanParam DefaultQueryBean queryBean) {
      Work entity = workDAO.readBook(bookId);
      if (entity == null) {
         throw new NotFoundException();
      }
      BookResource bookResource = new BookResource(uriInfo, entity, queryBean.getExpand());
      return Response.ok().entity(bookResource).build();
   }

   @PUT
   public Response putBook(Book book) {
      if (authorDAO.getDuplicatedAuthor(book.getAuthor()) == null) {
         throw new NotFoundException();
      }

      if (workDAO.readBook(bookId) == null) {
         throw new NotFoundException();
      }
      Work work = workDAO.updateBook(bookId, book);
      BookResource bookResource = new BookResource(uriInfo, work, null);
      return Response.ok(bookResource).build();
   }

   @DELETE
   public Response deleteBook() {
      if (workDAO.readBook(bookId) == null) {
         throw new NotFoundException();
      }

      workDAO.deleteBook(bookId);
      return Response.ok().build();
   }
}
