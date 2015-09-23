package pop.rtbi.labs.model.post;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created with IntelliJ IDEA.
 * User: ycai
 * Date: 11/08/2015
 * Time: 17:02
 */
public class PostWork {
   private String work;

   @JsonCreator
   public PostWork(@JsonProperty("work") String work) {
      this.work = work;
   }

   public String getWork() {
      return work;
   }
}
