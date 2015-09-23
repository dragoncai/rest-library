package pop.rtbi.labs.controller;

import com.google.common.collect.Lists;
import com.murex.rtbi.CollectionResource;
import com.murex.rtbi.LinkedResource;
import org.junit.After;
import org.junit.Test;
import pop.rtbi.labs.*;
import pop.rtbi.labs.exception.ConflictException;
import pop.rtbi.labs.model.query.WorksQueryBean;
import pop.rtbi.labs.resource.BookResource;

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
 * Date: 07/08/2015
 * Time: 14:34
 */
public class WorksControllerTest extends AbstractServiceTest {
   private final UriBuilder uriBuilder;
   private final UriInfo uriInfo;

   private final WorksController worksController;
   private final WorkDAO workDAO;
   private final AuthorDAO authorDAO;
   private WorksQueryBean queryBean;


   private static final Novelist NOVELIST_1 = new Novelist("1");
   private static final Novelist NOVELIST_2 = new Novelist("2");
   private static final Novelist NOVELIST_3 = new Novelist("3");
   private static final Novelist NOVELIST_4 = new Novelist("4");
   private static final Novelist NOVELIST_5 = new Novelist("5");

   private static final Book BOOK_1_NOVELIST_2 = new Book("1", NOVELIST_2);
   private static final Book BOOK_2_NOVELIST_3 = new Book("2", NOVELIST_3);
   private static final Book BOOK_3_NOVELIST_4 = new Book("3", NOVELIST_4);
   private static final Book BOOK_4_NOVELIST_5 = new Book("4", NOVELIST_5);
   private static final Book BOOK_5_NOVELIST_1 = new Book("5", NOVELIST_1);

   public WorksControllerTest() {
      worksController = new WorksController();
      uriBuilder = getServiceUriBuilder().path(WorksController.class);
      uriInfo = mockUriInfo(uriBuilder);
      worksController.setUriInfo(uriInfo);
      queryBean = new WorksQueryBean();
      workDAO = WorkDAO.INSTANCE;
      authorDAO = AuthorDAO.INSTANCE;
   }

   private void populateBooks() {
      workDAO.createBook(BOOK_3_NOVELIST_4);
      workDAO.createBook(BOOK_5_NOVELIST_1);
      workDAO.createBook(BOOK_2_NOVELIST_3);
      workDAO.createBook(BOOK_1_NOVELIST_2);
      workDAO.createBook(BOOK_4_NOVELIST_5);
   }

   private List<BookResource> sortedBookListByTitle() {
      List<BookResource> newAuthorResources = new ArrayList<>();
      newAuthorResources.add(new BookResource(uriInfo, BOOK_1_NOVELIST_2, null));
      newAuthorResources.add(new BookResource(uriInfo, BOOK_2_NOVELIST_3, null));
      newAuthorResources.add(new BookResource(uriInfo, BOOK_3_NOVELIST_4, null));
      newAuthorResources.add(new BookResource(uriInfo, BOOK_4_NOVELIST_5, null));
      newAuthorResources.add(new BookResource(uriInfo, BOOK_5_NOVELIST_1, null));
      return newAuthorResources;
   }

   private List<BookResource> sortedBookListByAuthor() {
      List<BookResource> newAuthorResources = new ArrayList<>();
      newAuthorResources.add(new BookResource(uriInfo, BOOK_5_NOVELIST_1, null));
      newAuthorResources.add(new BookResource(uriInfo, BOOK_1_NOVELIST_2, null));
      newAuthorResources.add(new BookResource(uriInfo, BOOK_2_NOVELIST_3, null));
      newAuthorResources.add(new BookResource(uriInfo, BOOK_3_NOVELIST_4, null));
      newAuthorResources.add(new BookResource(uriInfo, BOOK_4_NOVELIST_5, null));
      return newAuthorResources;
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
      queryBean = new WorksQueryBean();
   }

   @Test
   public void worksControllerGetMethodShouldReturnACompleteOKResponse() {
      Response getResponse = worksController.getBooks(queryBean);
      CollectionResource entity = (CollectionResource) getResponse.getEntity();

      assertThat(getResponse.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
      assertThat(entity.get("offset")).isNotNull();
      assertThat(entity.get("limit")).isNotNull();
      assertThat(entity.get("totalCount")).isNotNull();
   }

   @Test
   public void worksControllerPostBookShouldReturnACreatedResponseWithTheBookAndRetrieveTheRightAuthor() {
      authorDAO.createAuthor(NOVELIST_2);
      Response postResponse = worksController.postBook(BOOK_1_NOVELIST_2);
      assertThat(postResponse.getStatus()).isEqualTo(Response.Status.CREATED.getStatusCode());

      Map entity = ((BookResource) postResponse.getEntity()).getEntity(Map.class);
      assertThat(entity.get(Work.ID)).isEqualTo(BOOK_1_NOVELIST_2.getId());
      assertThat(entity.get(Work.TITLE)).isEqualTo(BOOK_1_NOVELIST_2.getTitle());
      assertThat(entity.get(Work.AUTHOR)).isEqualTo(new LinkedResource(uriInfo.getBaseUriBuilder().clone().path(AuthorsController.class).path(NOVELIST_2.getId())));
   }

   @Test
   public void worksControllerPostBookWithNonExistingShouldReturnACreatedResponseWithTheBookAndCreateTheAuthor() {
      Response postResponse = worksController.postBook(BOOK_1_NOVELIST_2);
      assertThat(postResponse.getStatus()).isEqualTo(Response.Status.CREATED.getStatusCode());

      Map entity = ((BookResource) postResponse.getEntity()).getEntity(Map.class);
      assertThat(entity.get(Work.ID)).isEqualTo(BOOK_1_NOVELIST_2.getId());
      assertThat(entity.get(Work.TITLE)).isEqualTo(BOOK_1_NOVELIST_2.getTitle());
      assertThat(entity.get(Work.AUTHOR)).isEqualTo(new LinkedResource(uriInfo.getBaseUriBuilder().clone().path(AuthorsController.class).path(NOVELIST_2.getId())));

      assertThat(authorDAO.readAuthor(NOVELIST_2.getId())).isEqualTo(NOVELIST_2);
   }

   @Test(expected = ConflictException.class)
   public void worksControllerPostExistingBookShouldReturnAException() {
      worksController.postBook(BOOK_1_NOVELIST_2);
      worksController.postBook(BOOK_1_NOVELIST_2);
   }

   @Test
   public void worksControllerGetMethodShouldReturnAListOfBooks() {
      workDAO.createBook(BOOK_1_NOVELIST_2);

      List<BookResource> expected = new ArrayList<>();
      expected.add(new BookResource(uriInfo, BOOK_1_NOVELIST_2, null));

      Response getResponse = worksController.getBooks(queryBean);
      CollectionResource entity = (CollectionResource) getResponse.getEntity();
      List actual = (List) entity.get("book");

      assertThat(entity.get("totalCount")).isEqualTo(1);
      assertThat(actual).hasSize(1);
      assertThat(actual).isEqualTo(expected);
   }

   @Test
   public void worksControllerGetMethodWith5AndLimitOf1BookShouldReturnAPaginatedResourceWithOnly1Book() {
      populateBooks();

      createQueryBean(0, 1);

      Response getResponse = worksController.getBooks(queryBean);
      CollectionResource entity = (CollectionResource) getResponse.getEntity();
      List actual = (List) entity.get("book");

      assertThat(entity.get("totalCount")).isEqualTo(5);
      assertThat(actual).hasSize(1);
   }

   @Test
   public void worksControllerGetMethodWith5AndOffsetOf1ShouldReturnAPaginatedResourceWithOnly4BookStartingFromTheSecond() {
      populateBooks();
      createQueryBean(1, 10);

      Response getResponse = worksController.getBooks(queryBean);
      CollectionResource entity = (CollectionResource) getResponse.getEntity();
      List actual = (List) entity.get("book");
      BookResource bookResource = (BookResource) actual.get(0);

      assertThat(entity.get("totalCount")).isEqualTo(5);
      assertThat(actual).hasSize(4);
      assertThat(bookResource.getEntity(Map.class).get("id")).isEqualTo(BOOK_5_NOVELIST_1.getId());
   }

   @Test
   public void worksControllerGetMethodWithTotalCountLessThanLimitShouldReturnNoPaginationWithoutLinks() {
      populateBooks();

      Response getResponse = worksController.getBooks(queryBean);
      CollectionResource entity = (CollectionResource) getResponse.getEntity();

      assertThat(entity.get("next")).isNull();
      assertThat(entity.get("prev")).isNull();
   }

   @Test
   public void worksControllerGetMethodWith5BookWithLimit1ShouldReturnAPaginatedResourceALinkToTheNextPageButNoLinkToPrevious() {
      populateBooks();
      createQueryBean(0, 1);

      Response getResponse = worksController.getBooks(queryBean);
      CollectionResource entity = (CollectionResource) getResponse.getEntity();

      assertCollectionResourceLinks(entity, "next", "1", "1");
      assertThat(entity.get("prev")).isNull();
   }

   @Test
   public void worksControllerGetMethodWith5BookStartingFrom4ShouldReturnAPaginatedResourceALinkToThePreviousPageButNoLinkToNext() {
      populateBooks();
      createQueryBean(4, 1);

      Response getResponse = worksController.getBooks(queryBean);
      CollectionResource entity = (CollectionResource) getResponse.getEntity();

      assertCollectionResourceLinks(entity, "prev", "3", "1");
      assertThat(entity.get("next")).isNull();
   }

   @Test
   public void worksControllerGetMethodWith5BookStartingFrom1ShouldReturnAPaginatedResourceALinkToThePreviousPageAndLinkToNext() {
      populateBooks();
      createQueryBean(1, 1);

      Response getResponse = worksController.getBooks(queryBean);
      CollectionResource entity = (CollectionResource) getResponse.getEntity();

      assertCollectionResourceLinks(entity, "prev", "0", "1");
      assertCollectionResourceLinks(entity, "next", "2", "1");
   }

   @Test
   public void worksControllerGetMethodWithSortingAscendingTitleShouldReturnASortedResource() {
      List<BookResource> expected = sortedBookListByTitle();

      populateBooks();

      queryBean.setSortBy("title-asc");

      Response getResponse = worksController.getBooks(queryBean);
      CollectionResource entity = (CollectionResource) getResponse.getEntity();
      List actual = (List) entity.get("book");

      assertThat(actual).isEqualTo(expected);
   }

   @Test
   public void worksControllerGetMethodWithSortingDescendingTitleShouldReturnASortedResource() {
      List<BookResource> expected = Lists.reverse(sortedBookListByTitle());

      populateBooks();

      queryBean.setSortBy("title-desc");

      Response getResponse = worksController.getBooks(queryBean);
      CollectionResource entity = (CollectionResource) getResponse.getEntity();
      List actual = (List) entity.get("book");

      assertThat(actual).isEqualTo(expected);
   }

   @Test
   public void worksControllerGetMethodWithSortingAscendingAuthorShouldReturnASortedResource() {
      List<BookResource> expected = sortedBookListByAuthor();

      populateBooks();

      queryBean.setSortBy("author-asc");

      Response getResponse = worksController.getBooks(queryBean);
      CollectionResource entity = (CollectionResource) getResponse.getEntity();
      List actual = (List) entity.get("book");

      assertThat(actual).isEqualTo(expected);
   }

   @Test
   public void worksControllerGetMethodWithSortingDescendingAuthorShouldReturnASortedResource() {
      List<BookResource> expected = Lists.reverse(sortedBookListByAuthor());

      populateBooks();

      queryBean.setSortBy("author-desc");

      Response getResponse = worksController.getBooks(queryBean);
      CollectionResource entity = (CollectionResource) getResponse.getEntity();
      List actual = (List) entity.get("book");

      assertThat(actual).isEqualTo(expected);
   }

   @Test
   public void worksControllerGetMethodWithFilteringTitleContainingTheNumber1ShouldReturnAFilteredResource() {
      List<BookResource> expected = new ArrayList<>();
      expected.add(new BookResource(uriInfo, BOOK_1_NOVELIST_2, null));

      populateBooks();

      queryBean.setTitle("1");

      Response getResponse = worksController.getBooks(queryBean);
      CollectionResource entity = (CollectionResource) getResponse.getEntity();
      List actual = (List) entity.get("book");

      assertThat(actual).isEqualTo(expected);
   }

   @Test
   public void worksControllerGetMethodWithFilteringAuthorNameContainingTheNumber1ShouldReturnAFilteredResource() {
      List<BookResource> expected = new ArrayList<>();
      expected.add(new BookResource(uriInfo, BOOK_5_NOVELIST_1, null));

      populateBooks();

      queryBean.setAuthorName("1");

      Response getResponse = worksController.getBooks(queryBean);
      CollectionResource entity = (CollectionResource) getResponse.getEntity();
      List actual = (List) entity.get("book");

      assertThat(actual).isEqualTo(expected);
   }

   @Test
   public void worksControllerGetMethodWithFilteringAuthorIdOfNovelist3ShouldReturnAFilteredResource() {
      List<BookResource> expected = new ArrayList<>();
      expected.add(new BookResource(uriInfo, BOOK_2_NOVELIST_3, null));

      populateBooks();

      queryBean.setAuthorId(NOVELIST_3.getId());

      Response getResponse = worksController.getBooks(queryBean);
      CollectionResource entity = (CollectionResource) getResponse.getEntity();
      List actual = (List) entity.get("book");

      assertThat(actual).isEqualTo(expected);
   }
}