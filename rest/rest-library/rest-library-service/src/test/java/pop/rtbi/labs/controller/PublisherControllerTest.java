package pop.rtbi.labs.controller;

import org.junit.Test;
import pop.rtbi.labs.AbstractServiceTest;
import pop.rtbi.labs.Publisher;
import pop.rtbi.labs.PublisherDAO;
import pop.rtbi.labs.model.post.PostPublisher;
import pop.rtbi.labs.model.query.DefaultQueryBean;
import pop.rtbi.labs.resource.PublisherResource;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import static org.fest.assertions.Assertions.assertThat;

/**
 * Created with IntelliJ IDEA.
 * User: ycai
 * Date: 11/08/2015
 * Time: 13:51
 */
public class PublisherControllerTest extends AbstractServiceTest {
   private final UriInfo uriInfo;

   private final PublisherController publisherController1;
   private final PublisherDAO publisherDAO;

   private static final PostPublisher POST_PUBLISHER_1 = new PostPublisher("1");

   private static final Publisher PUBLISHER_1 = POST_PUBLISHER_1.getPublisher();
   private DefaultQueryBean queryBean;

   public PublisherControllerTest() {
      publisherDAO = PublisherDAO.INSTANCE;
      UriBuilder uriBuilder = getServiceUriBuilder().path(PublishersController.class).path(PUBLISHER_1.getId());
      uriInfo = mockUriInfo(uriBuilder);
      publisherController1 = new PublisherController(uriInfo, PUBLISHER_1.getId());
      queryBean = new DefaultQueryBean();
   }

   @Test
   public void publisherControllerGetPublisherShouldReturnThePublisherResourceWithOkResponse() {
      publisherDAO.createPublisher(PUBLISHER_1);

      Response getResponse = publisherController1.getPublisher(queryBean);
      assertThat(getResponse.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());

      PublisherResource publisherResource = (PublisherResource) getResponse.getEntity();
      assertThat(publisherResource).isEqualTo(new PublisherResource(uriInfo, PUBLISHER_1, queryBean));
   }

   @Test(expected = NotFoundException.class)
   public void publisherControllerGetNonExistingPublisherShouldThrowAnNotFoundException() {
      publisherController1.getPublisher(queryBean);
   }

   @Test
   public void publisherControllerDeleteAnPublisherShouldReturnAOkResponseAndTheObjectShouldBeDeleted() {
      publisherDAO.createPublisher(PUBLISHER_1);
      Response deleteResponse = publisherController1.deletePublisher();
      assertThat(deleteResponse.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
      assertThat(publisherDAO.readPublisher(PUBLISHER_1.getId())).isNull();
   }

   @Test(expected = NotFoundException.class)
   public void publisherControllerDeleteNonExistingPublisherShouldThrowAnNotFoundException() {
      publisherController1.deletePublisher();
   }
}