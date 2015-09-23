package pop.rtbi.labs.model.post;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import pop.rtbi.labs.Review;

/**
 * Created with IntelliJ IDEA.
 * User: ycai
 * Date: 11/08/2015
 * Time: 17:26
 */
public class PostReview {
   private String work;
   private Review review;

   @JsonCreator
   public PostReview(@JsonProperty("work") String work, @JsonProperty("review") Review review) {
      this.work = work;
      this.review = review;
   }

   public String getWork() {
      return work;
   }

   public Review getReview() {
      return review;
   }
}
