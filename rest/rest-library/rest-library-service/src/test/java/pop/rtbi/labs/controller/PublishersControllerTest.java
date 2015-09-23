package pop.rtbi.labs.controller;

import com.murex.rtbi.CollectionResource;
import org.junit.After;
import org.junit.Test;
import pop.rtbi.labs.AbstractServiceTest;
import pop.rtbi.labs.PublisherDAO;
import pop.rtbi.labs.exception.ConflictException;
import pop.rtbi.labs.model.post.PostPublisher;
import pop.rtbi.labs.model.query.DefaultQueryBean;
import pop.rtbi.labs.resource.PublisherResource;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

/**
 * Created with IntelliJ IDEA.
 * User: ycai
 * Date: 11/08/2015
 * Time: 13:51
 */
public class PublishersControllerTest extends AbstractServiceTest {

   public static final PostPublisher POST_PUBLISHER_1 = new PostPublisher("1");
   public static final PostPublisher POST_PUBLISHER_2 = new PostPublisher("2");
   public static final PostPublisher POST_PUBLISHER_3 = new PostPublisher("3");
   public static final PostPublisher POST_PUBLISHER_4 = new PostPublisher("4");
   public static final PostPublisher POST_PUBLISHER_5 = new PostPublisher("5");
   private final UriBuilder uriBuilder;
   private final UriInfo uriInfo;

   private DefaultQueryBean queryBean;

   private final PublishersController publishersController;

   public PublishersControllerTest() {
      publishersController = new PublishersController();
      uriBuilder = getServiceUriBuilder().path(PublishersController.class);
      uriInfo = mockUriInfo(uriBuilder);
      publishersController.setUriInfo(uriInfo);
      queryBean = new DefaultQueryBean();
   }

   private void createQueryBean(int offset, int limit) {
      queryBean.setOffset(offset);
      queryBean.setLimit(limit);
   }

   private void assertCollectionResourceLinks(CollectionResource entity, String relation, String offset, String limit) {
      assertThat(entity.get(relation)).isEqualTo(uriBuilder.clone().replaceQueryParam("limit", limit).replaceQueryParam("offset", offset).build().toString());
   }

   private void populatePublishers() {
      PublisherDAO publisherDAO = PublisherDAO.INSTANCE;
      publisherDAO.createPublisher(POST_PUBLISHER_3.getPublisher());
      publisherDAO.createPublisher(POST_PUBLISHER_5.getPublisher());
      publisherDAO.createPublisher(POST_PUBLISHER_2.getPublisher());
      publisherDAO.createPublisher(POST_PUBLISHER_1.getPublisher());
      publisherDAO.createPublisher(POST_PUBLISHER_4.getPublisher());
   }

   private PublisherResource getNewPublisherResource(PostPublisher postPublisher) {
      return new PublisherResource(uriInfo, postPublisher.getPublisher(), queryBean);
   }

   @Override
   @After
   public void tearDown() {
      super.tearDown();
      queryBean = new DefaultQueryBean();
   }

   @Test
   public void publishersControllerGetMethodShouldReturnACompleteOKResponse() {
      Response response = publishersController.getPublishers(queryBean);
      CollectionResource entity = (CollectionResource) response.getEntity();

      assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
      assertThat(entity.get("offset")).isNotNull();
      assertThat(entity.get("limit")).isNotNull();
      assertThat(entity.get("totalCount")).isNotNull();
   }

   @Test
   public void publishersControllerPostPublisherShouldReturnACreatedResponseWithThePublisherResource() {
      Response postResponse = publishersController.postPublisher(POST_PUBLISHER_1);
      assertThat(postResponse.getStatus()).isEqualTo(Response.Status.CREATED.getStatusCode());

      PublisherResource publisherResource = (PublisherResource) postResponse.getEntity();
      assertThat(publisherResource).isEqualTo(getNewPublisherResource(POST_PUBLISHER_1));
   }

   @Test(expected = ConflictException.class)
   public void publishersControllerPostExistingPublisherShouldReturnAException() {
      publishersController.postPublisher(POST_PUBLISHER_1);
      publishersController.postPublisher(POST_PUBLISHER_1);
   }


   @Test
   public void authorsControllerGetMethodShouldReturnAListOfPublishers() {
      publishersController.postPublisher(POST_PUBLISHER_1);

      List<PublisherResource> expected = new ArrayList<>();
      expected.add(getNewPublisherResource(POST_PUBLISHER_1));

      Response response = publishersController.getPublishers(queryBean);
      CollectionResource entity = (CollectionResource) response.getEntity();
      List actual = (List) entity.get("publisher");

      assertThat(entity.get("totalCount")).isEqualTo(1);
      assertThat(actual).hasSize(1);
      assertThat(actual).isEqualTo(expected);
   }

   @Test
   public void authorsControllerGetMethodWith5AndLimitOf1NovelistShouldReturnAPaginatedResourceWithOnly1Publisher() {
      populatePublishers();

      createQueryBean(0, 1);

      Response response = publishersController.getPublishers(queryBean);
      CollectionResource entity = (CollectionResource) response.getEntity();
      List actual = (List) entity.get("publisher");

      assertThat(entity.get("totalCount")).isEqualTo(5);
      assertThat(actual).hasSize(1);
   }

   @Test
   public void authorsControllerGetMethodWith5AndOffsetOf1ShouldReturnAPaginatedResourceWithOnly4PublisherStartingFromTheSecond() {
      populatePublishers();
      createQueryBean(1, 10);

      Response response = publishersController.getPublishers(queryBean);
      CollectionResource entity = (CollectionResource) response.getEntity();
      List actual = (List) entity.get("publisher");

      assertThat(entity.get("totalCount")).isEqualTo(5);
      assertThat(actual).hasSize(4);
      assertThat(actual).containsExactly(getNewPublisherResource(POST_PUBLISHER_5), getNewPublisherResource(POST_PUBLISHER_2), getNewPublisherResource(POST_PUBLISHER_1), getNewPublisherResource(POST_PUBLISHER_4));
   }

   @Test
   public void authorsControllerGetMethodWithTotalCountLessThanLimitShouldReturnNoPaginationWithoutLinks() {
      populatePublishers();

      Response response = publishersController.getPublishers(queryBean);
      CollectionResource entity = (CollectionResource) response.getEntity();

      assertThat(entity.get("next")).isNull();
      assertThat(entity.get("prev")).isNull();
   }

   @Test
   public void authorsControllerGetMethodWith20NovelistShouldReturnAPaginatedResourceALinkToTheNextPageButNoLinkToPrevious() {
      populatePublishers();
      createQueryBean(0, 1);

      Response response = publishersController.getPublishers(queryBean);
      CollectionResource entity = (CollectionResource) response.getEntity();

      assertCollectionResourceLinks(entity, "next", "1", "1");
      assertThat(entity.get("prev")).isNull();
   }

   @Test
   public void publishersControllerGetMethodWith20NovelistStartingFrom10ShouldReturnAPaginatedResourceALinkToThePreviousPageButNoLinkToNext() {
      populatePublishers();
      createQueryBean(4, 1);

      Response response = publishersController.getPublishers(queryBean);
      CollectionResource entity = (CollectionResource) response.getEntity();

      assertCollectionResourceLinks(entity, "prev", "3", "1");
      assertThat(entity.get("next")).isNull();
   }

   @Test
   public void authorsControllerGetMethodWith20NovelistStartingFrom1ShouldReturnAPaginatedResourceALinkToThePreviousPageAndLinkToNext() {
      populatePublishers();
      createQueryBean(1, 1);

      Response response = publishersController.getPublishers(queryBean);
      CollectionResource entity = (CollectionResource) response.getEntity();

      assertCollectionResourceLinks(entity, "prev", "0", "1");
      assertCollectionResourceLinks(entity, "next", "2", "1");
   }
}