package pop.rtbi.labs;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;

import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;

/**
 * Created with IntelliJ IDEA.
 * User: ycai
 * Date: 27/05/15
 * Time: 12:09
 */
public class ServerEmbedded {
	public static void main(String[] args) throws IOException {
		URI baseUri = UriBuilder.fromUri("http://localhost").port(9999).build();
		ChatApplication chatApplication = new ChatApplication();
		HttpServer server = GrizzlyHttpServerFactory.createHttpServer(baseUri, chatApplication);
		server.start();
	}
}
