package pop.rtbi.labs.resource;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.config.ObjectMapperConfig;
import com.jayway.restassured.config.RestAssuredConfig;
import com.jayway.restassured.response.Response;
import com.murex.rtbi.GrizzlyEmbeddedServer;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import pop.rtbi.labs.*;
import pop.rtbi.labs.representation.AuthorsRepresentation;

import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.jayway.restassured.RestAssured.*;

/**
 * Created with IntelliJ IDEA.
 * User: ycai
 * Date: 18/06/2015
 * Time: 10:29
 */
public abstract class OldAbstractResourcesTest {
   static GrizzlyEmbeddedServer server;

   @BeforeClass
   public static void startServer() throws IOException {
      configureRestAssured();

      server = new GrizzlyEmbeddedServer(new OldLibraryService());
      server.setHost(baseURI);
      server.setPort(port);
      server.start();

   }

   private static void configureRestAssured() {
      RestAssured.baseURI = OldLibraryPaths.BASE_URI;
      RestAssured.port = OldLibraryPaths.TESTING_PORT;
      RestAssured.basePath = OldLibraryPaths.BASE_PATH;
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
//      AuthorDAO.INSTANCE.readAuthors().clear();
//      WorkDAO.INSTANCE.readBooks().clear();
      Response response = given().accept(MediaType.APPLICATION_JSON).get(OldLibraryPaths.AUTHORS_PATH);
      List<IAuthor> authors = Arrays.asList(response.as(AuthorsRepresentation.class).getAuthors());
      List<String> authorIDs = new ArrayList<>();
      authors.forEach(author -> authorIDs.add(author.getId()));
      authorIDs.forEach(id -> delete(OldLibraryPaths.getAuthorPath(id)).then().statusCode(HttpStatus.SC_OK));
   }


   protected Response postAuthor(String name) {
      Novelist novelist = new Novelist(name);
      return given().request()
           .accept(MediaType.APPLICATION_JSON)
           .contentType(MediaType.APPLICATION_JSON)
           .body(novelist)
           .when()
           .post(OldLibraryPaths.AUTHORS_PATH);
   }

   protected Response postBook(Novelist novelist, String title) {
      return given().request()
           .accept(MediaType.APPLICATION_JSON)
           .contentType(MediaType.APPLICATION_JSON)
           .body(new Book(title, novelist))
           .when()
           .post(OldLibraryPaths.BOOKS_PATH);
   }

   protected Response postBook(Book book) {
      return given().request()
           .accept(MediaType.APPLICATION_JSON)
           .contentType(MediaType.APPLICATION_JSON)
           .body(book)
           .when()
           .post(OldLibraryPaths.BOOKS_PATH);
   }
}
