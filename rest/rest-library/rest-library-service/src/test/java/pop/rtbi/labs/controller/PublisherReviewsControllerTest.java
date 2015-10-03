package pop.rtbi.labs.controller;

import com.murex.rtbi.CollectionResource;
import com.murex.rtbi.LinkedResource;
import org.junit.After;
import org.junit.Test;
import pop.rtbi.labs.*;
import pop.rtbi.labs.model.post.PostPublisher;
import pop.rtbi.labs.model.post.PostReview;
import pop.rtbi.labs.model.query.DefaultQueryBean;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

/**
 * Created with IntelliJ IDEA.
 * User: ycai
 * Date: 13/08/2015
 * Time: 14:34
 */
public class PublisherReviewsControllerTest extends AbstractServiceTest {
   public static final Review REVIEW_SOSO = new Review(3, "So-so");
   private final PublisherReviewsController publisherReviewsController;
   private final PublisherDAO publisherDAO;
   private final WorkDAO workDAO;

   private static final PostPublisher POST_PUBLISHER_1 = new PostPublisher("1");
   private static final Publisher PUBLISHER_1 = POST_PUBLISHER_1.getPublisher();

   private static final IAuthor NOVELIST_1 = new Novelist("1");

   private static final Work BOOK_1_NOVELIST_1 = new Book("1", NOVELIST_1);
   private static final Work BOOK_2_NOVELIST_1 = new Book("2", NOVELIST_1);

   private static final Review REVIEW_GREAT = new Review(5, "GREAT");
   private static final PostReview POST_REVIEW = new PostReview(BOOK_1_NOVELIST_1.getId(), REVIEW_GREAT);

   private DefaultQueryBean queryBean;
   private final UriInfo uriInfo;

   public PublisherReviewsControllerTest() {
      workDAO = WorkDAO.INSTANCE;
      publisherDAO = PublisherDAO.INSTANCE;

      UriBuilder uriBuilder = getServiceUriBuilder().path(PublishersController.class).path(PUBLISHER_1.getId()).path(PublisherReviewsController.class);
      uriInfo = mockUriInfo(uriBuilder);
      publisherReviewsController = new PublisherReviewsController(uriInfo, PUBLISHER_1.getId());
      queryBean = new DefaultQueryBean();
      PUBLISHER_1.addWork(NOVELIST_1, BOOK_1_NOVELIST_1);
      PUBLISHER_1.addWork(NOVELIST_1, BOOK_2_NOVELIST_1);

   }

   private List<Review> createListOfReviews(Review... reviews) {
      List<Review> list = new ArrayList<>();
      Collections.addAll(list, reviews);
      return list;
   }

   private AbstractMap.SimpleEntry<LinkedResource, List<Review>> expectedReviews(Work work, Review... reviews) {
       UriBuilder path = uriInfo.getBaseUriBuilder().clone().path(WorksController.class).path(work.getId());
       return new AbstractMap.SimpleEntry<>(new LinkedResource(path.build()), createListOfReviews(reviews));
   }

   private void populateWorks(Work... work) {
      for (Work w : work) {
         workDAO.createBook(w);
      }
   }

   private void populateTwoReviewsForOneBook() {
      populateWorks(BOOK_1_NOVELIST_1);
      PUBLISHER_1.addReview(BOOK_1_NOVELIST_1, REVIEW_GREAT);
      PUBLISHER_1.addReview(BOOK_1_NOVELIST_1, REVIEW_SOSO);
      addPublisher1();
   }

   @Override
   @After
   public void tearDown() {
      super.tearDown();
      PUBLISHER_1.getReviews().clear();
      queryBean = new DefaultQueryBean();
   }

   @Test
   public void publisherReviewsControllerGetMethodShouldReturnACompleteOKResponse() {
      addPublisher1();

      Response getResponse = publisherReviewsController.getPublishedReviews(queryBean);
      CollectionResource collectionResource = (CollectionResource) getResponse.getEntity();

      assertThat(getResponse.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
      assertThat(collectionResource.get("offset")).isNotNull();
      assertThat(collectionResource.get("limit")).isNotNull();
      assertThat(collectionResource.get("totalCount")).isNotNull();
   }

   @Test
   public void publisherReviewsControllerGetMethodShouldReturnTheListOfReviewsPublishedByThePublisher() {
      populateWorks(BOOK_1_NOVELIST_1);
      PUBLISHER_1.addReview(BOOK_1_NOVELIST_1, REVIEW_GREAT);
      addPublisher1();

      Response getResponse = publisherReviewsController.getPublishedReviews(queryBean);
      CollectionResource collectionResource = (CollectionResource) getResponse.getEntity();

      List works = (List) collectionResource.get("review");
      assertThat(works).isNotEmpty();
      assertThat(works).containsExactly(expectedReviews(BOOK_1_NOVELIST_1, REVIEW_GREAT));
   }

   @Test
   public void publisherReviewsControllerPostReviewShouldReturnACreatedResponseWithTheUpdatedList() {
      populateWorks(BOOK_1_NOVELIST_1);
      addPublisher1();

      Response postResponse = publisherReviewsController.publishReview(POST_REVIEW);
      assertThat(postResponse.getStatus()).isEqualTo(Response.Status.CREATED.getStatusCode());

      CollectionResource publisherWorkResource = (CollectionResource) postResponse.getEntity();
      List works = (List) publisherWorkResource.get("review");
      assertThat(works).isNotEmpty();

      assertThat(works).containsExactly(expectedReviews(BOOK_1_NOVELIST_1, REVIEW_GREAT));
   }

   @Test(expected = NotFoundException.class)
   public void publisherReviewsControllerPostReviewWithNotExistingBookShouldReturnANotFoundException() {
      addPublisher1();
      publisherReviewsController.publishReview(POST_REVIEW);
   }

   private String addPublisher1() {
      return publisherDAO.createPublisher(PUBLISHER_1);
   }

   @Test
   public void publisherReviewsControllerPostTwoWorksOfTwoAuthorsShouldReturnAMapOfTwoEntries() {
      populateWorks(BOOK_1_NOVELIST_1, BOOK_2_NOVELIST_1);
      PUBLISHER_1.addReview(BOOK_1_NOVELIST_1, REVIEW_GREAT);
      PUBLISHER_1.addReview(BOOK_2_NOVELIST_1, REVIEW_GREAT);
      addPublisher1();

      Response getResponse = publisherReviewsController.getPublishedReviews(queryBean);

      CollectionResource publisherWorkResource = (CollectionResource) getResponse.getEntity();
      List works = (List) publisherWorkResource.get("review");

      assertThat(works).hasSize(2);

      assertThat(works).containsExactly(expectedReviews(BOOK_1_NOVELIST_1, REVIEW_GREAT), expectedReviews(BOOK_2_NOVELIST_1, REVIEW_GREAT));
   }

   @Test
   public void publisherReviewsControllerPostTwoWorksOfOneAuthorShouldReturnAMapOfOneEntryOfAListOfTwoItem() {
      populateTwoReviewsForOneBook();

      Response getResponse = publisherReviewsController.getPublishedReviews(queryBean);

      CollectionResource publisherWorkResource = (CollectionResource) getResponse.getEntity();
      List works = (List) publisherWorkResource.get("review");

      assertThat(works).containsExactly(expectedReviews(BOOK_1_NOVELIST_1, REVIEW_GREAT, REVIEW_SOSO));
   }

   @Test
   public void publisherReviewsControllerGetWorksForAAuthorShouldReturnAMapOfOneEntryOfAListOfTwoItem() {
      populateTwoReviewsForOneBook();

      Response getResponse = publisherReviewsController.getWorkReviews(BOOK_1_NOVELIST_1.getId());

      CollectionResource publisherWorkResource = (CollectionResource) getResponse.getEntity();
      List works = (List) publisherWorkResource.get("review");

      assertThat(works).containsExactly(REVIEW_GREAT, REVIEW_SOSO);
   }

   @Test
   public void publisherReviewsControllerDeleteWorkForAAuthorShouldReturnOkResponse() {
      populateTwoReviewsForOneBook();

      Response deleteResponse = publisherReviewsController.deleteWorkReview(BOOK_1_NOVELIST_1.getId(), 0);
      assertThat(deleteResponse.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
      Response getResponse = publisherReviewsController.getWorkReviews(BOOK_1_NOVELIST_1.getId());

      CollectionResource publisherWorkResource = (CollectionResource) getResponse.getEntity();
      List works = (List) publisherWorkResource.get("review");

      assertThat(works).containsExactly(REVIEW_SOSO);
   }
}