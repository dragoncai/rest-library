package pop.rtbi.labs.model.query;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ycai
 * Date: 06/08/2015
 * Time: 11:33
 */
public class DefaultQueryBean {
   @DefaultValue("0")
   @QueryParam("offset")
   private int offset = 0;
   @DefaultValue("10")
   @QueryParam("limit")
   private int limit = 10;
   @QueryParam("sort-by")
   private String sortBy;
   @QueryParam("expand")
   private List<String> expand = new ArrayList<>();

   public int getOffset() {
      return offset;
   }

   public void setOffset(int offset) {
      this.offset = offset;
   }

   public int getLimit() {
      return limit;
   }

   public void setLimit(int limit) {
      this.limit = limit;
   }

   public String getSortBy() {
      return sortBy;
   }

   public void setSortBy(String sortBy) {
      this.sortBy = sortBy;
   }

   public List<String> getExpand() {
      return expand;
   }

   public void setExpand(List<String> expand) {
      this.expand = expand;
   }

   public MultivaluedMap<String, String> getMultivaluedMap() {
      MultivaluedHashMap<String, String> queries = new MultivaluedHashMap<>();
      queries.add("offset", String.valueOf(offset));
      queries.add("limit", String.valueOf(limit));
      queries.add("sort-by", sortBy);
      for (String s : expand) {
         queries.add("expand", s);
      }
      return queries;
   }
}
