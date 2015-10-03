package pop.rtbi.labs.controller;

import com.murex.rtbi.CollectionResource;
import com.murex.rtbi.LinkedResource;
import pop.rtbi.labs.AuthorDAO;
import pop.rtbi.labs.IAuthor;
import pop.rtbi.labs.exception.ConflictException;
import pop.rtbi.labs.model.post.PostAuthor;
import pop.rtbi.labs.model.query.AuthorsQueryBean;
import pop.rtbi.labs.resource.AuthorResource;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;
import static javax.ws.rs.core.Response.created;

@Path("authors")
@Produces({APPLICATION_JSON, APPLICATION_XML})
@Consumes({APPLICATION_JSON, APPLICATION_XML})
public class AuthorsController {
   @Context
   UriInfo uriInfo;
   AuthorDAO authorDAO;

   public AuthorsController() {
      authorDAO = AuthorDAO.INSTANCE;
   }

   @GET
   public Response getAuthors(@BeanParam AuthorsQueryBean queryBean) {
      Map<String, IAuthor> authorMap = authorDAO.sortAndFilter(queryBean.getSortBy(), queryBean.getFilters());
      Stream<AuthorResource> authorResourceStream = authorMap.values().stream().map(iAuthor -> new AuthorResource(uriInfo, iAuthor));
      List authors = authorResourceStream.collect(Collectors.toList());
      CollectionResource collectionResource = new CollectionResource(uriInfo, authors, "author", queryBean.getOffset(), queryBean.getLimit(), authorDAO.getTotalCount());
      return Response.ok().entity(collectionResource).build();
   }

   @Path("{authorId}")
   public AuthorController getAuthor(@PathParam("authorId") String authorId) {
      return new AuthorController(uriInfo, authorId);
   }

   /**
    * POST a new IAuthor.class
    *
    * @request.representation.example {"name" : "","type" : ""}
    */

   @POST
   public Response postAuthor(PostAuthor postAuthor) {
      IAuthor author = postAuthor.getAuthor();
      if (author == null) {
         throw new BadRequestException();
      }
      IAuthor duplicatedAuthor = authorDAO.getDuplicatedAuthor(author);
      if (duplicatedAuthor != null) {
         throw new ConflictException(new LinkedResource(uriInfo.getBaseUriBuilder().clone().path(AuthorsController.class).path(duplicatedAuthor.getId()).build()));
      }

      String id = authorDAO.createAuthor(author);
      IAuthor iAuthor = authorDAO.readAuthor(id);
      return created(uriInfo.getAbsolutePathBuilder().clone().path(id).build()).entity(new AuthorResource(uriInfo, iAuthor)).build();
   }

   public void setUriInfo(UriInfo uriInfo) {
      this.uriInfo = uriInfo;
   }
}
