package pop.rtbi.labs.representation;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import pop.rtbi.labs.IAuthor;
import pop.rtbi.labs.Journalist;
import pop.rtbi.labs.Novelist;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ycai
 * Date: 20/07/2015
 * Time: 12:01
 */
@XmlRootElement(name = "authors")
public class AuthorsRepresentation {

   @JacksonXmlElementWrapper(useWrapping = false)
   @XmlElement(name = "author")
   @XmlElements(value = {
        @XmlElement(type = Novelist.class),
        @XmlElement(type = Journalist.class),
   })
   private IAuthor[] authors;

   public AuthorsRepresentation() {
      this(new IAuthor[0]);
   }

   public AuthorsRepresentation(IAuthor[] authors) {
      this.authors = authors;
   }

   public AuthorsRepresentation(List<IAuthor> authors) {
      this.authors = authors.toArray(new IAuthor[authors.size()]);
   }


   public IAuthor[] getAuthors() {
      return authors;
   }
}