package pop.rtbi.labs.model.post;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import pop.rtbi.labs.IAuthor;
import pop.rtbi.labs.Journalist;
import pop.rtbi.labs.Novelist;

/**
 * Created with IntelliJ IDEA.
 * User: ycai
 * Date: 19/08/2015
 * Time: 12:19
 */
public class PostAuthor {
   private String name;
   private String type;
   private IAuthor author;

   @JsonCreator
   public PostAuthor(@JsonProperty("name") String name, @JsonProperty("type") String type) {
      this.name = name;
      this.type = type;
      author = createAuthor();
   }

   @JsonIgnore
   public IAuthor getAuthor(){
      return author;
   }

   private IAuthor createAuthor() {
      if ("novelist".equals(type.toLowerCase())) {
         return new Novelist(name);
      }
      else if ("journalist".equals(type.toLowerCase())) {
         return new Journalist(name);
      }
      return null;
   }

   public String getName() {
      return name;
   }

   public String getType() {
      return type;
   }
}
