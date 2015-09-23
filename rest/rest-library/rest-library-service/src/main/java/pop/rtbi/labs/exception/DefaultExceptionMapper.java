package pop.rtbi.labs.exception;

import com.murex.rtbi.ErrorResource;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Created with IntelliJ IDEA.
 * User: ycai
 * Date: 30/07/2015
 * Time: 16:29
 */
@Provider
public class DefaultExceptionMapper implements ExceptionMapper<Throwable> {
   @Override
   public Response toResponse(Throwable exception) {
      ErrorResource errorResource = new ErrorResource(Response.Status.INTERNAL_SERVER_ERROR, exception.getMessage(), exception.getCause());
      return Response.status(errorResource.getErrorCode()).entity(errorResource).build();
   }
}
