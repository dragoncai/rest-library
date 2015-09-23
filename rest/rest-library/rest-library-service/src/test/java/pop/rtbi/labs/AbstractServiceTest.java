package pop.rtbi.labs;

import org.junit.After;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.stub;

/**
 * Created with IntelliJ IDEA.
 * User: ycai
 * Date: 18/06/2015
 * Time: 10:29
 */
public abstract class AbstractServiceTest {
   @After
   public void tearDown() {
      AuthorDAO.INSTANCE.readAuthors().clear();
      WorkDAO.INSTANCE.readBooks().clear();
      PublisherDAO.INSTANCE.readPublishers().clear();
   }

   protected UriInfo mockUriInfo(UriBuilder uriBuilder) {
      UriInfo uriInfo = mock(UriInfo.class);

      MultivaluedMap<String, String> multivaluedMap = new MultivaluedHashMap<>();
      stub(uriInfo.getQueryParameters()).toReturn(multivaluedMap);

      stub(uriInfo.getAbsolutePathBuilder()).toReturn(uriBuilder);
      stub(uriInfo.getBaseUriBuilder()).toReturn(getServiceUriBuilder());

      return uriInfo;
   }

   protected UriBuilder getServiceUriBuilder() {
      UriBuilder uriBuilder = UriBuilder.fromUri(LibraryPaths.BASE_URI);
      uriBuilder.port(LibraryPaths.TESTING_PORT);
      return uriBuilder;
   }
}
