package pop.rtbi.labs;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created with IntelliJ IDEA.
 * User: ycai
 * Date: 16/06/2015
 * Time: 12:32
 */
public class NovelistTest {

   public static final String ID = "id";
   public static final String AUTHOR = "author";

   @Test
   public void testConstructorAuthorParamShouldGenerateDifferentId() {
      Novelist novelist = new Novelist(AUTHOR);
      Novelist novelist1 = new Novelist(AUTHOR);
      assertNotEquals(novelist.getId(), novelist1.getId());
   }

   @Test
   public void testEqualsBetweenSameId() {
      Novelist novelist = new Novelist(ID, AUTHOR);
      Novelist novelist1 = new Novelist(ID, AUTHOR);
      assertTrue(novelist.equals(novelist1));
   }

   @Test
   public void testEqualsBetweenDifferentId() {
      Novelist novelist = new Novelist(AUTHOR);
      Novelist novelist1 = new Novelist(AUTHOR);
      assertFalse(novelist.equals(novelist1));
   }

   @Test
   public void testEqualsBetweenDifferentName() {
      Novelist novelist = new Novelist(ID, AUTHOR);
      Novelist novelist1 = new Novelist(ID, "novelist1");
      assertFalse(novelist.equals(novelist1));
   }

   @Test
   public void testHashcodeTrueForIdentical() {
      Novelist novelist = new Novelist(ID, AUTHOR);
      Novelist novelist1 = new Novelist(ID, AUTHOR);
      assertTrue(novelist.hashCode() == novelist1.hashCode());
   }

   @Test
   public void testHashcodeTrueDifferentId() {
      Novelist novelist = new Novelist(AUTHOR);
      Novelist novelist1 = new Novelist(AUTHOR);
      assertTrue(novelist.hashCode() == novelist1.hashCode());
   }

   @Test
   public void testHashcodeTrueForDifferentName() {
      Novelist novelist = new Novelist(AUTHOR);
      Novelist novelist1 = new Novelist("novelist1");
      assertFalse(novelist.hashCode() == novelist1.hashCode());
   }
}