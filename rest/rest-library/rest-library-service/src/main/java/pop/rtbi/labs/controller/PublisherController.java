package pop.rtbi.labs.controller;

import pop.rtbi.labs.Publisher;
import pop.rtbi.labs.PublisherDAO;
import pop.rtbi.labs.model.query.DefaultQueryBean;
import pop.rtbi.labs.resource.PublisherResource;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;

@Path("{publisherId}")
@Produces({APPLICATION_JSON, APPLICATION_XML})
@Consumes({APPLICATION_JSON, APPLICATION_XML})
public class PublisherController {
   private UriInfo uriInfo;
   @PathParam("publisherId")
   private String publisherId;
   private PublisherDAO publisherDAO;

   public PublisherController(UriInfo uriInfo, String publisherId) {
      this.uriInfo = uriInfo;
      this.publisherId = publisherId;
      publisherDAO = PublisherDAO.INSTANCE;
   }

   @GET
   public Response getPublisher(@BeanParam DefaultQueryBean queryBean) {
      Publisher publisher = publisherDAO.readPublisher(publisherId);
      if (publisher == null) {
         throw new NotFoundException();
      }
      PublisherResource publisherResource = new PublisherResource(uriInfo, publisher, queryBean);
      return Response.ok(publisherResource).build();
   }

   @Path("works")
   public PublisherWorksController getNewPublisherWorksController() {
      return new PublisherWorksController(uriInfo, publisherId);
   }

   @Path("reviews")
   public PublisherReviewsController getNewPublisherReviewsController() {
      return new PublisherReviewsController(uriInfo, publisherId);
   }

   @DELETE
   public Response deletePublisher() {
      if (publisherDAO.readPublisher(publisherId) == null) {
         throw new NotFoundException();
      }
      publisherDAO.deletePublisher(publisherId);
      return Response.ok().build();
   }
}
