package pop.rtbi.labs.resource;

import com.murex.rtbi.CollectionResource;
import com.murex.rtbi.LinkedResource;
import pop.rtbi.labs.Publisher;
import pop.rtbi.labs.Review;
import pop.rtbi.labs.Work;
import pop.rtbi.labs.controller.PublisherReviewsController;
import pop.rtbi.labs.controller.PublisherWorksController;
import pop.rtbi.labs.controller.PublishersController;
import pop.rtbi.labs.controller.WorksController;
import pop.rtbi.labs.model.query.DefaultQueryBean;

import javax.ws.rs.core.UriInfo;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created with IntelliJ IDEA.
 * User: ycai
 * Date: 11/08/2015
 * Time: 14:29
 */
public class PublisherResource extends LinkedResource {

   private final Publisher publisher;
   private final UriInfo uriInfo;
   private final DefaultQueryBean queryBean;

   public PublisherResource(UriInfo uriInfo, Publisher publisher, DefaultQueryBean queryBean) {
      super(uriInfo.getBaseUriBuilder().clone().path(PublishersController.class).path(publisher.getId()).build());

      this.publisher = publisher;
      this.uriInfo = uriInfo;
      this.queryBean = queryBean;

      LinkedHashMap<String, Object> entityMap = new LinkedHashMap<>();
      entityMap.put(Publisher.ID, publisher.getId());
      entityMap.put(Publisher.NAME, publisher.getName());
      entityMap.put(Publisher.WORKS, addWorks());
      entityMap.put(Publisher.REVIEWS, addReviews());

      put(ENTITY, entityMap);
   }

   private Object addWorks() {
      if (queryBean != null && queryBean.getExpand().contains(Publisher.WORKS)) {
         return getWorks();
      } else {
         return buildLinkedResource(PublisherWorksController.class);
      }
   }

   private Object addReviews() {
      if (queryBean != null && queryBean.getExpand().contains(Publisher.REVIEWS)) {
         return getReviews();
      } else {
         return buildLinkedResource(PublisherReviewsController.class);
      }
   }

   private LinkedResource buildLinkedResource(Class<?> resource) {
      return new LinkedResource(uriInfo.getBaseUriBuilder().clone().path(PublishersController.class).path(publisher.getId()).path(resource).build());
   }

   private CollectionResource createCollectionResource(List collect, String listName) {
      return new CollectionResource(uriInfo, collect, listName, collect.size());
   }

   private List getWorks() {
      Collection<List<Work>> values = publisher.getWorks().values();
      return values.stream().map(getMapFunctionForWorks()).collect(Collectors.toList());
   }

   private List getReviews() {
      Set<Map.Entry<Work, List<Review>>> values = publisher.getReviews().entrySet();
      return values.stream().map(queryBean.getExpand().contains("key") ? getMapFunctionForReviewsExpanded() : getMapFunctionForReviews()).collect(Collectors.toList());
   }

   private Function<List<Work>, List<BookResource>> getMapFunctionForWorks() {
      return works -> works.stream().map(work -> new BookResource(uriInfo, work, queryBean.getExpand())).collect(Collectors.toList());
   }

   private Function<Map.Entry<Work, List<Review>>, SimpleEntry<LinkedResource, List<Review>>> getMapFunctionForReviews() {
      return entry -> new SimpleEntry<>(new LinkedResource(uriInfo.getBaseUriBuilder().clone().path(WorksController.class).path(entry.getKey().getId()).build()), entry.getValue());
   }

   private Function<Map.Entry<Work, List<Review>>, SimpleEntry<BookResource, List<Review>>> getMapFunctionForReviewsExpanded() {
      return entry -> new SimpleEntry<>(new BookResource(uriInfo, entry.getKey(), queryBean.getExpand()), entry.getValue());
   }

   public CollectionResource getWorksResource() {
      return createCollectionResource(getWorks(), "work");
   }

   public CollectionResource getReviewsResource() {
      return createCollectionResource(getReviews(), "review");
   }
}
