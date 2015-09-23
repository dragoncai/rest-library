package pop.rtbi.labs.model.query;

import pop.rtbi.labs.Work;

import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

/**
 * Created with IntelliJ IDEA.
 * User: ycai
 * Date: 07/08/2015
 * Time: 13:53
 */
public class WorksQueryBean extends DefaultQueryBean {
   @QueryParam(Work.AUTHOR)
   String authorName;
   @QueryParam(Work.AUTHOR_ID)
   String authorId;
   @QueryParam(Work.TITLE)
   String title;

   public String getAuthorName() {
      return authorName;
   }

   public void setAuthorName(String authorName) {
      this.authorName = authorName;
   }

   public String getAuthorId() {
      return authorId;
   }

   public void setAuthorId(String authorId) {
      this.authorId = authorId;
   }

   public String getTitle() {
      return title;
   }

   public void setTitle(String title) {
      this.title = title;
   }

   @Override
   public MultivaluedMap<String, String> getMultivaluedMap() {
      MultivaluedMap<String, String> multivaluedMap = super.getMultivaluedMap();
      multivaluedMap.add(Work.AUTHOR, authorName);
      multivaluedMap.add(Work.AUTHOR_ID, authorId);
      multivaluedMap.add(Work.TITLE, title);
      return multivaluedMap;
   }

   public MultivaluedMap<String, String> getFilters() {
      MultivaluedMap<String, String> multivaluedMap = new MultivaluedHashMap<>();
      multivaluedMap.add(Work.AUTHOR, authorName);
      multivaluedMap.add(Work.AUTHOR_ID, authorId);
      multivaluedMap.add(Work.TITLE, title);
      return multivaluedMap;
   }

}
