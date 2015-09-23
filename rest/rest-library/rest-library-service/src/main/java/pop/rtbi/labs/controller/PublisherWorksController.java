package pop.rtbi.labs.controller;

import com.murex.rtbi.CollectionResource;
import pop.rtbi.labs.*;
import pop.rtbi.labs.exception.ConflictException;
import pop.rtbi.labs.model.post.PostWork;
import pop.rtbi.labs.model.query.DefaultQueryBean;
import pop.rtbi.labs.resource.PublisherResource;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.Map;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;

@Path("works")
@Produces({APPLICATION_JSON, APPLICATION_XML})
@Consumes({APPLICATION_JSON, APPLICATION_XML})
public class PublisherWorksController {
   private final UriInfo uriInfo;
   private final PublisherDAO publisherDAO;
   private final String publisherId;

   public PublisherWorksController(UriInfo uriInfo, String publisherId) {
      this.uriInfo = uriInfo;
      this.publisherId = publisherId;
      publisherDAO = PublisherDAO.INSTANCE;
   }

   private Publisher getPublisher() {
      return publisherDAO.readPublisher(publisherId);
   }


   /**
    * POST a new published Work.class
    *
    * @request.representation.example {"work" : "workId"}
    */
   @POST
   public Response publishWork(PostWork postWork) {
      Work work = WorkDAO.INSTANCE.readBook(postWork.getWork());
      if (work == null) {
         throw new NotFoundException("Work@" + postWork.getWork() + " not found");
      }
      IAuthor author = work.getAuthor();

      Map<IAuthor, List<Work>> works = getPublisher().getWorks();
      if (works.containsKey(author) && works.get(author).contains(work)) {
         throw new ConflictException();
      }
      publisherDAO.publishAWork(publisherId, work);
      DefaultQueryBean queryBean = new DefaultQueryBean();
      return Response.created(uriInfo.getAbsolutePathBuilder().build()).entity(getPublishedWorks(queryBean).getEntity()).build();
   }

   @GET
   public Response getPublishedWorks(@BeanParam DefaultQueryBean queryBean) {
      CollectionResource worksResource = new PublisherResource(uriInfo, getPublisher(), queryBean).getWorksResource();
      return Response.ok(worksResource).build();
   }

   @GET
   @Path("{authorId}")
   public Response getAuthorPublishedWorks(@PathParam("authorId") String authorId) {
      IAuthor author = AuthorDAO.INSTANCE.readAuthor(authorId);
      List<Work> works = getPublisher().getWorks().get(author);
      CollectionResource collectionResource = new CollectionResource(uriInfo, works, "work", works.size());
      return Response.ok(collectionResource).build();
   }

   @DELETE
   @Path("{authorId}/{workId}")
   public Response deleteAuthorPublishedWork(@PathParam("authorId") String authorId, @PathParam("workId") String workId) {
      IAuthor author = AuthorDAO.INSTANCE.readAuthor(authorId);
      Work work = WorkDAO.INSTANCE.readBook(workId);
      List<Work> works = getPublisher().getWorks().get(author);
      works.remove(work);
      return Response.ok().build();
   }
}
