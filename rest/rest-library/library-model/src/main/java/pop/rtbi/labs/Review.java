package pop.rtbi.labs;

/**
 * Created with IntelliJ IDEA.
 * User: ycai
 * Date: 11/08/2015
 * Time: 11:29
 */
public final class Review {
   private final int rate;
   private final String comment;

   private Review() {
      rate = 0;
      comment = null;
   }

   public Review(int rate, String comment) {
      this.rate = rate;
      this.comment = comment;
   }

   public int getRate() {
      return rate;
   }

   public String getComment() {
      return comment;
   }
}
