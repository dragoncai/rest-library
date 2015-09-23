package pop.rtbi.labs.controller;

import com.murex.rtbi.CollectionResource;
import com.murex.rtbi.LinkedResource;
import org.junit.Test;
import pop.rtbi.labs.*;
import pop.rtbi.labs.resource.AuthorResource;
import pop.rtbi.labs.resource.BookResource;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.fest.assertions.Assertions.assertThat;

/**
 * Created with IntelliJ IDEA.
 * User: ycai
 * Date: 06/08/2015
 * Time: 16:27
 */
public class AuthorControllerTest extends AbstractServiceTest {
   private final UriBuilder uriBuilder;

   private final AuthorController authorController1;
   private final AuthorDAO authorDAO;
   private final WorkDAO workDAO;

   private static final IAuthor NOVELIST_1 = new Novelist("1");
   private static final IAuthor NOVELIST_2 = new Novelist("2");

   private static final Work BOOK_1_NOVELIST_1 = new Book("1", NOVELIST_1);
   private static final Work BOOK_2_NOVELIST_2 = new Book("2", NOVELIST_2);

   public AuthorControllerTest() {
      authorDAO = AuthorDAO.INSTANCE;
      workDAO = WorkDAO.INSTANCE;
      uriBuilder = getServiceUriBuilder().path(AuthorsController.class).path(NOVELIST_1.getId());
      UriInfo uriInfo = mockUriInfo(uriBuilder);
      authorController1 = new AuthorController(uriInfo, NOVELIST_1.getId());
   }

   @Test
   public void authorControllerGetAuthorShouldReturnTheAuthorResourceWithOkResponse() {
      authorDAO.createAuthor(NOVELIST_1);

      Response getResponse = authorController1.getAuthor();
      assertThat(getResponse.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());

      AuthorResource authorResource = (AuthorResource) getResponse.getEntity();
      assertThat(authorResource.get(LinkedResource.HREF)).isEqualTo(uriBuilder.clone().build());

      Map authorEntity = (Map) authorResource.get(AuthorResource.ENTITY);
      assertThat(authorEntity.get(IAuthor.ID)).isEqualTo(NOVELIST_1.getId());
      assertThat(authorEntity.get(IAuthor.NAME)).isEqualTo(NOVELIST_1.getName());
      assertThat(((LinkedResource) authorEntity.get("books")).get(LinkedResource.HREF)).isEqualTo(uriBuilder.clone().path("books").build());
   }

   @Test(expected = NotFoundException.class)
   public void authorControllerGetNonExistingAuthorShouldThrowAnNotFoundException() {
      authorController1.getAuthor();
   }

   @Test
   public void authorControllerPutMethodShouldReturnOkResponseWithTheNewAuthor() {
      authorDAO.createAuthor(NOVELIST_1);
      Response putResponse = authorController1.putAuthor((Novelist) NOVELIST_2);
      assertThat(putResponse.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());

      AuthorResource authorResource = (AuthorResource) putResponse.getEntity();
      assertThat(authorResource.get(LinkedResource.HREF)).isEqualTo(uriBuilder.clone().build());

      Map authorEntity = (Map) authorResource.get(AuthorResource.ENTITY);
      assertThat(authorEntity.get(IAuthor.ID)).isEqualTo(NOVELIST_1.getId());
      assertThat(authorEntity.get(IAuthor.NAME)).isEqualTo(NOVELIST_2.getName());
      assertThat(((LinkedResource) authorEntity.get("books")).get(LinkedResource.HREF)).isEqualTo(uriBuilder.clone().path("books").build());
   }

   @Test(expected = NotFoundException.class)
   public void authorControllerPutNonExistingAuthorShouldThrowAnNotFoundException() {
      authorController1.putAuthor((Novelist) NOVELIST_2);
   }

   @Test
   public void authorControllerDeleteAnAuthorShouldReturnAOkResponseAndTheObjectShouldBeDeleted() {
      authorDAO.createAuthor(NOVELIST_1);
      Response deleteResponse = authorController1.deleteAuthor();
      assertThat(deleteResponse.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
      assertThat(authorDAO.readAuthor(NOVELIST_1.getId())).isNull();
   }

   @Test(expected = NotFoundException.class)
   public void authorControllerDeleteNonExistingAuthorShouldThrowAnNotFoundException() {
      authorController1.deleteAuthor();
   }

   @Test
   public void authorControllerGetBooksShouldReturnTheListOfBookFromTheAuthorWithOkResponse() {
      workDAO.createBook(BOOK_1_NOVELIST_1);
      workDAO.createBook(BOOK_2_NOVELIST_2);
      authorDAO.createAuthor(NOVELIST_1);
      authorDAO.createAuthor(NOVELIST_2);

      Response booksResponse = authorController1.getBooks();
      assertThat(booksResponse.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());

      CollectionResource collectionResource = (CollectionResource) booksResponse.getEntity();
      List bookResourceList = (List) collectionResource.get("books");
      assertThat(collectionResource.get("totalCount")).isEqualTo(1);
      assertThat(bookResourceList.size()).isEqualTo(1);

      BookResource actual = (BookResource) bookResourceList.get(0);
      Map bookEntity = actual.getEntity(Map.class);
      LinkedResource linkedResource = (LinkedResource) bookEntity.get(Work.AUTHOR);
      assertThat(linkedResource.get(LinkedResource.HREF)).isEqualTo(uriBuilder.clone().build());
   }

   @Test
   public void authorControllerDeleteAnAuthorShouldReturnAOkResponseAndDeleteAllHisBooks() {
      authorDAO.createAuthor(NOVELIST_1);
      workDAO.createBook(BOOK_1_NOVELIST_1);
      Response deleteResponse = authorController1.deleteAuthor();
      assertThat(deleteResponse.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
      assertThat(authorDAO.readAuthor(NOVELIST_1.getId())).isNull();
      assertThat(workDAO.readBook(BOOK_1_NOVELIST_1.getId())).isNull();
   }
}
