package pop.rtbi.labs.exception;

import com.murex.rtbi.ErrorResource;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;
import static javax.ws.rs.core.Response.status;

/**
 * Created with IntelliJ IDEA.
 * User: ycai
 * Date: 07/08/2015
 * Time: 09:43
 */
@Provider
@Produces({APPLICATION_JSON, APPLICATION_XML})
public class NotFoundExceptionMapper implements ExceptionMapper<NotFoundException> {
   @Override
   public Response toResponse(NotFoundException exception) {
      String details = "The resource does not exist";
      ErrorResource errorResource = new ErrorResource(exception, details);
      return status(errorResource.getErrorCode()).entity(errorResource).build();
   }
}
