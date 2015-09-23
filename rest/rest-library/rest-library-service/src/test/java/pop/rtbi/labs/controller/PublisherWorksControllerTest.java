package pop.rtbi.labs.controller;

import com.murex.rtbi.CollectionResource;
import org.junit.After;
import org.junit.Test;
import pop.rtbi.labs.*;
import pop.rtbi.labs.exception.ConflictException;
import pop.rtbi.labs.model.post.PostPublisher;
import pop.rtbi.labs.model.post.PostWork;
import pop.rtbi.labs.model.query.DefaultQueryBean;
import pop.rtbi.labs.resource.BookResource;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

/**
 * Created with IntelliJ IDEA.
 * User: ycai
 * Date: 12/08/2015
 * Time: 15:28
 */
public class PublisherWorksControllerTest extends AbstractServiceTest {

   private final PublisherWorksController publisherWorksController;
   private final PublisherDAO publisherDAO;
   private final AuthorDAO authorDAO;
   private final WorkDAO workDAO;

   private static final PostPublisher POST_PUBLISHER_1 = new PostPublisher("1");
   private static final Publisher PUBLISHER_1 = POST_PUBLISHER_1.getPublisher();

   private static final IAuthor NOVELIST_1 = new Novelist("1");
   private static final IAuthor NOVELIST_2 = new Novelist("2");

   private static final Work BOOK_1_NOVELIST_1 = new Book("1", NOVELIST_1);
   private static final Work BOOK_2_NOVELIST_1 = new Book("2", NOVELIST_1);
   private static final Work BOOK_2_NOVELIST_2 = new Book("2", NOVELIST_2);

   private static final PostWork POST_WORK = new PostWork(BOOK_1_NOVELIST_1.getId());

   private DefaultQueryBean queryBean;
   private final UriInfo uriInfo;

   public PublisherWorksControllerTest() {
      authorDAO = AuthorDAO.INSTANCE;
      workDAO = WorkDAO.INSTANCE;
      publisherDAO = PublisherDAO.INSTANCE;

      UriBuilder uriBuilder = getServiceUriBuilder().path(PublishersController.class).path(PUBLISHER_1.getId()).path(PublisherWorksController.class);
      uriInfo = mockUriInfo(uriBuilder);
      publisherWorksController = new PublisherWorksController(uriInfo, PUBLISHER_1.getId());
      queryBean = new DefaultQueryBean();
   }

   private List<BookResource> expectedWorks(Work... works) {
      List<BookResource> resources = new ArrayList<>();
      for (Work work : works) {
         resources.add(new BookResource(uriInfo, work, queryBean.getExpand()));
      }
      return resources;
   }

   private void populateWorks(Work... work) {
      for (Work w : work) {
         workDAO.createBook(w);
      }
   }

   private void populateAuthors(IAuthor... author) {
      for (IAuthor a : author) {
         authorDAO.createAuthor(a);
      }
   }

   private void publishWork() {
      PUBLISHER_1.addWork(NOVELIST_1, BOOK_1_NOVELIST_1);
      PUBLISHER_1.addWork(NOVELIST_1, BOOK_2_NOVELIST_1);
      addPublisher1();
   }

   private String addPublisher1() {
      return publisherDAO.createPublisher(PUBLISHER_1);
   }

   @Override
   @After
   public void tearDown() {
      super.tearDown();
      PUBLISHER_1.getWorks().clear();
   }

   @Test
   public void publisherWorksControllerGetMethodShouldReturnACompleteOKResponse() {
      addPublisher1();

      Response getResponse = publisherWorksController.getPublishedWorks(queryBean);
      CollectionResource collectionResource = (CollectionResource) getResponse.getEntity();

      assertThat(getResponse.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
      assertThat(collectionResource.get("offset")).isNotNull();
      assertThat(collectionResource.get("limit")).isNotNull();
      assertThat(collectionResource.get("totalCount")).isNotNull();
   }

   @Test
   public void publisherWorksControllerGetMethodShouldReturnTheListOfBooksPublishedByThePublisher() {
      populateAuthors(NOVELIST_1);
      populateWorks(BOOK_1_NOVELIST_1);
      PUBLISHER_1.addWork(NOVELIST_1, BOOK_1_NOVELIST_1);
      addPublisher1();

      Response getResponse = publisherWorksController.getPublishedWorks(queryBean);
      CollectionResource collectionResource = (CollectionResource) getResponse.getEntity();

      List works = (List) collectionResource.get("work");
      assertThat(works).isNotEmpty();
      assertThat(works).containsExactly(expectedWorks(BOOK_1_NOVELIST_1));
   }

   @Test
   public void publisherWorksControllerPostWorkShouldReturnACreatedResponseWithTheUpdatedList() {
      populateAuthors(NOVELIST_1);
      populateWorks(BOOK_1_NOVELIST_1);
      addPublisher1();

      Response postResponse = publisherWorksController.publishWork(POST_WORK);
      assertThat(postResponse.getStatus()).isEqualTo(Response.Status.CREATED.getStatusCode());

      CollectionResource publisherWorkResource = (CollectionResource) postResponse.getEntity();
      List works = (List) publisherWorkResource.get("work");
      assertThat(works).isNotEmpty();

      assertThat(works).containsExactly(expectedWorks(BOOK_1_NOVELIST_1));
   }

   @Test(expected = ConflictException.class)
   public void publisherWorksControllerPostAlreadyPublishedWorkShouldReturnAConflictException() {
      populateAuthors(NOVELIST_1);
      populateWorks(BOOK_1_NOVELIST_1);
      addPublisher1();
      publisherWorksController.publishWork(POST_WORK);
      publisherWorksController.publishWork(POST_WORK);
   }

   @Test(expected = NotFoundException.class)
   public void publisherWorksControllerPostWorkExistingNovelistButNotExistingBookShouldReturnANotFoundException() {
      populateAuthors(NOVELIST_1);
      addPublisher1();
      publisherWorksController.publishWork(POST_WORK);
   }

   @Test
   public void publisherWorksControllerPostTwoWorksOfTwoAuthorsShouldReturnAMapOfTwoEntries() {
      populateAuthors(NOVELIST_1, NOVELIST_2);
      populateWorks(BOOK_1_NOVELIST_1, BOOK_2_NOVELIST_2);
      PUBLISHER_1.addWork(NOVELIST_1, BOOK_1_NOVELIST_1);
      PUBLISHER_1.addWork(NOVELIST_2, BOOK_2_NOVELIST_2);
      addPublisher1();

      Response getResponse = publisherWorksController.getPublishedWorks(queryBean);

      CollectionResource publisherWorkResource = (CollectionResource) getResponse.getEntity();
      List works = (List) publisherWorkResource.get("work");

      assertThat(works).hasSize(2);

      assertThat(works).containsExactly(expectedWorks(BOOK_1_NOVELIST_1), expectedWorks(BOOK_2_NOVELIST_2));
   }

   @Test
   public void publisherWorksControllerPostTwoWorksOfOneAuthorShouldReturnAMapOfOneEntryOfAListOfTwoItem() {
      populateAuthors(NOVELIST_1);
      populateWorks(BOOK_1_NOVELIST_1, BOOK_2_NOVELIST_1);
      publishWork();

      Response getResponse = publisherWorksController.getPublishedWorks(queryBean);

      CollectionResource publisherWorkResource = (CollectionResource) getResponse.getEntity();
      List works = (List) publisherWorkResource.get("work");

      assertThat(works).containsExactly(expectedWorks(BOOK_1_NOVELIST_1, BOOK_2_NOVELIST_1));
   }

   @Test
   public void publisherWorksControllerGetWorksForAAuthorShouldReturnAMapOfOneEntryOfAListOfTwoItem() {
      populateAuthors(NOVELIST_1);
      populateWorks(BOOK_1_NOVELIST_1, BOOK_2_NOVELIST_1);
      publishWork();

      Response getResponse = publisherWorksController.getAuthorPublishedWorks(NOVELIST_1.getId());

      CollectionResource publisherWorkResource = (CollectionResource) getResponse.getEntity();
      List works = (List) publisherWorkResource.get("work");

      assertThat(works).containsExactly(BOOK_1_NOVELIST_1, BOOK_2_NOVELIST_1);
   }

   @Test
   public void publisherWorksControllerDeleteWorkForAAuthorShouldReturnOkResponse() {
      populateAuthors(NOVELIST_1);
      populateWorks(BOOK_1_NOVELIST_1, BOOK_2_NOVELIST_1);
      publishWork();

      publisherWorksController.deleteAuthorPublishedWork(NOVELIST_1.getId(), BOOK_1_NOVELIST_1.getId());
      Response getResponse = publisherWorksController.getAuthorPublishedWorks(NOVELIST_1.getId());

      CollectionResource publisherWorkResource = (CollectionResource) getResponse.getEntity();
      List works = (List) publisherWorkResource.get("work");

      assertThat(works).containsExactly(BOOK_2_NOVELIST_1);
   }
}
