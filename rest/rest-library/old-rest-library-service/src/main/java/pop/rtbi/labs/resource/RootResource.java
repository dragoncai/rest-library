package pop.rtbi.labs.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created with IntelliJ IDEA.
 * User: ycai
 * Date: 16/06/2015
 * Time: 16:30
 */
@Path("/")
public class RootResource {
   @GET
   @Produces(MediaType.TEXT_HTML)
   public Response getName() {
      String indexPage =
           "<!DOCTYPE html>\n" +
                "<html>\n" +
                "\t<head><title>REST Library API</title></head>\n" +
                "\t<body>\n" +
                "\t\t<ul>\n" +
                "\t\t\t<li><a href=\"./books\">/books</a></li>\n" +
                "\t\t\t<li><a href=\"./authors\">/authors</a></li>\n" +
                "\t\t</ul>\n" +
                "\t</body>\n" +
                "</html>";
      return Response.ok(indexPage).build();
   }
}
