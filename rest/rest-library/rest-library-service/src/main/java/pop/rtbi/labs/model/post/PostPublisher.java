package pop.rtbi.labs.model.post;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import pop.rtbi.labs.Publisher;

/**
 * Created with IntelliJ IDEA.
 * User: ycai
 * Date: 11/08/2015
 * Time: 15:08
 */
@JsonRootName(value = "publisher")
public final class PostPublisher {
   private final String name;
   private final Publisher publisher;

   public PostPublisher() {
      this(null);
   }

   @JsonCreator
   public PostPublisher(@JsonProperty("name") String name) {
      this.name = name;
      publisher = new Publisher(name);
   }

   @JsonIgnore
   public Publisher getPublisher() {
      return publisher;
   }

   public String getName() {
      return name;
   }
}
