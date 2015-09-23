package pop.rtbi.labs.resource;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.junit.Test;
import pop.rtbi.labs.*;
import pop.rtbi.labs.representation.AuthorsRepresentation;
import pop.rtbi.labs.representation.BooksRepresentation;

import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.jayway.restassured.RestAssured.expect;
import static com.jayway.restassured.RestAssured.given;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA.
 * User: ycai
 * Date: 22/05/15
 * Time: 11:59
 */
public class OldAuthorsResourceTest extends OldAbstractResourcesTest {

   public static final String AUTHOR = "Author";

   @Test
   public void testAccessToAuthorsShouldReturnOk200() {
      expect()
           .statusCode(HttpStatus.SC_OK)
           .when()
           .get(OldLibraryPaths.AUTHORS_PATH);
   }

   @Test
   public void testGetAuthorsForJsonShouldReturnJson() {
      given().accept(MediaType.APPLICATION_JSON)
           .expect()
           .contentType(MediaType.APPLICATION_JSON)
           .when()
           .get(OldLibraryPaths.AUTHORS_PATH);
   }

   @Test
   public void testGetAuthorsForXmlShouldReturnXml() {
      given().accept(MediaType.APPLICATION_XML)
           .expect()
           .contentType(MediaType.APPLICATION_XML)
           .when()
           .get(OldLibraryPaths.AUTHORS_PATH);
   }

   @Test
   public void testPostAAuthorShouldReturnCreated201() {
      IAuthor novelist = new Novelist(AUTHOR);
//
//      OldAuthorsResource authorsResource = new OldAuthorsResource();
//      javax.ws.rs.core.Response response = authorsResource.postAuthor(novelist);
//      assertEquals(201, response.getStatus());

      given()
           .request()
           .contentType(MediaType.APPLICATION_JSON)
           .body(novelist)
           .expect()
           .statusCode(HttpStatus.SC_CREATED)
           .when()
           .post(OldLibraryPaths.AUTHORS_PATH);
   }

   @Test
   public void testPostAAuthorShouldReturnSameAuthor() {
      Novelist expected = new Novelist(AUTHOR);
      Novelist actual =
           given()
                .request()
                .contentType(MediaType.APPLICATION_JSON)
                .body(expected)
                .when()
                .post(OldLibraryPaths.AUTHORS_PATH)
                .as(Novelist.class);

      assertEquals(expected, actual);
   }

   @Test
   public void testPostAAuthor2ShouldReturnGoodLocationUri() {
      Response response =
           given()
                .request()
                .contentType(MediaType.APPLICATION_JSON)
                .body(new Novelist(AUTHOR))
                .when()
                .post(OldLibraryPaths.AUTHORS_PATH);

      String expected = RestAssured.baseURI + ":" + RestAssured.port + RestAssured.basePath + OldLibraryPaths.AUTHORS_PATH + "/" + response.as(Novelist.class).getId();

      assertEquals(expected, response.andReturn().getHeaders().getValue(HttpHeaders.LOCATION));
   }

   @Test
   public void testDoublePostShouldReturnConflict409() {
      postAuthor(AUTHOR);

      given()
           .request()
           .contentType(MediaType.APPLICATION_JSON)
           .body(new Novelist(AUTHOR))
           .expect()
           .statusCode(HttpStatus.SC_CONFLICT)
           .when()
           .post(OldLibraryPaths.AUTHORS_PATH);

   }

   @Test
   public void testDoublePostShouldReturnTheOldId() {
      String sameName = AUTHOR;

      String expected = postAuthor(sameName).as(Novelist.class).getId();
      String actual = postAuthor(sameName).as(Novelist.class).getId();

      assertEquals(expected, actual);
   }

   @Test
   public void testAuthorQueryShouldReturn200() {
      given()
           .queryParam(Novelist.NAME, AUTHOR)
           .expect()
           .statusCode(HttpStatus.SC_OK)
           .when()
           .get(OldLibraryPaths.AUTHORS_PATH);
   }

   @Test
   public void testAuthorQueryShouldReturnTheRightAuthor() {
      Novelist posted = postAuthor(AUTHOR).as(Novelist.class);
      AuthorDAO expected = AuthorDAO.INSTANCE;
      expected.createAuthor(posted);

      AuthorsRepresentation actual = given()
           .accept(MediaType.APPLICATION_JSON)
           .queryParam(Novelist.NAME, "Auth")
           .when()
           .get(OldLibraryPaths.AUTHORS_PATH)
           .as(AuthorsRepresentation.class);

      assertEquals(expected.readAuthors().values().stream().collect(toList()), Arrays.asList(actual.getAuthors()));
   }

   @Test
   public void testPostABookWithNewAuthorShouldPostTheAuthor() {
      IAuthor expected = postBook(new Novelist(AUTHOR), "Title").as(Book.class).getAuthor();

      Novelist actual =
           given()
                .accept(MediaType.APPLICATION_JSON)
                .when()
                .get(OldLibraryPaths.getAuthorPath(expected.getId()))
                .as(Novelist.class);

      assertEquals(expected, actual);
   }

   @Test
   public void testDeleteAnAuthorShouldDeleteAllHisBooksAndNotOthersCheckExactName() {
      Novelist novelist = (Novelist) postBook(new Novelist(AUTHOR), "Title1").as(Book.class).getAuthor();
      postBook(new Novelist("Author2"), "Title2").as(Book.class).getAuthor();
      postBook(novelist, "Title3");

      RestAssured.delete(OldLibraryPaths.getAuthorPath(novelist.getId()));
      List<Work> books = Arrays.asList(given().accept(MediaType.APPLICATION_JSON).get(OldLibraryPaths.BOOKS_PATH).as(BooksRepresentation.class).getBooks());
      assertEquals(1, books.size());
   }

   @Test
   public void testQuerySortNameAscShouldReturnAnAscendingSortedList() {
      List<Novelist> expected = new ArrayList<>();
      expected.add(postAuthor("A").as(Novelist.class));
      expected.add(postAuthor("B").as(Novelist.class));
      expected.add(postAuthor("C").as(Novelist.class));

      AuthorsRepresentation actual = given()
           .accept(MediaType.APPLICATION_JSON)
           .queryParam(OldLibraryPaths.SORT_BY_QUERY, Novelist.NAME + OldLibraryPaths.SORT_BY_DELIMITER + OldLibraryPaths.ASC_SORT)
           .when()
           .get(OldLibraryPaths.AUTHORS_PATH)
           .as(AuthorsRepresentation.class);

      assertEquals(expected, Arrays.asList(actual.getAuthors()));
   }

   @Test
   public void testSortingAuthorsShouldBeByNameByDefault() {
      List<Novelist> expected = new ArrayList<>();
      expected.add(postAuthor("A").as(Novelist.class));
      expected.add(postAuthor("B").as(Novelist.class));
      expected.add(postAuthor("C").as(Novelist.class));

      AuthorsRepresentation actual = given()
           .accept(MediaType.APPLICATION_JSON)
           .when()
           .get(OldLibraryPaths.AUTHORS_PATH).as(AuthorsRepresentation.class);

      assertEquals(expected, Arrays.asList(actual.getAuthors()));

   }

   @Test
   public void testQuerySortNameDescShouldReturnADescendingSortedList() {
      List<Novelist> expected = new ArrayList<>();
      expected.add(postAuthor("C").as(Novelist.class));
      expected.add(postAuthor("B").as(Novelist.class));
      expected.add(postAuthor("A").as(Novelist.class));

      AuthorsRepresentation actual = given()
           .accept(MediaType.APPLICATION_JSON)
           .queryParam(OldLibraryPaths.SORT_BY_QUERY, Novelist.NAME + OldLibraryPaths.SORT_BY_DELIMITER + OldLibraryPaths.DESC_SORT)
           .when()
           .get(OldLibraryPaths.AUTHORS_PATH)
           .as(AuthorsRepresentation.class);

      assertEquals(expected, Arrays.asList(actual.getAuthors()));
   }

   @Test
   public void testQuerySortMisspelledFieldDescShouldReturnTheDefaultSort() {
      List<Novelist> expected = new ArrayList<>();
      expected.add(postAuthor("A").as(Novelist.class));
      expected.add(postAuthor("B").as(Novelist.class));
      expected.add(postAuthor("C").as(Novelist.class));

      AuthorsRepresentation actual = given()
           .accept(MediaType.APPLICATION_JSON)
           .queryParam(OldLibraryPaths.SORT_BY_QUERY, "wrong_Field" + OldLibraryPaths.SORT_BY_DELIMITER + OldLibraryPaths.DESC_SORT)
           .when()
           .get(OldLibraryPaths.AUTHORS_PATH)
           .as(AuthorsRepresentation.class);


      assertEquals(expected, Arrays.asList(actual.getAuthors()));
   }

   @Test
   public void testQuerySortNameMisspelledOrderShouldReturnTheDefaultSort() {
      List<Novelist> expected = new ArrayList<>();
      expected.add(postAuthor("A").as(Novelist.class));
      expected.add(postAuthor("B").as(Novelist.class));
      expected.add(postAuthor("C").as(Novelist.class));

      AuthorsRepresentation actual = given()
           .accept(MediaType.APPLICATION_JSON)
           .queryParam(OldLibraryPaths.SORT_BY_QUERY, Novelist.NAME + OldLibraryPaths.SORT_BY_DELIMITER + "something")
           .when()
           .get(OldLibraryPaths.AUTHORS_PATH)
           .as(AuthorsRepresentation.class);

      assertEquals(expected, Arrays.asList(actual.getAuthors()));
   }

   @Test
   public void testQueryMisspelledSortParamShouldReturnTheDefaultSort() {
      List<Novelist> expected = new ArrayList<>();
      expected.add(postAuthor("A").as(Novelist.class));
      expected.add(postAuthor("B").as(Novelist.class));
      expected.add(postAuthor("C").as(Novelist.class));

      AuthorsRepresentation actual = given()
           .accept(MediaType.APPLICATION_JSON)
           .queryParam(OldLibraryPaths.SORT_BY_QUERY, "something")
           .when()
           .get(OldLibraryPaths.AUTHORS_PATH)
           .as(AuthorsRepresentation.class);

      assertEquals(expected, Arrays.asList(actual.getAuthors()));
   }


}
