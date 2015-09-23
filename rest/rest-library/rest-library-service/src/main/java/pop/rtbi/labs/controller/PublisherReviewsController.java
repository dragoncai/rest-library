package pop.rtbi.labs.controller;

import com.murex.rtbi.CollectionResource;
import pop.rtbi.labs.*;
import pop.rtbi.labs.model.post.PostReview;
import pop.rtbi.labs.model.query.DefaultQueryBean;
import pop.rtbi.labs.resource.PublisherResource;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;

@Path("reviews")
@Produces({APPLICATION_JSON, APPLICATION_XML})
@Consumes({APPLICATION_JSON, APPLICATION_XML})
public class PublisherReviewsController {
   private final UriInfo uriInfo;
   private final String publisherId;
   private PublisherDAO publisherDAO;

   public PublisherReviewsController(UriInfo uriInfo, String publisherId) {
      this.uriInfo = uriInfo;
      this.publisherId = publisherId;
      publisherDAO = PublisherDAO.INSTANCE;
   }

   private Publisher getPublisher() {
      return publisherDAO.readPublisher(publisherId);
   }

   /**
    * POST a new published Review.class
    *
    * @request.representation.example {"work" : "workId", "review" : {"rate" : 0, "comment" : ""}}
    */
   @POST
   public Response publishReview(PostReview postReview) {
      WorkDAO workDAO = WorkDAO.INSTANCE;
      Work work = workDAO.readBook(postReview.getWork());
      if (work == null) {
         throw new NotFoundException("Work@" + postReview.getWork() + " not found");
      }
      publisherDAO.publishAReview(publisherId, work, postReview.getReview());
      DefaultQueryBean queryBean = new DefaultQueryBean();
      return Response.created(uriInfo.getAbsolutePathBuilder().build()).entity(getPublishedReviews(queryBean).getEntity()).build();
   }


   @GET
   public Response getPublishedReviews(@BeanParam DefaultQueryBean queryBean) {
      CollectionResource reviewsResource = new PublisherResource(uriInfo, getPublisher(), queryBean).getReviewsResource();
      return Response.ok(reviewsResource).build();
   }

   @GET
   @Path("{workId}")
   public Response getWorkReviews(@PathParam("workId") String workId) {
      Work work = WorkDAO.INSTANCE.readBook(workId);
      List<Review> reviews = getPublisher().getReviews().get(work);
      CollectionResource collectionResource = new CollectionResource(uriInfo, reviews, "review", reviews.size());
      return Response.ok(collectionResource).build();
   }

   @DELETE
   @Path("{workId}/{index}")
   public Response deleteWorkReview(@PathParam("workId") String workId, @PathParam("index") int index) {
      Work work = WorkDAO.INSTANCE.readBook(workId);
      List<Review> reviews = getPublisher().getReviews().get(work);
      reviews.remove(index);
      return Response.ok().build();
   }
}
