package pop.rtbi.labs.controller;

import com.murex.rtbi.CollectionResource;
import com.murex.rtbi.LinkedResource;
import pop.rtbi.labs.Publisher;
import pop.rtbi.labs.PublisherDAO;
import pop.rtbi.labs.exception.ConflictException;
import pop.rtbi.labs.model.post.PostPublisher;
import pop.rtbi.labs.model.query.DefaultQueryBean;
import pop.rtbi.labs.resource.PublisherResource;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;
import static javax.ws.rs.core.Response.created;

@Path("publishers")
@Produces({APPLICATION_JSON, APPLICATION_XML})
@Consumes({APPLICATION_JSON, APPLICATION_XML})
public class PublishersController {

   @Context
   UriInfo uriInfo;
   private final PublisherDAO publisherDAO;

   public PublishersController() {
      publisherDAO = PublisherDAO.INSTANCE;
   }

   private List<PublisherResource> getPublishersAsList(@BeanParam DefaultQueryBean queryBean) {
      return publisherDAO.readPublishers().entrySet().stream().map(getMapFunctionToRepresentPublishers(queryBean)).collect(Collectors.toList());
   }

   private Function<Map.Entry<String, Publisher>, PublisherResource> getMapFunctionToRepresentPublishers(@BeanParam DefaultQueryBean queryBean) {
      return stringPublisherEntry -> new PublisherResource(uriInfo, stringPublisherEntry.getValue(), queryBean);
   }

   @GET
   public Response getPublishers(@BeanParam DefaultQueryBean queryBean) {
      List publishers = getPublishersAsList(queryBean);
      CollectionResource collectionResource = new CollectionResource(uriInfo, publishers, "publisher", queryBean.getOffset(), queryBean.getLimit(), publisherDAO.getTotalCount());
      return Response.ok(collectionResource).build();
   }

   @Path("{publisherId}")
   public PublisherController getNewPublisherController(@PathParam("publisherId") String publisherId) {
      return new PublisherController(uriInfo, publisherId);
   }

   /**
    * POST a new Publisher.class
    *
    * @request.representation.example {"name" : ""}
    */

   @POST
   public Response postPublisher(PostPublisher postPublisher) {
      Publisher publisher = postPublisher.getPublisher();
      Publisher duplicatedPublisher = publisherDAO.getDuplicatedPublisher(publisher);
      if (duplicatedPublisher != null) {
         throw new ConflictException(new LinkedResource(uriInfo.getBaseUriBuilder().clone().path(PublishersController.class).path(duplicatedPublisher.getId()).build()));
      }
      publisherDAO.createPublisher(publisher);
      DefaultQueryBean queryBean = new DefaultQueryBean();
      PublisherResource publisherResource = new PublisherResource(uriInfo, publisher, queryBean);
      return created(uriInfo.getAbsolutePathBuilder().clone().path(publisher.getId()).build()).entity(publisherResource).build();
   }

   public void setUriInfo(UriInfo uriInfo) {
      this.uriInfo = uriInfo;
   }
}
