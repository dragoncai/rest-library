package pop.rtbi.labs;

import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

/**
 * Created with IntelliJ IDEA.
 * User: ycai
 * Date: 11/08/2015
 * Time: 10:45
 */
public class PublisherTest {

   public static final Novelist AUTHOR = new Novelist();
   public static final Book WORK1 = new Book("1", AUTHOR);
   public static final Book WORK2 = new Book("2", AUTHOR);
   public static final Review REVIEW1 = new Review(5, "Great");
   public static final Review REVIEW2 = new Review(2, "Bad");

   @Test
   public void addANewPublishedWorkShouldAddItAsANewItem() {
      Publisher publisher = new Publisher();
      publisher.addWork(AUTHOR, WORK1);
      assertThat(publisher.getWorks().get(AUTHOR)).containsExactly(WORK1);
   }

   @Test
   public void addATwoPublishedBookWithTheSameAuthorShouldBothAddToTheList() {
      Publisher publisher = new Publisher();
      publisher.addWork(AUTHOR, WORK1);
      publisher.addWork(AUTHOR, WORK2);
      assertThat(publisher.getWorks().get(AUTHOR)).containsExactly(WORK1, WORK2);
   }

   @Test
   public void addARatingToANonPublishedBookShouldNotBeAdded() {
      Publisher publisher = new Publisher();
      publisher.addReview(WORK1, REVIEW1);
      assertThat(publisher.getReviews()).hasSize(0);
   }

   @Test
   public void addARatingToAPublishedBookShouldAddTheReview() {
      Publisher publisher = new Publisher();
      publisher.addWork(AUTHOR, WORK1);
      publisher.addReview(WORK1, REVIEW1);
      assertThat(publisher.getReviews().get(WORK1)).containsExactly(REVIEW1);
   }

   @Test
   public void addTwoRatingtoAPublishedBookShouldAddTheReviews() {
      Publisher publisher = new Publisher();
      publisher.addWork(AUTHOR, WORK1);
      publisher.addReview(WORK1, REVIEW1);
      publisher.addReview(WORK1, REVIEW2);
      assertThat(publisher.getReviews().get(WORK1)).containsExactly(REVIEW1, REVIEW2);
   }
}