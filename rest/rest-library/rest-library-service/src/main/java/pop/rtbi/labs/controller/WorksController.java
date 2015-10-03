package pop.rtbi.labs.controller;

import com.murex.rtbi.CollectionResource;
import com.murex.rtbi.LinkedResource;
import pop.rtbi.labs.*;
import pop.rtbi.labs.exception.ConflictException;
import pop.rtbi.labs.model.query.WorksQueryBean;
import pop.rtbi.labs.resource.BookResource;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;
import static javax.ws.rs.core.Response.created;

@Path("books")
@Produces({APPLICATION_JSON, APPLICATION_XML})
@Consumes({APPLICATION_JSON, APPLICATION_XML})
public class WorksController {
   @Context
   UriInfo uriInfo;
   WorkDAO workDAO;
   AuthorDAO authorDAO;

   public WorksController() {
      workDAO = WorkDAO.INSTANCE;
      authorDAO = AuthorDAO.INSTANCE;
   }

   @GET
   public Response getBooks(@BeanParam WorksQueryBean worksQueryBean) {
       Collection<Work> workCollection = workDAO.sortAndFilter(worksQueryBean.getSortBy(), worksQueryBean.getFilters()).values();
       Stream<BookResource> bookResourceStream = workCollection.stream().map(work -> new BookResource(uriInfo, work, worksQueryBean.getExpand()));
       List<BookResource> books = bookResourceStream.collect(Collectors.toList());
       CollectionResource collectionResource = new CollectionResource(uriInfo, books, "book", worksQueryBean.getOffset(), worksQueryBean.getLimit(), workDAO.getTotalCount());
       return Response.ok().entity(collectionResource).build();
   }

   @Path("{bookId}")
   public WorkController getBook(@PathParam("bookId") String bookId) {
      return new WorkController(uriInfo, bookId);
   }

   /**
    * POST a new published Book.class
    *
    * @request.representation.example {"title" : "", "author" : {"name" : ""}}
    */
   @POST
   public Response postBook(Book book) {
      Book book1 = book;
      Work duplicatedBook = workDAO.getDuplicatedBook(book1);
      if (duplicatedBook != null) {
         throw new ConflictException(new LinkedResource(uriInfo.getBaseUriBuilder().clone().path(WorksController.class).path(duplicatedBook.getId()).build()));
      }

      IAuthor duplicatedAuthor = authorDAO.getDuplicatedAuthor(book1.getAuthor());
      if (duplicatedAuthor != null) {
         book1 = new Book(book1.getId(), book1.getTitle(), duplicatedAuthor);
      } else {
         authorDAO.createAuthor(book1.getAuthor());
      }

      String id = workDAO.createBook(book1);
      Work work = workDAO.readBook(id);
      return created(uriInfo.getAbsolutePathBuilder().clone().path(id).build()).entity(new BookResource(uriInfo, work, null)).build();
   }

   public void setUriInfo(UriInfo uriInfo) {
      this.uriInfo = uriInfo;
   }
}
