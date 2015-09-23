package pop.rtbi.labs.resource;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Header;
import org.junit.Test;
import pop.rtbi.labs.*;
import pop.rtbi.labs.representation.BooksRepresentation;
import pop.rtbi.labs.representation.CustomLink;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.jayway.restassured.RestAssured.*;
import static org.fest.assertions.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertEquals;
import static pop.rtbi.labs.OldLibraryPaths.*;

/**
 * Created with IntelliJ IDEA.
 * User: ycai
 * Date: 20/05/15
 * Time: 15:13
 */
public class OldBooksResourceTest extends OldAbstractResourcesTest {

   private int code(Status ok) {
      return ok.getStatusCode();
   }

   @Test
   public void testAccessToBooksShouldReturn200Ok() {
      expect()
           .statusCode(code(Status.OK))
           .when()
           .get(BOOKS_PATH);
   }

   @Test
   public void testGetBooksForJsonShouldReturnJson() {
      given().accept(MediaType.APPLICATION_JSON)
           .expect()
           .contentType(MediaType.APPLICATION_JSON)
           .when()
           .get(BOOKS_PATH);
   }

   @Test
   public void testGetBooksForXmlShouldReturnXml() {
      given().accept(MediaType.APPLICATION_XML)
           .expect()
           .contentType(MediaType.APPLICATION_XML)
           .when()
           .get(BOOKS_PATH);
   }

   @Test
   public void testPostABookShouldReturn201Created() {
      given()
           .request()
           .contentType(MediaType.APPLICATION_JSON)
           .body(new Book("A", new Novelist("A")))
           .expect()
           .statusCode(code(Status.CREATED))
           .when()
           .post(BOOKS_PATH);
   }

   @Test
   public void testPostABookShouldReturnTheBook() {
      Book expected = new Book("A", new Novelist("A"));

      Book actual = given()
           .request()
           .contentType(MediaType.APPLICATION_JSON)
           .body(expected)
           .when()
           .post(BOOKS_PATH)
           .as(Book.class);

      assertEquals(expected, actual);
   }

   @Test
   public void testDoublePostShouldReturn409Conflict() {
      postBook(new Book("A", new Novelist("A")));

      given()
           .request()
           .contentType(MediaType.APPLICATION_JSON)
           .body(new Book("A", new Novelist("A")))
           .expect()
           .statusCode(code(Status.CONFLICT))
           .when()
           .post(BOOKS_PATH);

   }

   @Test
   public void testEmptyTitleQueryShouldReturnTheEntireList() {
      List<Book> expected = new ArrayList<>();

      expected.add(new Book("Alpha", new Novelist("A")));
      expected.add(new Book("Beta", new Novelist("A")));
      expected.add(new Book("Gamma", new Novelist("A")));
      expected.add(new Book("Theta", new Novelist("A")));

      expected.forEach(this::postBook);

      List<Work> actual = Arrays.asList(
           given()
                .accept(MediaType.APPLICATION_JSON)
                .queryParam(Book.TITLE, "")
                .when()
                .get(BOOKS_PATH)
                .as(BooksRepresentation.class).getBooks());

      assertEquals(expected, actual);
   }

   @Test
   public void testTitleQueryShouldReturn200() {
      postBook(new Book("A", new Novelist("A")));

      given()
           .queryParam(Book.TITLE, "A")
           .accept(MediaType.APPLICATION_JSON)
           .expect()
           .statusCode(code(Status.OK))
           .when()
           .get(BOOKS_PATH);
   }

   @Test
   public void testTitleQueryShouldReturnTheRightBook() {
      List<Book> expected = new ArrayList<>();

      Book expectedBook = new Book("A", new Novelist("A"));
      expected.add(expectedBook);

      postBook(expectedBook);

      List<Work> actual = Arrays.asList(
           given()
                .accept(MediaType.APPLICATION_JSON)
                .queryParam(Book.TITLE, "A")
                .when()
                .get(BOOKS_PATH)
                .as(BooksRepresentation.class).getBooks());

      assertEquals(expected, actual);
   }

   @Test
   public void testTitleQueryWithNoExistingTitleShouldReturnAnEmptyList() {
      postBook(new Book("A", new Novelist("A")));

      List<Work> actual = Arrays.asList(
           given()
                .accept(MediaType.APPLICATION_JSON)
                .queryParam(Book.TITLE, "Other")
                .when()
                .get(BOOKS_PATH)
                .as(BooksRepresentation.class).getBooks());

      assertEquals(0, actual.size());
   }

   @Test
   public void testAuthorQueryShouldReturn200Ok() {
      postBook(new Book("A", new Novelist("A")));

      given()
           .queryParam(Book.AUTHOR, "a")
           .expect()
           .statusCode(code(Status.OK))
           .when()
           .get(BOOKS_PATH);

   }

   @Test
   public void testAuthorQueryShouldReturnTheRightAuthor() {
      List<Book> expected = new ArrayList<>();
      expected.add(new Book("A", new Novelist("A")));
      postBook(expected.get(0));

      List<Work> actual = Arrays.asList(
           given()

                .accept(MediaType.APPLICATION_JSON)
                .queryParam(Book.AUTHOR, "a")
                .when()
                .get(BOOKS_PATH)
                .as(BooksRepresentation.class).getBooks());


      assertEquals(expected, actual);
   }

   @Test
   public void testQuerySortTitleAscShouldReturnAnAscendingSortedList() {
      Novelist novelist = new Novelist("A");
      List<Book> expected = new ArrayList<>();

      expected.add(new Book("aaa", novelist));
      expected.add(new Book("bbb", novelist));
      expected.add(new Book("ccc", novelist));

      postBook(expected.get(1));
      postBook(expected.get(2));
      postBook(expected.get(0));

      List<Work> actual = Arrays.asList(
           given()
                .accept(MediaType.APPLICATION_JSON)
                .queryParam(SORT_BY_QUERY, Book.TITLE + SORT_BY_DELIMITER + ASC_SORT)
                .when()
                .get(BOOKS_PATH)
                .as(BooksRepresentation.class).getBooks());

      assertEquals(expected, actual);
   }

   @Test
   public void testQuerySortTitleDescShouldReturnAnDescendingSortedList() {
      Novelist novelist = new Novelist("A");
      List<Book> expected = new ArrayList<>();

      expected.add(new Book("ccc", novelist));
      expected.add(new Book("bbb", novelist));
      expected.add(new Book("aaa", novelist));

      postBook(expected.get(1));
      postBook(expected.get(2));
      postBook(expected.get(0));

      List<Work> actual = Arrays.asList(
           given()
                .accept(MediaType.APPLICATION_JSON)
                .queryParam(SORT_BY_QUERY, Book.TITLE + SORT_BY_DELIMITER + DESC_SORT)
                .when()
                .get(BOOKS_PATH)
                .as(BooksRepresentation.class).getBooks());

      assertEquals(expected, actual);
   }

   @Test
   public void testQuerySortMisspelledFieldDescShouldReturnTheOriginalList() {
      Novelist novelist = new Novelist("A");

      postBook(new Book("bbb", novelist));
      postBook(new Book("ccc", novelist));
      postBook(new Book("aaa", novelist));

      List<Work> expected = Arrays.asList(get(BOOKS_PATH).as(BooksRepresentation.class).getBooks());

      List<Work> actual = Arrays.asList(
           given()
                .accept(MediaType.APPLICATION_JSON)
                .queryParam(SORT_BY_QUERY, "wrong_field" + SORT_BY_DELIMITER + DESC_SORT)
                .when()
                .get(BOOKS_PATH)
                .as(BooksRepresentation.class).getBooks());

      assertEquals(expected, actual);
   }

   @Test
   public void testQuerySortTitleMisspelledOrderShouldReturnTheOriginalList() {
      Novelist novelist = new Novelist("A");
      postBook(new Book("bbb", novelist));
      postBook(new Book("ccc", novelist));
      postBook(new Book("aaa", novelist));

      List<Work> expected = Arrays.asList(get(BOOKS_PATH).as(BooksRepresentation.class).getBooks());

      List<Work> actual = Arrays.asList(
           given()
                .accept(MediaType.APPLICATION_JSON)
                .queryParam(SORT_BY_QUERY, Book.TITLE + SORT_BY_DELIMITER + "something")
                .when()
                .get(BOOKS_PATH)
                .as(BooksRepresentation.class).getBooks());

      assertEquals(expected, actual);
   }

   @Test
   public void testQueryMisspelledSortParamShouldReturnTheOriginalList() {
      Novelist novelist = new Novelist("A");

      postBook(new Book("bbb", novelist));
      postBook(new Book("ccc", novelist));
      postBook(new Book("aaa", novelist));

      List<Work> expected = Arrays.asList(get(BOOKS_PATH).as(BooksRepresentation.class).getBooks());

      List<Work> actual = Arrays.asList(
           given()
                .accept(MediaType.APPLICATION_JSON)
                .queryParam(SORT_BY_QUERY, "something")
                .when()
                .get(BOOKS_PATH)
                .as(BooksRepresentation.class).getBooks());

      assertEquals(expected, actual);
   }

   @Test
   public void testQuerySortAuthorAscShouldReturnAnAscendingSortedListByAuthorName() {
      String title = "A";
      Book expectedSecond = postBook(new Book(title, new Novelist("Bertrand"))).as(Book.class);
      Book expectedThird = postBook(new Book(title, new Novelist("Celine"))).as(Book.class);
      Book expectedFirst = postBook(new Book(title, new Novelist("Albert"))).as(Book.class);

      List<Work> actual = Arrays.asList(
           given()
                .accept(MediaType.APPLICATION_JSON)
                .queryParam(SORT_BY_QUERY, Book.AUTHOR + SORT_BY_DELIMITER + ASC_SORT)
                .when()
                .get(BOOKS_PATH)
                .as(BooksRepresentation.class).getBooks());

      assertEquals(expectedFirst, actual.get(0));
      assertEquals(expectedSecond, actual.get(1));
      assertEquals(expectedThird, actual.get(2));
   }

   @Test
   public void testQuerySortAuthorDescShouldReturnAnDescendingSortedListByAuthorName() {
      String title = "A";
      Book expectedSecond = postBook(new Book(title, new Novelist("Bertrand"))).as(Book.class);
      Book expectedFirst = postBook(new Book(title, new Novelist("Celine"))).as(Book.class);
      Book expectedThird = postBook(new Book(title, new Novelist("Albert"))).as(Book.class);

      List<Work> actual = Arrays.asList(
           given()
                .accept(MediaType.APPLICATION_JSON)
                .queryParam(SORT_BY_QUERY, Book.AUTHOR + SORT_BY_DELIMITER + DESC_SORT)
                .when()
                .get(BOOKS_PATH)
                .as(BooksRepresentation.class).getBooks());

      assertEquals(expectedFirst, actual.get(0));
      assertEquals(expectedSecond, actual.get(1));
      assertEquals(expectedThird, actual.get(2));
   }

   @Test
   public void testPaginationOffsetShouldBeTheStartingPoint() {
      postBook(new Book("A", new Novelist("A")));
      Book expected = postBook(new Book("B", new Novelist("B"))).as(Book.class);
      assertEquals(expected, getBooksResponseWithOffset(1).getBooks()[0]);
   }

   @Test
   public void testPaginationLimitShouldBeTheNumberOfItem() {
      postBook(new Book("A", new Novelist("A")));
      postBook(new Book("B", new Novelist("B")));

      assertEquals(1, getBooksResponseWithLimit(1).getBooks().length);
   }

   @Test
   public void testPaginationWithLessThan5ItemsShouldOnlyDisplayThem() {
      postBook(new Book("A", new Novelist("A")));
      assertEquals(1, getBooksResponseWithLimit(3).getBooks().length);
   }

   private BooksRepresentation getBooksResponseWithLimit(int limit) {
      return given().accept(MediaType.APPLICATION_JSON).queryParam(LIMIT_QUERY, limit).when().get(BOOKS_PATH).as(BooksRepresentation.class);
   }

   @Test
   public void pagination_offset_by_one_on_a_resource_list_of_one_should_return_empty() {
      postBook(new Book("A", new Novelist("A")));
      assertThat(getBooksResponseWithOffset(1).getBooks()).hasSize(0);
   }

   @Test
   public void pagination_offset_by_two_on_a_resource_list_of_one_should_return_empty() {
      postBook(new Book("A", new Novelist("A")));
      assertThat(getBooksResponseWithOffset(2).getBooks()).hasSize(0);
   }

   @Test
   public void pagination_offset_by_two_on_a_resource_list_of_one_should_return_empty_wth_a_link_to_previous_only() {
      postBook(new Book("A", new Novelist("A")));
      List<CustomLink> links = getBooksResponseWithOffset(2).getLinks();
      assertThat(links).hasSize(1);
      CustomLink previousLink = links.get(0);
      assertThat(previousLink.getRel()).isEqualTo("prev");
      assertThat(previousLink.getHref()).endsWith("books?offset=0&limit=10");
   }

   private BooksRepresentation getBooksResponseWithOffset(int offset) {
      return given().accept(MediaType.APPLICATION_JSON).queryParam(OFFSET_QUERY, offset).when().get(BOOKS_PATH).as(BooksRepresentation.class);
   }

   @Test
   public void testPaginationLinkForNextPage() {
      postBook(new Book("A", new Novelist("A")));
      postBook(new Book("B", new Novelist("B")));
      URI uri = UriBuilder.fromUri(RestAssured.baseURI + ":" + RestAssured.port).path(BOOKS_PATH).queryParam(OFFSET_QUERY, "1").queryParam(LIMIT_QUERY, "1").build();
      given().queryParam(LIMIT_QUERY, 1)
           .when().get(BOOKS_PATH)
           .then().header(HttpHeaders.LINK, equalTo("<" + uri + ">; rel=\"next\""));
   }

   @Test
   public void testPaginationWithNoNextPage() {
      postBook(new Book("A", new Novelist("A")));
      given().queryParam(LIMIT_QUERY, 1)
           .when().get(BOOKS_PATH)
           .then().header(HttpHeaders.LINK, nullValue());
   }

   @Test
   public void testPaginationLinkForPreviousPage() {
      postBook(new Book("A", new Novelist("A")));
      postBook(new Book("B", new Novelist("B")));
      URI uri = UriBuilder.fromUri(RestAssured.baseURI + ":" + RestAssured.port).path(BOOKS_PATH).queryParam(OFFSET_QUERY, "0").queryParam(LIMIT_QUERY, "1").build();
      given().queryParam(LIMIT_QUERY, 1).queryParam(OFFSET_QUERY, 1)
           .when().get(BOOKS_PATH)
           .then().header(HttpHeaders.LINK, equalTo("<" + uri + ">; rel=\"prev\""));
   }

   @Test
   public void testPaginationLinksForPreviousAndNextPages() {
      postBook(new Book("A", new Novelist("A")));
      postBook(new Book("B", new Novelist("B")));
      postBook(new Book("C", new Novelist("C")));
      URI next = UriBuilder.fromUri(RestAssured.baseURI + ":" + RestAssured.port).path(BOOKS_PATH).queryParam(OFFSET_QUERY, "2").queryParam(LIMIT_QUERY, "1").build();
      URI previous = UriBuilder.fromUri(RestAssured.baseURI + ":" + RestAssured.port).path(BOOKS_PATH).queryParam(OFFSET_QUERY, "0").queryParam(LIMIT_QUERY, "1").build();

      List<Header> headers = given().queryParam(LIMIT_QUERY, 1).queryParam(OFFSET_QUERY, 1)
           .when().get(BOOKS_PATH).headers().getList(HttpHeaders.LINK);
      assertEquals("<" + next + ">; rel=\"next\"", headers.get(0).getValue());
      assertEquals("<" + previous + ">; rel=\"prev\"", headers.get(1).getValue());
   }
}
