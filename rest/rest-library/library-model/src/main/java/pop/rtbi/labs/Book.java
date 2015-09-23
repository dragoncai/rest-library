package pop.rtbi.labs;

import java.util.Objects;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: ycai
 * Date: 20/05/15
 * Time: 10:02
 */
public final class Book implements Work {
   private final IAuthor author;
   private final String title;
   private final String id;

   public Book() {
      this("Unknown", new Novelist());
   }

   public Book(String title, IAuthor author) {
      this(UUID.randomUUID().toString().replace("-", ""), title, author);
   }

   public Book(String id, String title, IAuthor author) {
      this.id = id;
      this.title = title;
      this.author = author;
   }

   public IAuthor getAuthor() {
      return author;
   }

   public String getTitle() {
      return title;
   }

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

      Book book = (Book) o;

      return author.hashCode() == book.author.hashCode() && title.equals(book.title) && id.equals(book.id);

   }

   @Override
   public int hashCode() {
      return Objects.hash(author, title);
   }

   @Override
   public String toString() {
      return id + "\t" + title + "\t" + author.getName();
   }
}
