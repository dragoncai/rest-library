package pop.rtbi.labs;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created with IntelliJ IDEA.
 * User: ycai
 * Date: 10/08/2015
 * Time: 14:12
 */
public enum PublisherDAO {
   INSTANCE;

   private transient Map<String, Publisher> publisherMap = new LinkedHashMap<>();

   public Publisher readPublisher(String id) {
      return readPublishers().get(id);
   }

   public Map<String, Publisher> readPublishers() {
      return publisherMap;
   }

   public String createPublisher(Publisher publisher) {
      if (getDuplicatedPublisher(publisher) != null) {
         return null;
      }
      String id = publisher.getId();
      publisherMap.put(id, publisher);
      return id;
   }

   public void deletePublisher(String id) {
      publisherMap.remove(id);
   }

   public int getTotalCount() {
      return publisherMap.size();
   }

   public Publisher getDuplicatedPublisher(Publisher publisher) {
      Optional<Publisher> optional = publisherMap.values().stream().filter(publisher1 -> publisher1.hashCode() == publisher.hashCode()).findAny();
      if (optional.isPresent()) {
         return optional.get();
      }
      return null;
   }

   public void publishAWork(String id, Work work) {
      readPublisher(id).addWork(work.getAuthor(), work);
   }

   public void publishAReview(String id, Work work, Review review) {
      readPublisher(id).addReview(work, review);
   }


}
