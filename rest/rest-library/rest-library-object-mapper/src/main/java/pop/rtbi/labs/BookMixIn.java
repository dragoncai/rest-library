package pop.rtbi.labs;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created with IntelliJ IDEA.
 * User: ycai
 * Date: 23/07/2015
 * Time: 17:01
 */
public interface BookMixIn {
   @XmlElement(type = Novelist.class)
   IAuthor getAuthor();
}
