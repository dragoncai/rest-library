package pop.rtbi.labs;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created with IntelliJ IDEA.
 * User: ycai
 * Date: 16/06/2015
 * Time: 15:05
 */
public class BookTest {
   @Test
   public void testConstructorBookParamShouldGenerateDifferentId() {
      Book book = new Book("book", new Novelist());
      Book book1 = new Book("book", new Novelist());
      assertNotEquals(book.getId(), book1.getId());
   }

   @Test
   public void testEqualsBetweenSameId() {
      Book book = new Book("id", "book", new Novelist());
      Book book1 = new Book("id", "book", new Novelist());
      assertTrue(book.equals(book1));
   }

   @Test
   public void testEqualsBetweenDifferentId() {
      Book book = new Book("book", new Novelist());
      Book book1 = new Book("book", new Novelist());
      assertFalse(book.equals(book1));
   }

   @Test
   public void testEqualsBetweenDifferentTitle() {
      Book book = new Book("id", "book", new Novelist());
      Book book1 = new Book("id", "book1", new Novelist());
      assertFalse(book.equals(book1));
   }

   @Test
   public void testHashcodeTrueForIdentical() {
      Book book = new Book("id", "book", new Novelist());
      Book book1 = new Book("id", "book", new Novelist());
      assertTrue(book.hashCode() == book1.hashCode());
   }

   @Test
   public void testHashcodeTrueDifferentId() {
      Book book = new Book("book", new Novelist());
      Book book1 = new Book("book", new Novelist());
      assertTrue(book.hashCode() == book1.hashCode());
   }

   @Test
   public void testHashcodeTrueForDifferentTitle() {
      Book book = new Book("book", new Novelist());
      Book book1 = new Book("book1", new Novelist());
      assertFalse(book.hashCode() == book1.hashCode());
   }
}