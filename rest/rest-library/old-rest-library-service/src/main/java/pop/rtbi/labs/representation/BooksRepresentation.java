package pop.rtbi.labs.representation;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import pop.rtbi.labs.Book;
import pop.rtbi.labs.Work;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ycai
 * Date: 07/07/2015
 * Time: 16:58
 */
@XmlRootElement(name = "books")
@XmlType(propOrder = {"books", "totalCount" ,"links"})
public class BooksRepresentation {
   @JacksonXmlElementWrapper(useWrapping = false)
   @XmlElement(name = "book", type = Book.class)
   private Work[] books;

   private List<CustomLink> links;

   private int totalCount;

   public BooksRepresentation() {
      books = new Book[0];
      links = new ArrayList<>();
      totalCount = 0;
   }

   public BooksRepresentation(Work[] authors, List<CustomLink> links, int totalCount) {
      this.books = authors;
      this.links = links;
      this.totalCount = totalCount;
   }

   public Work[] getBooks() {
      return books;
   }

   public List<CustomLink> getLinks() {
      return links;
   }

   public int getTotalCount() {
      return totalCount;
   }
}
