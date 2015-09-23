package pop.rtbi.labs;

import java.util.Objects;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: ycai
 * Date: 20/05/15
 * Time: 10:15
 */
public final class Novelist implements IAuthor {
   private final String id;
   private final String name;

   public Novelist() {
      this("Unknown");
   }

   public Novelist(String name) {
      this(UUID.randomUUID().toString().replace("-", ""), name);
   }

   public Novelist(String id, String name) {
      this.id = id;
      this.name = name;
   }

   @Override
   public String getName() {
      return name;
   }

   @Override
   public String getId() {
      return id;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      }
      if (o == null || getClass() != o.getClass()) {
         return false;
      }

      Novelist novelist = (Novelist) o;

      return id.equals(novelist.id) && name.equals(novelist.name);

   }

   @Override
   public int hashCode() {
      return Objects.hash(name);
   }

   @Override
   public String toString() {
      return id + "\t" + name;
   }

}
