package pop.rtbi.labs.resource;

import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import java.util.ArrayList;

/**
 * Root resource (exposed at "myresource" path)
 */
@Path("chat")
public class ChatResource {

	static protected ArrayList<AsyncResponse> responses = new ArrayList<AsyncResponse>();

	@GET
	@Produces("text/plain")
	public void receive(@Suspended AsyncResponse response) {
		responses.add(response);
	}

	@POST
	@Consumes("text/plain")
	public void send(final String message) {
		for (AsyncResponse response : responses) {
			response.resume(message);
		}
		responses.clear();
	}
}