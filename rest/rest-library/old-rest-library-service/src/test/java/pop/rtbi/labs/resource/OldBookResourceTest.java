package pop.rtbi.labs.resource;

import org.apache.http.HttpStatus;
import org.junit.Test;
import pop.rtbi.labs.Novelist;
import pop.rtbi.labs.Book;
import pop.rtbi.labs.OldLibraryPaths;

import javax.ws.rs.core.MediaType;

import static com.jayway.restassured.RestAssured.expect;
import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA.
 * User: ycai
 * Date: 10/06/2015
 * Time: 10:20
 */
public class OldBookResourceTest extends OldAbstractResourcesTest {

   public static final String TITLE = "Title";
   public static final String AUTHOR1 = "Author1";

   @Test
   public void testGetABookShouldReturn200Ok() {
      String id = postBook(new Novelist(AUTHOR1), TITLE).as(Book.class).getId();

      expect()
           .statusCode(HttpStatus.SC_OK)
           .when()
           .get(OldLibraryPaths.getBookPath(id));
   }

   @Test
   public void testGetABookForJsonShouldReturnJSON() {
      String id = postBook(new Novelist(AUTHOR1), TITLE).as(Book.class).getId();

      given()
           .accept(MediaType.APPLICATION_JSON)
           .expect()
           .contentType(MediaType.APPLICATION_JSON)
           .when()
           .get(OldLibraryPaths.getBookPath(id));
   }

   @Test
   public void testGetABookForXmlShouldReturnXml() {
      String id = postBook(new Novelist(AUTHOR1), TITLE).as(Book.class).getId();

      given()
           .accept(MediaType.APPLICATION_XML)
           .expect()
           .contentType(MediaType.APPLICATION_XML)
           .when()
           .get(OldLibraryPaths.getBookPath(id));
   }

   @Test
   public void testGetABookShouldReturnSameId() {
      String id = postBook(new Novelist(AUTHOR1), TITLE).as(Book.class).getId();

      given()
           .accept(MediaType.APPLICATION_JSON)
           .expect()
           .body(Book.ID, equalTo(id))
           .when()
           .get(OldLibraryPaths.getBookPath(id));
   }

   @Test
   public void testGetABookShouldReturnSameBook() {
      Book expected = new Book(TITLE, new Novelist(AUTHOR1));
      String id = postBook((Novelist)expected.getAuthor(), expected.getTitle()).as(Book.class).getId();

      Book actual =
           given()
                .accept(MediaType.APPLICATION_JSON)
                .when()
                .get(OldLibraryPaths.getBookPath(id))
                .as(Book.class);

      assertEquals(actual, actual);
   }

   @Test
   public void testGetANonExistingBookShouldReturn204NoContent() {
      expect()
           .statusCode(HttpStatus.SC_NO_CONTENT)
           .when()
           .get(OldLibraryPaths.getBookPath("some random id"));
   }

   @Test
   public void testPutABookShouldReturn202Accepted() {
      Book expected = new Book("Title1", new Novelist(AUTHOR1));

      String id = postBook((Novelist)expected.getAuthor(), expected.getTitle()).as(Book.class).getId();

      given()
           .contentType(MediaType.APPLICATION_JSON).body(expected)
           .expect()
           .statusCode(HttpStatus.SC_ACCEPTED)
           .when()
           .put(OldLibraryPaths.getBookPath(id));
   }

   @Test
   public void testPutABookShouldReturnTheNewBook() {
      Book expected = new Book(TITLE, new Novelist(AUTHOR1));
      String id = postBook((Novelist)expected.getAuthor(), expected.getTitle()).as(Book.class).getId();

      Book actual =
           given()
                .contentType(MediaType.APPLICATION_JSON).body(expected)
                .when()
                .put(OldLibraryPaths.getBookPath(id))
                .as(Book.class);

      assertEquals(expected.hashCode(), actual.hashCode());
   }

   @Test
   public void testPutABookOverANonExistingBookShouldReturn204() {
      given()
           .contentType(MediaType.APPLICATION_JSON)
           .body(new Book(TITLE, new Novelist(AUTHOR1)))
           .expect()
           .statusCode(HttpStatus.SC_NO_CONTENT)
           .when()
           .put(OldLibraryPaths.getBookPath("some_random_id"));
   }

   @Test
   public void testDeleteABookShouldReturn200() {
      String id = postBook(new Novelist(AUTHOR1), TITLE).as(Book.class).getId();

      expect().statusCode(HttpStatus.SC_OK)
           .when().delete(OldLibraryPaths.getBookPath(id));
   }

   @Test
   public void testDeleteANonExistingBookShouldReturn204NoContent() {
      expect().statusCode(HttpStatus.SC_NO_CONTENT)
           .when().delete(OldLibraryPaths.getBookPath("some random id"));
   }

}
