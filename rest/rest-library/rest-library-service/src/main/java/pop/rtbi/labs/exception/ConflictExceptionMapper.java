package pop.rtbi.labs.exception;

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
public class ConflictExceptionMapper implements ExceptionMapper<ConflictException> {
   @Override
   public Response toResponse(ConflictException exception) {
      return exception.getResponse();
   }
}
