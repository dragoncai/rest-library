package pop.rtbi.labs.controller;

import com.murex.rtbi.LinkedResource;
import org.junit.After;
import org.junit.Test;
import pop.rtbi.labs.*;
import pop.rtbi.labs.model.query.DefaultQueryBean;
import pop.rtbi.labs.resource.AuthorResource;
import pop.rtbi.labs.resource.BookResource;

import javax.ws.rs.NotFoundException;
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
 * Date: 06/08/2015
 * Time: 16:27
 */
public class WorkControllerTest extends AbstractServiceTest {
   private UriBuilder uriBuilder;
   private final UriInfo uriInfo;

   private final WorkController workController;
   private final WorkDAO workDAO;
   private final AuthorDAO authorDAO;
   private DefaultQueryBean queryBean;

   private static final IAuthor NOVELIST_1 = new Novelist("1");
   private static final IAuthor NOVELIST_2 = new Novelist("2");

   private static final Book BOOK_1_NOVELIST_1 = new Book("1", NOVELIST_1);
   private static final Book BOOK_2_NOVELIST_2 = new Book("2", NOVELIST_2);

   public WorkControllerTest() {
      workDAO = WorkDAO.INSTANCE;
      queryBean = new DefaultQueryBean();
      uriBuilder = getServiceUriBuilder().path(WorksController.class).path(BOOK_1_NOVELIST_1.getId());
      uriInfo = mockUriInfo(uriBuilder);
      workController = new WorkController(uriInfo, BOOK_1_NOVELIST_1.getId());
      authorDAO = AuthorDAO.INSTANCE;
   }

   @Override
   @After
   public void tearDown() {
      super.tearDown();
      queryBean = new DefaultQueryBean();
   }

   @Test
   public void workControllerGetBookShouldReturnTheBookResourceWithOkResponse() {
      workDAO.createBook(BOOK_1_NOVELIST_1);

      Response getResponse = workController.getBook(new DefaultQueryBean());
      assertThat(getResponse.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());

      BookResource bookResource = (BookResource) getResponse.getEntity();
      assertThat(bookResource.get(LinkedResource.HREF)).isEqualTo(uriBuilder.clone().build());

      Map bookEntityMap = bookResource.getEntity(Map.class);
      assertThat(bookEntityMap.get(Work.ID)).isEqualTo(BOOK_1_NOVELIST_1.getId());
      assertThat(bookEntityMap.get(Work.TITLE)).isEqualTo(BOOK_1_NOVELIST_1.getTitle());
      assertThat(bookEntityMap.get(Work.AUTHOR)).isEqualTo(new LinkedResource(uriInfo.getBaseUriBuilder().clone().path(AuthorsController.class).path(NOVELIST_1.getId())));
   }

   @Test(expected = NotFoundException.class)
   public void workControllerGetNonExistingBookShouldThrowAnNotFoundException() {
      workController.getBook(new DefaultQueryBean());
   }

   @Test
   public void workControllerPutMethodShouldReturnOkResponseWithTheNewBook() {
      authorDAO.createAuthor(NOVELIST_2);
      workDAO.createBook(BOOK_1_NOVELIST_1);
      Response putResponse = workController.putBook(BOOK_2_NOVELIST_2);
      assertThat(putResponse.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());

      BookResource bookResource = (BookResource) putResponse.getEntity();
      assertThat(bookResource.get(LinkedResource.HREF)).isEqualTo(uriBuilder.clone().build());

      Map bookEntityMap = bookResource.getEntity(Map.class);
      assertThat(bookEntityMap.get(Work.ID)).isEqualTo(BOOK_1_NOVELIST_1.getId());
      assertThat(bookEntityMap.get(Work.TITLE)).isEqualTo(BOOK_2_NOVELIST_2.getTitle());
      assertThat(bookEntityMap.get(Work.AUTHOR)).isEqualTo(new LinkedResource(uriInfo.getBaseUriBuilder().clone().path(AuthorsController.class).path(NOVELIST_2.getId())));
   }

   @Test(expected = NotFoundException.class)
   public void workControllerPutMethodWithNonExistingAuthorShouldReturnNotFoundException() {
      workDAO.createBook(BOOK_1_NOVELIST_1);
      workController.putBook(BOOK_2_NOVELIST_2);
   }

   @Test(expected = NotFoundException.class)
   public void workControllerPutNonExistingBookShouldThrowAnNotFoundException() {
      workController.putBook(BOOK_2_NOVELIST_2);
   }

   @Test
   public void workControllerDeleteAnBookShouldReturnAOkResponseAndTheObjectShouldBeDeleted() {
      workDAO.createBook(BOOK_1_NOVELIST_1);
      Response deleteResponse = workController.deleteBook();
      assertThat(deleteResponse.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
      assertThat(workDAO.readBook(BOOK_1_NOVELIST_1.getId())).isNull();
   }

   @Test(expected = NotFoundException.class)
   public void workControllerDeleteNonExistingBookShouldThrowAnNotFoundException() {
      workController.deleteBook();
   }

   @Test
   public void workControllerGetWithExpandAuthorShouldDisplayTheAuthorInformationInTheResponse() {
      workDAO.createBook(BOOK_1_NOVELIST_1);

      List<String> expand = new ArrayList<>();
      expand.add("author");
      queryBean.setExpand(expand);

      Response getResponse = workController.getBook(queryBean);
      assertThat(getResponse.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());

      BookResource bookResource = (BookResource) getResponse.getEntity();
      assertThat(bookResource.get(LinkedResource.HREF)).isEqualTo(uriBuilder.clone().build());

      Map bookEntityMap = bookResource.getEntity(Map.class);
      assertThat(bookEntityMap.get(Work.ID)).isEqualTo(BOOK_1_NOVELIST_1.getId());
      assertThat(bookEntityMap.get(Work.TITLE)).isEqualTo(BOOK_1_NOVELIST_1.getTitle());
      assertThat(bookEntityMap.get(Work.AUTHOR)).isEqualTo(new AuthorResource(uriInfo, NOVELIST_1));
   }
}
