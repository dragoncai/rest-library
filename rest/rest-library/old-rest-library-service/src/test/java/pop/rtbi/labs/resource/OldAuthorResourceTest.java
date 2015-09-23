package pop.rtbi.labs.resource;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.Test;
import pop.rtbi.labs.*;
import pop.rtbi.labs.representation.BooksRepresentation;

import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.jayway.restassured.RestAssured.*;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA.
 * User: ycai
 * Date: 10/06/2015
 * Time: 10:20
 */
public class OldAuthorResourceTest extends OldAbstractResourcesTest {

   public static final String AUTHOR_1 = "Author1";
   public static final String AUTHOR_2 = "Author2";
   public static final String AUTHOR_10 = "Author10";
   public static final String TITLE_1 = "Title1";
   public static final String TITLE_2 = "Title2";
   public static final String TITLE_3 = "Title3";
   public static final String TITLE_4 = "Title4";
   public static final String TITLE_5 = "Title5";

    @Test
   public void testGetAAuthorShouldReturnOk200() {
      String id = postAuthor(AUTHOR_1).as(Novelist.class).getId();

      given()
           .accept(MediaType.APPLICATION_JSON)
           .expect()
           .statusCode(HttpStatus.SC_OK)
           .when()
           .get(OldLibraryPaths.getAuthorPath(id));

   }

   @Test
   public void testGetAAuthorShouldReturnJSON() {
      String id = postAuthor(AUTHOR_1).as(Novelist.class).getId();
      given()
           .accept(MediaType.APPLICATION_JSON)
           .expect()
           .contentType(MediaType.APPLICATION_JSON)
           .when()
           .get(OldLibraryPaths.getAuthorPath(id));

   }

   @Test
   public void testGetAAuthorShouldReturnXML() {
      String id = postAuthor(AUTHOR_1).as(Novelist.class).getId();

      given()
           .accept(MediaType.APPLICATION_XML)
           .expect()
           .statusCode(HttpStatus.SC_OK)
           .contentType(MediaType.APPLICATION_XML)
           .when()
           .get(OldLibraryPaths.getAuthorPath(id));
   }

   @Test
   public void testGetAAuthorShouldReturnSameAuthor() {
      Novelist expected = postAuthor(AUTHOR_1).as(Novelist.class);

      Response response = given()
           .accept(MediaType.APPLICATION_JSON)
           .when()
           .get(OldLibraryPaths.getAuthorPath(expected.getId()));

      Novelist actual =
           response
                .as(Novelist.class);

      assertEquals(expected, actual);
   }

   @Test
   public void testGetAAuthorShouldReturnSameId() {
      String id = postAuthor(AUTHOR_1).as(Novelist.class).getId();

      given()
           .accept(MediaType.APPLICATION_JSON)
           .expect()
           .body(IAuthor.ID, equalTo(id))
           .when()
           .get(OldLibraryPaths.getAuthorPath(id));

   }

   @Test
   public void testGetANonExistingAuthorShouldReturn204NoContent() {
      expect()
           .statusCode(HttpStatus.SC_NO_CONTENT)
           .when()
           .get(OldLibraryPaths.getAuthorPath("some random id"));
   }


   @Test
   public void testPutAAuthorShouldReturnAccepted202() {
      String id = postAuthor(AUTHOR_1).as(Novelist.class).getId();

      given()
           .contentType(MediaType.APPLICATION_JSON)
           .body(new Novelist(AUTHOR_2))
           .expect()
           .statusCode(HttpStatus.SC_ACCEPTED)
           .when()
           .put(OldLibraryPaths.getAuthorPath(id));
   }

   @Test
   public void testPutAAuthorShouldReturnTheNewAuthor() {
      String id = postAuthor(AUTHOR_1).as(Novelist.class).getId();
      Novelist expected = new Novelist(AUTHOR_2);

      Novelist actual =
           given()
                .request()
                .contentType(MediaType.APPLICATION_JSON)
                .body(expected)
                .when()
                .put(OldLibraryPaths.getAuthorPath(id))
                .as(Novelist.class);

      assertEquals(expected.getName(), actual.getName());
      assertEquals(id, actual.getId());
   }

   @Test
   public void testPutAAuthorOverANonExistingAuthorShouldReturn204() {
      given()
           .contentType(MediaType.APPLICATION_JSON)
           .body(new Novelist(AUTHOR_1))
           .expect()
           .statusCode(HttpStatus.SC_NO_CONTENT)
           .when()
           .put(OldLibraryPaths.getAuthorPath("some_random_id"));
   }

   @Test
   public void testDeleteAnAuthorShouldReturn200() {
      String id = postAuthor(AUTHOR_1).as(Novelist.class).getId();

      expect()
           .statusCode(HttpStatus.SC_OK)
           .when()
           .delete(OldLibraryPaths.getAuthorPath(id));
   }

   @Test
   public void testDeleteANonExistingAuthorShouldReturn204NoContent() {
      expect()
           .statusCode(HttpStatus.SC_NO_CONTENT)
           .when()
           .delete(OldLibraryPaths.getAuthorPath("some random id"));
   }

   @Test
   public void testGetAuthorSBooksShouldReturn200() {
      String id = postAuthor(AUTHOR_1).as(Novelist.class).getId();

      expect()
           .statusCode(HttpStatus.SC_OK)
           .when()
           .get(OldLibraryPaths.getAuthorBooksPath(id));

   }

   @Test
   public void testGetAuthorSBooksTheRightBooks() {
      Novelist[] novelists = {
           postAuthor(AUTHOR_1).as(Novelist.class),
           postAuthor(AUTHOR_2).as(Novelist.class)
      };

      List<Book> expected = new ArrayList<>();
      expected.add(postBook(novelists[0], TITLE_1).as(Book.class));
      expected.add(postBook(novelists[0], TITLE_2).as(Book.class));
      postBook(novelists[1], TITLE_3);
      expected.add(postBook(novelists[0], TITLE_4).as(Book.class));
      postBook(novelists[1], TITLE_4);

      List<Work> actual = Arrays.asList(
           given().accept(MediaType.APPLICATION_JSON)
                .when().get(OldLibraryPaths.getAuthorBooksPath(novelists[0].getId()))
                .as(BooksRepresentation.class).getBooks());

      assertEquals(expected, actual);
   }

   @Test
   public void testGetAuthorSBooksShouldOnlyReturnItsBooksWithHisExactName() {
      Novelist[] novelists = {
           postAuthor(AUTHOR_1).as(Novelist.class),
           postAuthor(AUTHOR_10).as(Novelist.class)
      };

      List<Book> expected = new ArrayList<>();
      expected.add(postBook(novelists[0], TITLE_1).as(Book.class));
      expected.add(postBook(novelists[0], TITLE_2).as(Book.class));
      postBook(novelists[1], TITLE_3);
      expected.add(postBook(novelists[0], TITLE_4).as(Book.class));
      postBook(novelists[1], TITLE_5);

      List<Work> actual = Arrays.asList(
           given().accept(MediaType.APPLICATION_JSON)
                .when().get(OldLibraryPaths.getAuthorBooksPath(novelists[0].getId()))
                .as(BooksRepresentation.class).getBooks());

      assertEquals(expected, actual);
   }

   @Test
   public void testAnAuthorSListOfBooksWithTitleQueryShouldReturn200Ok() {
      Novelist novelist = postAuthor(AUTHOR_1).as(Novelist.class);

      given()
           .accept(MediaType.APPLICATION_XML)
           .queryParam(Book.TITLE, "")
           .expect()
           .statusCode(HttpStatus.SC_OK)
           .when()
           .get(OldLibraryPaths.getAuthorBooksPath(novelist.getId()));
   }

   @Test
   public void testAnAuthorSListOfBooksWithEmptyTitleQueryShouldReturnAllBooks() {
      Novelist novelist = postAuthor(AUTHOR_1).as(Novelist.class);

      List<Book> expected = new ArrayList<>();
      expected.add(postBook(novelist, TITLE_1).as(Book.class));
      expected.add(postBook(novelist, TITLE_2).as(Book.class));
      expected.add(postBook(novelist, TITLE_3).as(Book.class));

      List<Work> actual = Arrays.asList(
           given()
                .accept(MediaType.APPLICATION_JSON)
                .queryParam(Book.TITLE, "")
                .when()
                .get(OldLibraryPaths.getAuthorBooksPath(novelist.getId()))
                .as(BooksRepresentation.class).getBooks());

      assertEquals(expected, actual);
   }

   @Test
   public void testAnAuthorSListOfBooksWithTitleQueryShouldReturnTheRightBooks() {
      Novelist novelist = postAuthor(AUTHOR_1).as(Novelist.class);

      List<Book> expected = new ArrayList<>();
      postBook(novelist, TITLE_1);

      expected.add(postBook(novelist, TITLE_2).as(Book.class));
      postBook(novelist, TITLE_3);

      List<Work> actual = Arrays.asList(
           given()
                .accept(MediaType.APPLICATION_JSON)
                .queryParam(Book.TITLE, "title2")
                .when()
                .get(OldLibraryPaths.getAuthorBooksPath(novelist.getId()))
                .as(BooksRepresentation.class).getBooks());

      assertEquals(expected, actual);
   }

   @Test
   public void testDeleteAnAuthorShouldDeleteAllHisBooks() {
      IAuthor author = postBook(new Novelist(AUTHOR_1), TITLE_1).as(Book.class).getAuthor();

      RestAssured.delete(OldLibraryPaths.getAuthorPath(author.getId()));

      List<Work> actual = Arrays.asList(
           get(OldLibraryPaths.BOOKS_PATH)
                .as(BooksRepresentation.class).getBooks());

      assertEquals(0, actual.size());
   }

   @Test
   public void testDeleteAnAuthorShouldDeleteAllHisBooksAndNotOthers() {
      Novelist novelist = (Novelist)postBook(new Novelist(AUTHOR_1), TITLE_1).as(Book.class).getAuthor();
      postBook(new Novelist(AUTHOR_2), TITLE_2).as(Book.class).getAuthor();
      postBook(novelist, TITLE_3);

      RestAssured.delete(OldLibraryPaths.getAuthorPath(novelist.getId()));
      List<Work> actual = Arrays.asList(
           given().accept(MediaType.APPLICATION_JSON)
                .when().get(OldLibraryPaths.BOOKS_PATH)
                .as(BooksRepresentation.class).getBooks());

      assertEquals(1, actual.size());
   }
}
