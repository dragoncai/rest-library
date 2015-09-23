package pop.rtbi.labs.exception;

import com.murex.rtbi.ErrorResource;
import com.murex.rtbi.LinkedResource;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.core.Response;

/**
 * Created with IntelliJ IDEA.
 * User: ycai
 * Date: 31/07/2015
 * Time: 16:44
 */
public class ConflictException extends ClientErrorException {

   public ConflictException(LinkedResource resource) {
       this(Response.status(Response.Status.CONFLICT).entity(new ErrorResource(Response.Status.CONFLICT, "The resource already exists", resource)).build());
   }

   public ConflictException() {
      super(Response.Status.CONFLICT);
   }

   public ConflictException(Response response) {
      super(validate(response, Response.Status.CONFLICT));
   }

   static Response validate(final Response response, Response.Status expectedStatus) {
      if (expectedStatus.getStatusCode() != response.getStatus()) {
         throw new IllegalArgumentException(String.format("Invalid response status code. Expected [%d], was [%d].",
              expectedStatus.getStatusCode(), response.getStatus()));
      }
      return response;
   }
}
