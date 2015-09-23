package pop.rtbi.labs;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: ycai
 * Date: 10/08/2015
 * Time: 14:01
 */
public final class Publisher {
   public static final String ID = "id";
   public static final String NAME = "name";
   public static final String WORKS = "works";
   public static final String REVIEWS = "reviews";
   private final String id;
   private final String name;
   private Map<IAuthor, List<Work>> works;
   private Map<Work, List<Review>> reviews;

   public Publisher() {
      this("Unknown");
   }

   public Publisher(String name) {
      this(UUID.randomUUID().toString().replace("-", ""), name);
   }

   public Publisher(String id, String name) {
      this.id = id;
      this.name = name;
      this.works = new LinkedHashMap<>();
      this.reviews = new LinkedHashMap<>();
   }

   public Map<IAuthor, List<Work>> getWorks() {
      return works;
   }

   public Map<Work, List<Review>> getReviews() {
      return reviews;
   }

   public String getName() {
      return name;
   }

   public String getId() {
      return id;
   }

   public void addWork(IAuthor author, Work work) {
      if (!works.containsKey(author)) {
         works.put(author, new ArrayList<>());
      }
      works.get(author).add(work);
   }

   public void addReview(Work work, Review review) {
      if (isPublished(work)) {
         if (!reviews.containsKey(work)) {
            reviews.put(work, new ArrayList<>());
         }
         reviews.get(work).add(review);
      }
   }

   private boolean isPublished(Work work) {
      return works.containsKey(work.getAuthor()) && works.get(work.getAuthor()).contains(work);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
          return true;
      }
      if (o == null || getClass() != o.getClass()) {
          return false;
      }

      Publisher publisher = (Publisher) o;

      return id.equals(publisher.id) && name.equals(publisher.name) && works.equals(publisher.works) && reviews.equals(publisher.reviews);

   }

   @Override
   public int hashCode() {
      return name != null ? name.hashCode() : 0;
   }
}
