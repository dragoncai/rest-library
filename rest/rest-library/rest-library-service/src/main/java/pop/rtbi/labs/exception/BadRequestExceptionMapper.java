package pop.rtbi.labs.exception;

import com.murex.rtbi.ErrorResource;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import static javax.ws.rs.core.Response.status;

/**
 * Created with IntelliJ IDEA.
 * User: ycai
 * Date: 19/08/2015
 * Time: 12:26
 */
@Provider
public class BadRequestExceptionMapper implements ExceptionMapper<BadRequestException> {
   @Override
   public Response toResponse(BadRequestException exception) {
      ErrorResource errorResource = new ErrorResource(exception);
      return status(errorResource.getErrorCode()).entity(errorResource).build();
   }
}
