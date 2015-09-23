package pop.rtbi.labs;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.config.ObjectMapperConfig;
import com.jayway.restassured.config.RestAssuredConfig;
import com.jayway.restassured.response.ValidatableResponse;
import com.jayway.restassured.specification.RequestSpecification;
import com.murex.rtbi.GrizzlyEmbeddedServer;
import javafx.util.Pair;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import pop.rtbi.labs.controller.WorksController;
import pop.rtbi.labs.resource.BookResource;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;
import static javax.ws.rs.core.Response.Status;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.stub;
import static pop.rtbi.labs.LibraryPaths.*;

/**
 * Created with IntelliJ IDEA.
 * User: ycai
 * Date: 10/08/2015
 * Time: 15:48
 */
public class LibraryMainTest {
   public static final int OK = Status.OK.getStatusCode();
   public static final int CREATED = Status.CREATED.getStatusCode();
   public static final int NOT_FOUND = Status.NOT_FOUND.getStatusCode();
   public static final int CONFLICT = Status.CONFLICT.getStatusCode();
   static GrizzlyEmbeddedServer server;
   private static WorkDAO workDAO = WorkDAO.INSTANCE;
   private static AuthorDAO authorDAO = AuthorDAO.INSTANCE;
   private static PublisherDAO publisherDAO = PublisherDAO.INSTANCE;

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
   private static final Book BOOK_6_NOVELIST_2 = new Book("6", NOVELIST_2);
   private static final Book BOOK_7_NOVELIST_2 = new Book("7", NOVELIST_2);
   private static final Book BOOK_8_NOVELIST_2 = new Book("8", NOVELIST_2);
   private static final Book BOOK_9_NOVELIST_5 = new Book("9", NOVELIST_5);

   private static final Book BOOK_10_NOVELIST_1 = new Book("10", NOVELIST_1);
   private static final Publisher PUBLISHER_1 = new Publisher("1");

   private static final Publisher PUBLISHER_2 = new Publisher("2");
   private static final Review REVIEW_PERFECT = new Review(5, "The book is a PERFECTION. Love it !");
   private static final Review REVIEW_GREAT = new Review(5, "Great story.");
   private static final Review REVIEW_BORING = new Review(2, "I was bored to death.");
   private static final Review REVIEW_BAD = new Review(0, "It was garbage.");
   private static final Review REVIEW_ALRIGHT = new Review(3, "The book was alright, not good not bad");

   @BeforeClass
   public static void startServer() throws IOException {
      configureRestAssured();
      initiateBooks();
      initiatePublishers();

      server = new GrizzlyEmbeddedServer(new LibraryApplication());
      server.setHost(LibraryPaths.BASE_URI);
      server.setPort(LibraryPaths.TESTING_PORT);
      server.start();

   }

   private static void configureRestAssured() {
      RestAssured.baseURI = LibraryPaths.BASE_URI;
      RestAssured.port = LibraryPaths.TESTING_PORT;
      RestAssured.basePath = LibraryPaths.BASE_PATH;
      RestAssured.config = RestAssuredConfig.config().objectMapperConfig(new ObjectMapperConfig().jackson2ObjectMapperFactory((cls, charset) -> {
         JsonProvider jsonProvider = new JsonProvider();
         return jsonProvider.getObjectMapper();
      }));
   }


   @AfterClass
   public static void stopServer() throws IOException {
      server.stop();
   }


   @After
   public void tearDown() {
   }

   private UriInfo mockUriInfo(UriBuilder uriBuilder) {
      UriInfo uriInfo = mock(UriInfo.class);

      MultivaluedMap<String, String> multivaluedMap = new MultivaluedHashMap<>();
      stub(uriInfo.getQueryParameters()).toReturn(multivaluedMap);

      stub(uriInfo.getAbsolutePathBuilder()).toReturn(uriBuilder);
      stub(uriInfo.getBaseUriBuilder()).toReturn(getServiceUriBuilder());

      return uriInfo;
   }

   private UriBuilder getServiceUriBuilder() {
      UriBuilder uriBuilder = UriBuilder.fromUri(BASE_URI);
      uriBuilder.port(TESTING_PORT);
      return uriBuilder;
   }

   private static void initiatePublishers() {
      publisherDAO.createPublisher(PUBLISHER_1);
      publisherDAO.createPublisher(PUBLISHER_2);

      String id1 = PUBLISHER_1.getId();
      publisherDAO.publishAWork(id1, BOOK_1_NOVELIST_2);
      publisherDAO.publishAWork(id1, BOOK_2_NOVELIST_3);
      publisherDAO.publishAWork(id1, BOOK_3_NOVELIST_4);

      String id2 = PUBLISHER_2.getId();
      publisherDAO.publishAWork(id2, BOOK_4_NOVELIST_5);
      publisherDAO.publishAWork(id2, BOOK_5_NOVELIST_1);
      publisherDAO.publishAReview(id2, BOOK_5_NOVELIST_1, REVIEW_GREAT);
      publisherDAO.publishAWork(id2, BOOK_6_NOVELIST_2);
      publisherDAO.publishAReview(id2, BOOK_6_NOVELIST_2, REVIEW_PERFECT);
      publisherDAO.publishAWork(id2, BOOK_7_NOVELIST_2);
      publisherDAO.publishAWork(id2, BOOK_8_NOVELIST_2);
      publisherDAO.publishAReview(id2, BOOK_8_NOVELIST_2, REVIEW_BAD);
      publisherDAO.publishAReview(id2, BOOK_8_NOVELIST_2, REVIEW_ALRIGHT);
      publisherDAO.publishAReview(id2, BOOK_8_NOVELIST_2, REVIEW_BORING);
      publisherDAO.publishAWork(id2, BOOK_9_NOVELIST_5);
      publisherDAO.publishAWork(id2, BOOK_10_NOVELIST_1);
   }

   private static void initiateBooks() {
      initiateAuthors();

      workDAO.createBook(BOOK_6_NOVELIST_2);
      workDAO.createBook(BOOK_9_NOVELIST_5);
      workDAO.createBook(BOOK_3_NOVELIST_4);
      workDAO.createBook(BOOK_5_NOVELIST_1);
      workDAO.createBook(BOOK_8_NOVELIST_2);
      workDAO.createBook(BOOK_2_NOVELIST_3);
      workDAO.createBook(BOOK_10_NOVELIST_1);
      workDAO.createBook(BOOK_1_NOVELIST_2);
      workDAO.createBook(BOOK_7_NOVELIST_2);
      workDAO.createBook(BOOK_4_NOVELIST_5);
   }

   private static void initiateAuthors() {
      authorDAO.createAuthor(NOVELIST_2);
      authorDAO.createAuthor(NOVELIST_4);
      authorDAO.createAuthor(NOVELIST_1);
      authorDAO.createAuthor(NOVELIST_5);
      authorDAO.createAuthor(NOVELIST_3);
   }

   private List<BookResource> sortedBookListByTitle() {
      UriBuilder uriBuilder = getServiceUriBuilder().path(WorksController.class);
      UriInfo uriInfo = mockUriInfo(uriBuilder);
      List<BookResource> bookResources = new ArrayList<>();
      bookResources.add(new BookResource(uriInfo, BOOK_1_NOVELIST_2, null));
      bookResources.add(new BookResource(uriInfo, BOOK_2_NOVELIST_3, null));
      bookResources.add(new BookResource(uriInfo, BOOK_3_NOVELIST_4, null));
      bookResources.add(new BookResource(uriInfo, BOOK_4_NOVELIST_5, null));
      bookResources.add(new BookResource(uriInfo, BOOK_5_NOVELIST_1, null));
      bookResources.add(new BookResource(uriInfo, BOOK_6_NOVELIST_2, null));
      bookResources.add(new BookResource(uriInfo, BOOK_7_NOVELIST_2, null));
      bookResources.add(new BookResource(uriInfo, BOOK_8_NOVELIST_2, null));
      bookResources.add(new BookResource(uriInfo, BOOK_9_NOVELIST_5, null));
      bookResources.add(new BookResource(uriInfo, BOOK_10_NOVELIST_1, null));
      return bookResources;
   }

   private List<BookResource> sortedBookListByAuthor() {
      UriBuilder uriBuilder = getServiceUriBuilder().path(WorksController.class);
      UriInfo uriInfo = mockUriInfo(uriBuilder);
      List<BookResource> bookResources = new ArrayList<>();
      bookResources.add(new BookResource(uriInfo, BOOK_5_NOVELIST_1, null));
      bookResources.add(new BookResource(uriInfo, BOOK_10_NOVELIST_1, null));
      bookResources.add(new BookResource(uriInfo, BOOK_1_NOVELIST_2, null));
      bookResources.add(new BookResource(uriInfo, BOOK_6_NOVELIST_2, null));
      bookResources.add(new BookResource(uriInfo, BOOK_7_NOVELIST_2, null));
      bookResources.add(new BookResource(uriInfo, BOOK_8_NOVELIST_2, null));
      bookResources.add(new BookResource(uriInfo, BOOK_2_NOVELIST_3, null));
      bookResources.add(new BookResource(uriInfo, BOOK_3_NOVELIST_4, null));
      bookResources.add(new BookResource(uriInfo, BOOK_4_NOVELIST_5, null));
      bookResources.add(new BookResource(uriInfo, BOOK_9_NOVELIST_5, null));

      return bookResources;
   }


   private com.jayway.restassured.response.Response createGetResponse(RequestSpecification requestSpecification, String path) {
      return requestSpecification.when().get(path);
   }

   private RequestSpecification givenJson() {
      return RestAssured.given().accept(APPLICATION_JSON);
   }

   private RequestSpecification givenXml() {
      return RestAssured.given().accept(APPLICATION_XML);
   }

   @Test
   public void getMethodsAcceptingJsonShouldReturnOkJsonResponse() {
      createGetResponse(givenJson(), AUTHORS_PATH).then().statusCode(OK).contentType(APPLICATION_JSON);
      createGetResponse(givenJson(), AUTHORS_PATH + "/" + NOVELIST_1.getId()).then().statusCode(OK).contentType(APPLICATION_JSON);
      createGetResponse(givenJson(), AUTHORS_PATH + "/" + NOVELIST_1.getId() + "/" + "books").then().statusCode(OK).contentType(APPLICATION_JSON);
      createGetResponse(givenJson(), BOOKS_PATH).then().statusCode(OK).contentType(APPLICATION_JSON);
      createGetResponse(givenJson(), BOOKS_PATH + "/" + BOOK_1_NOVELIST_2.getId()).then().statusCode(OK).contentType(APPLICATION_JSON);
      createGetResponse(givenJson(), PUBLISHERS_PATH).then().statusCode(OK).contentType(APPLICATION_JSON);
      createGetResponse(givenJson(), PUBLISHERS_PATH + "/" + PUBLISHER_1.getId()).then().statusCode(OK).contentType(APPLICATION_JSON);
      createGetResponse(givenJson(), PUBLISHERS_PATH + "/" + PUBLISHER_1.getId() + "/" + "works").then().statusCode(OK).contentType(APPLICATION_JSON);
      createGetResponse(givenJson(), PUBLISHERS_PATH + "/" + PUBLISHER_1.getId() + "/" + "reviews").then().statusCode(OK).contentType(APPLICATION_JSON);
   }

   @Test
   public void getMethodsAcceptingXmlShouldReturnOkXmlResponse() {
      createGetResponse(givenXml(), AUTHORS_PATH).then().statusCode(OK).contentType(APPLICATION_XML);
      createGetResponse(givenXml(), AUTHORS_PATH + "/" + NOVELIST_1.getId()).then().statusCode(OK).contentType(APPLICATION_XML);
      createGetResponse(givenXml(), AUTHORS_PATH + "/" + NOVELIST_1.getId() + "/" + "books").then().statusCode(OK).contentType(APPLICATION_XML);
      createGetResponse(givenXml(), BOOKS_PATH).then().statusCode(OK).contentType(APPLICATION_XML);
      createGetResponse(givenXml(), BOOKS_PATH + "/" + BOOK_1_NOVELIST_2.getId()).then().statusCode(OK).contentType(APPLICATION_XML);
      createGetResponse(givenXml(), PUBLISHERS_PATH).then().statusCode(OK).contentType(APPLICATION_XML);
      createGetResponse(givenXml(), PUBLISHERS_PATH + "/" + PUBLISHER_1.getId()).then().statusCode(OK).contentType(APPLICATION_XML);
      createGetResponse(givenXml(), PUBLISHERS_PATH + "/" + PUBLISHER_1.getId() + "/" + "works").then().statusCode(OK).contentType(APPLICATION_XML);
      createGetResponse(givenXml(), PUBLISHERS_PATH + "/" + PUBLISHER_1.getId() + "/" + "reviews").then().statusCode(OK).contentType(APPLICATION_XML);
   }

   @Test
   public void postMethodsContentTypeXmlShouldReturnCreateResponse() {
      String authorId = extractId(createPostMethod(APPLICATION_XML, createXmlNovelist("Some Novelist"), AUTHORS_PATH).then().statusCode(CREATED));
      String bookId = extractId(createPostMethod(APPLICATION_XML, createXmlBook("Some Title", "Some Novelist"), BOOKS_PATH).then().statusCode(CREATED));
      String publisherId = extractId(createPostMethod(APPLICATION_XML, createXmlPublisher("Some Publisher"), PUBLISHERS_PATH).then().statusCode(CREATED));
      createPostMethod(APPLICATION_XML, createXmlPublisherWork(bookId), PUBLISHERS_PATH + "/" + publisherId + "/" + "works").then().statusCode(CREATED);
      createPostMethod(APPLICATION_XML, createXmlPublisherReview(bookId, 0, "Some comment"), PUBLISHERS_PATH + "/" + publisherId + "/" + "reviews").then().statusCode(CREATED);

      authorDAO.deleteAuthor(authorId);
      workDAO.deleteBook(bookId);
      publisherDAO.deletePublisher(publisherId);
   }

   @Test
   public void postMethodsContentTypeJsonShouldReturnCreateResponse() {
      String authorId = extractId(createPostMethod(APPLICATION_JSON, createJsonNovelist("Some Novelist"), AUTHORS_PATH).then().statusCode(CREATED));
      String bookId = extractId(createPostMethod(APPLICATION_JSON, createJsonBook("Some Title", "Some Novelist"), BOOKS_PATH).then().statusCode(CREATED));
      String publisherId = extractId(createPostMethod(APPLICATION_JSON, createJsonPublisher("Some Publisher"), PUBLISHERS_PATH).then().statusCode(CREATED));
      createPostMethod(APPLICATION_JSON, createJsonPublisherWork(bookId), PUBLISHERS_PATH + "/" + publisherId + "/" + "works").then().statusCode(CREATED);
      createPostMethod(APPLICATION_JSON, createJsonPublisherReview(bookId, 0, "Some comment"), PUBLISHERS_PATH + "/" + publisherId + "/" + "reviews").then().statusCode(CREATED);

      authorDAO.deleteAuthor(authorId);
      workDAO.deleteBook(bookId);
      publisherDAO.deletePublisher(publisherId);
   }

   @Test
   public void getMethodsWithQueriesShouldReturnSortedPaginatedAndFilteredResponse() {
      createGetResponse(createQueriesRequest(2, 2, "name-asc", null), AUTHORS_PATH).then().statusCode(OK).contentType(APPLICATION_JSON)
           .body("offset", equalTo(2))
           .body("limit", equalTo(2))
           .body("totalCount", equalTo(5))
           .body("author.size()", equalTo(2))
           .body("author[0].entity.id", equalTo(NOVELIST_3.getId()))
           .body("author[1].entity.id", equalTo(NOVELIST_4.getId()))
           .body("next", notNullValue())
           .body("prev", notNullValue());

      createGetResponse(createQueriesRequest(1, 1, "title-asc", new Pair<>("author", "2")), BOOKS_PATH).then().statusCode(OK).contentType(APPLICATION_JSON)
           .body("offset", equalTo(1))
           .body("limit", equalTo(1))
           .body("totalCount", equalTo(10))
           .body("book.size()", equalTo(1))
           .body("book[0].entity.id", equalTo(BOOK_6_NOVELIST_2.getId()))
           .body("next", notNullValue())
           .body("prev", notNullValue());
   }

   @Test
   public void getNotExistingResourceShouldReturnAErrorMessageWithNotFoundError() throws Exception {
      createGetResponse(givenJson(), AUTHORS_PATH + "/notExistingId").then().statusCode(NOT_FOUND).contentType(APPLICATION_JSON)
           .body("errorCode", equalTo(NOT_FOUND))
           .body("errorMessage", equalTo("HTTP " + NOT_FOUND + " " + Status.NOT_FOUND.getReasonPhrase()))
           .body("errorDetails", equalTo("The resource does not exist"));
   }

   @Test
   public void postDoubleEntityShouldReturnAConflictExceptionErrorMessage() throws Exception {
      createPostMethod(APPLICATION_JSON, createJsonNovelist(NOVELIST_1.getName()), AUTHORS_PATH).then().statusCode(CONFLICT).contentType(APPLICATION_JSON)
           .body("errorCode", equalTo(CONFLICT))
           .body("errorMessage", equalTo("The resource already exists"))
           .body("errorDetails.href", equalTo(BASE_URI + ":" + TESTING_PORT + "/" + getAuthorPath(NOVELIST_1.getId())));

      createPostMethod(APPLICATION_JSON, createJsonBook(BOOK_1_NOVELIST_2.getTitle(), BOOK_1_NOVELIST_2.getAuthor().getName()), BOOKS_PATH).then().statusCode(CONFLICT).contentType(APPLICATION_JSON)
           .body("errorCode", equalTo(CONFLICT))
           .body("errorMessage", equalTo("The resource already exists"))
           .body("errorDetails.href", equalTo(BASE_URI + ":" + TESTING_PORT + "/" + getBookPath(BOOK_1_NOVELIST_2.getId())));

      createPostMethod(APPLICATION_JSON, createJsonPublisher(PUBLISHER_1.getName()), PUBLISHERS_PATH).then().statusCode(CONFLICT).contentType(APPLICATION_JSON)
           .body("errorCode", equalTo(CONFLICT))
           .body("errorMessage", equalTo("The resource already exists"))
           .body("errorDetails.href", equalTo(BASE_URI + ":" + TESTING_PORT + "/" + PUBLISHERS_PATH + "/" + PUBLISHER_1.getId()));
   }

   private RequestSpecification createQueriesRequest(int offset, int limit, String sortBy, Pair<String, String> filter) {
      RequestSpecification given = RestAssured.given();
      if (offset != -1) {
         given.queryParam(OFFSET_QUERY, offset);
      }
      if (limit != -1) {
         given.queryParam(LIMIT_QUERY, limit);
      }
      if (sortBy != null) {
         given.queryParam(SORT_BY_QUERY, sortBy);
      }
      if (filter != null) {
         given.queryParam(filter.getKey(), filter.getValue());
      }
      return given;
   }

   private String createXmlPublisherReview(String bookId, int rate, final String comment) {
      return "<entry>" +
                 "<work>" + bookId + "</work>" +
                 "<review>" +
                    "<rate>" + rate + "</rate>" +
                    "<comment>" + comment + "</comment>" +
                 "</review>" +
              "</entry>";
   }

   private String createXmlPublisherWork(String bookId) {
      return "<entry>" +
        "<work>" + bookId + "</work>" +
        "</entry>";
   }

   private String createXmlPublisher(final String name) {
      return "<publisher>" +
        "<name>" + name + "</name>" +
        "</publisher>";
   }

   private String createXmlBook(final String title, final String authorName) {
      return "<book>" +
        "<title>" + title + "</title>" +
        "<author>" +
        "<name>" + authorName + "</name>" +
        "</author>" +
        "</book>";
   }

   private String createXmlNovelist(final String name) {
      return "<novelist>" +
        "<name>" + name + "</name>" +
        "<type>novelist</type>" +
        "</novelist>";
   }

   private String createJsonPublisherReview(String bookId, int rate, final String comment) {
      return "{" +
        "\"work\":\"" + bookId + "\"," +
        "\"review\":{" +
        "\"rate\":\"" + rate + "\"," +
        "\"comment\":\"" + comment + "\"" +
        "}" +
        "}";
   }

   private String createJsonPublisherWork(String bookId) {
      return "{" +
        "\"work\":\"" + bookId + "\"" +
        "}";
   }

   private String createJsonPublisher(final String name) {
      return "{" +
        "\"name\":\"" + name + "\"" +
        "}";
   }

   private String createJsonBook(final String title, final String authorName) {
      return "{" +
        "\"title\":\"" + title + "\"," +
        "\"author\":{" +
        "\"name\":\"" + authorName + "\"" +
        "}" +
        "}";
   }

   private String createJsonNovelist(final String name) {
      return "{" +
        "\"name\":\"" + name + "\"," +
        "\"type\":\"Novelist\"" +
        "}";
   }

   private String extractId(ValidatableResponse validatableResponse) {
      return validatableResponse.contentType(APPLICATION_JSON).extract().jsonPath().get("entity.id");
   }

   private com.jayway.restassured.response.Response createPostMethod(String applicationXml, String body, String authorsPath) {
      return RestAssured.given().contentType(applicationXml).body(body).when().post(authorsPath);
   }
}