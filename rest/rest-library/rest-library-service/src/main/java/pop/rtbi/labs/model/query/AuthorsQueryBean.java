package pop.rtbi.labs.model.query;

import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ycai
 * Date: 06/08/2015
 * Time: 15:17
 */
public class AuthorsQueryBean extends DefaultQueryBean {
   @QueryParam("name")
   private List<String> name = new ArrayList<>();

   public List<String> getName() {
      return name;
   }

   public void addName(String name) {
      this.name.add(name);
   }

   public void setName(List<String> name) {
      this.name = name;
   }

   @Override
   public MultivaluedMap<String, String> getMultivaluedMap() {
      MultivaluedMap<String, String> multivaluedMap = super.getMultivaluedMap();
      multivaluedMap.addAll("name", name);
      return multivaluedMap;
   }

   public MultivaluedMap<String, String> getFilters() {
      MultivaluedMap<String, String> multivaluedMap = new MultivaluedHashMap<>();
      multivaluedMap.addAll("name", name);
      return multivaluedMap;
   }
}
