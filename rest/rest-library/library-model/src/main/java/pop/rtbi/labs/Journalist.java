package pop.rtbi.labs;

import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: ycai
 * Date: 23/07/2015
 * Time: 14:50
 */
public final class Journalist implements IAuthor {
   private final String name;
   private final String id;

   private Journalist() {
      this("aJournalist");
   }

   public Journalist(String name) {
      this(name, UUID.randomUUID().toString().replace("-", ""));
   }

   public Journalist(String name, String id) {
      this.name = name;
      this.id = id;
   }

   @Override
   public String getName() {
      return name;
   }

   @Override
   public String getId() {
      return id;
   }
}
