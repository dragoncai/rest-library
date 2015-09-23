package pop.rtbi.labs.controller;

import com.google.common.collect.Lists;
import com.murex.rtbi.CollectionResource;
import org.junit.After;
import org.junit.Test;
import pop.rtbi.labs.AbstractServiceTest;
import pop.rtbi.labs.AuthorDAO;
import pop.rtbi.labs.IAuthor;
import pop.rtbi.labs.Novelist;
import pop.rtbi.labs.exception.ConflictException;
import pop.rtbi.labs.model.post.PostAuthor;
import pop.rtbi.labs.model.query.AuthorsQueryBean;
import pop.rtbi.labs.resource.AuthorResource;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.fest.assertions.Assertions.assertThat;

/**
 * Created with IntelliJ IDEA.
 * User: ycai
 * Date: 04/08/2015
 * Time: 16:55
 */
public class AuthorsControllerTest extends AbstractServiceTest {
   private final UriBuilder uriBuilder;
   private final UriInfo uriInfo;

   private AuthorsController authorsController;
   private AuthorsQueryBean queryBean;

   public static final PostAuthor POST_AUTHOR = new PostAuthor("1", "novelist");
   private static final Novelist NOVELIST_1 = (Novelist) POST_AUTHOR.getAuthor();
   private static final Novelist NOVELIST_2 = new Novelist("2");
   private static final Novelist NOVELIST_3 = new Novelist("3");
   private static final Novelist NOVELIST_4 = new Novelist("4");
   private static final Novelist NOVELIST_5 = new Novelist("5");
   private AuthorDAO authorDAO;

   public AuthorsControllerTest() {
      authorDAO = AuthorDAO.INSTANCE;
      authorsController = new AuthorsController();
      uriBuilder = getServiceUriBuilder().path(AuthorsController.class);
      uriInfo = mockUriInfo(uriBuilder);
      authorsController.setUriInfo(uriInfo);
      queryBean = new AuthorsQueryBean();
   }

   private void populateAuthors() {
      authorDAO.createAuthor(NOVELIST_3);
      authorDAO.createAuthor(NOVELIST_5);
      authorDAO.createAuthor(NOVELIST_2);
      authorDAO.createAuthor(NOVELIST_1);
      authorDAO.createAuthor(NOVELIST_4);
   }

   private List<AuthorResource> sortedNovelistList() {
      List<AuthorResource> authorResources = new ArrayList<>();
      authorResources.add(new AuthorResource(uriInfo, NOVELIST_1));
      authorResources.add(new AuthorResource(uriInfo, NOVELIST_2));
      authorResources.add(new AuthorResource(uriInfo, NOVELIST_3));
      authorResources.add(new AuthorResource(uriInfo, NOVELIST_4));
      authorResources.add(new AuthorResource(uriInfo, NOVELIST_5));
      return authorResources;
   }

   private void createQueryBean(int offset, int limit) {
      queryBean.setOffset(offset);
      queryBean.setLimit(limit);
   }

   private void assertCollectionResourceLinks(CollectionResource entity, String relation, String offset, String limit) {
      assertThat(entity.get(relation)).isEqualTo(uriBuilder.clone().replaceQueryParam("limit", limit).replaceQueryParam("offset", offset).build().toString());
   }

   @Override
   @After
   public void tearDown() {
      super.tearDown();
      queryBean = new AuthorsQueryBean();
   }

   @Test
   public void authorsControllerGetMethodShouldReturnACompleteOKResponse() {
      Response response = authorsController.getAuthors(queryBean);
      CollectionResource entity = (CollectionResource) response.getEntity();

      assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
      assertThat(entity.get("offset")).isNotNull();
      assertThat(entity.get("limit")).isNotNull();
      assertThat(entity.get("totalCount")).isNotNull();
   }

   @Test
   public void authorsControllerPostNovelistShouldReturnACreatedResponseWithTheAuthor() {
      Response response = authorsController.postAuthor(POST_AUTHOR);
      assertThat(response.getStatus()).isEqualTo(Response.Status.CREATED.getStatusCode());
      AuthorResource authorResource = (AuthorResource) response.getEntity();
      Map authorEntityMap = authorResource.getEntity(Map.class);
      assertThat(authorEntityMap.get(IAuthor.ID)).isEqualTo(NOVELIST_1.getId());
      assertThat(authorEntityMap.get(IAuthor.NAME)).isEqualTo(NOVELIST_1.getName());
   }

   @Test(expected = ConflictException.class)
   public void authorsControllerPostExistingNovelistShouldReturnAException() {
      authorsController.postAuthor(POST_AUTHOR);
      authorsController.postAuthor(POST_AUTHOR);
   }

   @Test
   public void authorsControllerPostJournalistShouldReturnAJournalist() throws Exception {
      PostAuthor journalist = new PostAuthor("1", "Journalist");
      Response postResponse = authorsController.postAuthor(journalist);
      assertThat(postResponse.getStatus()).isEqualTo(Response.Status.CREATED.getStatusCode());

      AuthorResource authorResource = (AuthorResource) postResponse.getEntity();
      Map authorEntityMap = authorResource.getEntity(Map.class);
      assertThat(authorEntityMap.get(IAuthor.ID)).isEqualTo(journalist.getAuthor().getId());
      assertThat(authorEntityMap.get(IAuthor.NAME)).isEqualTo(journalist.getAuthor().getName());
      assertThat(authorEntityMap.get("type")).isEqualTo("journalist");
   }

   @Test
   public void authorsControllerGetMethodShouldReturnAListOfAuthors() {
      authorsController.postAuthor(POST_AUTHOR);

      List<AuthorResource> expected = new ArrayList<>();
      expected.add(new AuthorResource(uriInfo, NOVELIST_1));

      Response response = authorsController.getAuthors(queryBean);
      CollectionResource entity = (CollectionResource) response.getEntity();
      List actual = (List) entity.get("author");

      assertThat(entity.get("totalCount")).isEqualTo(1);
      assertThat(actual).hasSize(1);
      assertThat(actual).isEqualTo(expected);
   }

   @Test
   public void authorsControllerGetMethodWith5AndLimitOf1NovelistShouldReturnAPaginatedResourceWithOnly1Author() {
      populateAuthors();

      createQueryBean(0, 1);

      Response response = authorsController.getAuthors(queryBean);
      CollectionResource entity = (CollectionResource) response.getEntity();
      List actual = (List) entity.get("author");

      assertThat(entity.get("totalCount")).isEqualTo(5);
      assertThat(actual).hasSize(1);
   }

   @Test
   public void authorsControllerGetMethodWith5AndOffsetOf1ShouldReturnAPaginatedResourceWithOnly4AuthorStartingFromTheSecond() {
      populateAuthors();
      createQueryBean(1, 10);

      Response response = authorsController.getAuthors(queryBean);
      CollectionResource entity = (CollectionResource) response.getEntity();
      List actual = (List) entity.get("author");
      AuthorResource authorResource = (AuthorResource) actual.get(0);

      assertThat(entity.get("totalCount")).isEqualTo(5);
      assertThat(actual).hasSize(4);
      assertThat(authorResource.getEntity(Map.class).get("id")).isEqualTo(NOVELIST_5.getId());
   }

   @Test
   public void authorsControllerGetMethodWithTotalCountLessThanLimitShouldReturnNoPaginationWithoutLinks() {
      populateAuthors();

      Response response = authorsController.getAuthors(queryBean);
      CollectionResource entity = (CollectionResource) response.getEntity();

      assertThat(entity.get("next")).isNull();
      assertThat(entity.get("prev")).isNull();
   }

   @Test
   public void authorsControllerGetMethodWith20NovelistShouldReturnAPaginatedResourceALinkToTheNextPageButNoLinkToPrevious() {
      populateAuthors();
      createQueryBean(0, 1);

      Response response = authorsController.getAuthors(queryBean);
      CollectionResource entity = (CollectionResource) response.getEntity();

      assertCollectionResourceLinks(entity, "next", "1", "1");
      assertThat(entity.get("prev")).isNull();
   }

   @Test
   public void authorsControllerGetMethodWith20NovelistStartingFrom10ShouldReturnAPaginatedResourceALinkToThePreviousPageButNoLinkToNext() {
      populateAuthors();
      createQueryBean(4, 1);

      Response response = authorsController.getAuthors(queryBean);
      CollectionResource entity = (CollectionResource) response.getEntity();

      assertCollectionResourceLinks(entity, "prev", "3", "1");
      assertThat(entity.get("next")).isNull();
   }

   @Test
   public void authorsControllerGetMethodWith20NovelistStartingFrom1ShouldReturnAPaginatedResourceALinkToThePreviousPageAndLinkToNext() {
      populateAuthors();
      createQueryBean(1, 1);

      Response response = authorsController.getAuthors(queryBean);
      CollectionResource entity = (CollectionResource) response.getEntity();

      assertCollectionResourceLinks(entity, "prev", "0", "1");
      assertCollectionResourceLinks(entity, "next", "2", "1");
   }

   @Test
   public void authorsControllerGetMethodWithSortingAscendingNameShouldReturnASortedResource() {
      List<AuthorResource> expected = sortedNovelistList();

      populateAuthors();

      queryBean.setSortBy("name-asc");

      Response response = authorsController.getAuthors(queryBean);
      CollectionResource entity = (CollectionResource) response.getEntity();
      List actual = (List) entity.get("author");

      assertThat(actual).isEqualTo(expected);
   }

   @Test
   public void authorsControllerGetMethodWithSortingDescendingNameShouldReturnASortedResource() {
      List<AuthorResource> expected = Lists.reverse(sortedNovelistList());

      populateAuthors();

      queryBean.setSortBy("name-desc");

      Response response = authorsController.getAuthors(queryBean);
      CollectionResource entity = (CollectionResource) response.getEntity();
      List actual = (List) entity.get("author");

      assertThat(actual).isEqualTo(expected);
   }

   @Test
   public void authorsControllerGetMethodWithFilteringNameContainingTheNumber1ShouldReturnAFilteredResource() {
      List<AuthorResource> expected = new ArrayList<>();
      expected.add(new AuthorResource(uriInfo, NOVELIST_1));

      populateAuthors();

      queryBean.addName("1");

      Response response = authorsController.getAuthors(queryBean);
      CollectionResource entity = (CollectionResource) response.getEntity();
      List actual = (List) entity.get("author");

      assertThat(actual).isEqualTo(expected);
   }
}
