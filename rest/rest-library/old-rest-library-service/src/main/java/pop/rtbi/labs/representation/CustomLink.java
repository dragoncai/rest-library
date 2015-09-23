package pop.rtbi.labs.representation;

import javax.xml.bind.annotation.XmlAttribute;

/**
 * Created with IntelliJ IDEA.
 * User: ycai
 * Date: 09/07/2015
 * Time: 14:24
 */
public final class CustomLink {
   @XmlAttribute
   private final String href;
   @XmlAttribute
   private final String rel;

   public CustomLink(String href, String rel) {
      this.href = href;
      this.rel = rel;
   }

   public CustomLink(String href) {
      this(href, null);
   }

   public CustomLink() {
      this(null);
   }

   public String getHref() {
      return href;
   }

   public String getRel() {
      return rel;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
          return true;
      }
      if (o == null || getClass() != o.getClass()) {
          return false;
      }

      CustomLink customLink = (CustomLink) o;

      return !(href != null ? !href.equals(customLink.href) : customLink.href != null) && !(rel != null ? !rel.equals(customLink.rel) : customLink.rel != null);

   }

   @Override
   public int hashCode() {
      int result = href != null ? href.hashCode() : 0;
      result = 31 * result + (rel != null ? rel.hashCode() : 0);
      return result;
   }
}

