package pop.rtbi.labs;

import java.util.*;
import java.util.stream.Stream;

/**
 * Created with IntelliJ IDEA.
 * User: ycai
 * Date: 10/06/2015
 * Time: 11:31
 */
public enum WorkDAO {
   INSTANCE;

   private transient Map<String, Work> works = new LinkedHashMap<>();

   public Map<String, Work> readBooks() {
      return works;
   }

   public String createBook(Work work) {
      if (getDuplicatedBook(work) != null) {
         return null;
      }

      String id = work.getId();
      works.put(id, work);
      return id;
   }

   public Work readBook(String id) {
      return readBooks().get(id);
   }

   public void deleteBook(String id) {
      works.remove(id);
   }

   public Work updateBook(String id, Work newBook) {
      Book book = new Book(id, newBook.getTitle(), newBook.getAuthor());
      works.replace(id, book);
      return book;
   }

   public Work getDuplicatedBook(Work work) {
      Optional<Work> optional = works.values().stream().filter(book1 -> book1.hashCode() == work.hashCode()).findAny();
      if (optional.isPresent()) {
         return optional.get();
      }
      return null;
   }

   public int getTotalCount() {
      return works.size();
   }

   public Map<String, Work> sortAndFilter(String sortBy, Map<String, List<String>> filters) {
      Map<String, Work> result = works;

      if (filters != null) {
         result = filter(result, filters);
      }

      if (sortBy != null) {
         result = sort(result, sortBy);
      }
      return result;
   }

   private Map<String, Work> filter(Map<String, Work> works, Map<String, List<String>> filters) {
      for (Map.Entry<String, List<String>> query : filters.entrySet()) {
         for (String value : query.getValue()) {
            works = filter(works, query.getKey(), value);
         }
      }
      return works;
   }

   private Map<String, Work> filter(Map<String, Work> works, String query, String value) {
      if (query != null && value != null) {
         if (query.equals(Book.TITLE)) {
            return filterByTitle(works, value);
         }

         if (query.equals(Book.AUTHOR)) {
            return filterByAuthorName(works, value);
         }

         if (query.equals(Book.AUTHOR_ID)) {
            return filterByAuthorId(works, value);
         }
      }
      return works;
   }

   private Map<String, Work> filterByAuthorId(Map<String, Work> works, String value) {
      Map<String, Work> filteredMap = new LinkedHashMap<>();
      Stream<Map.Entry<String, Work>> stream = works.entrySet().stream();
      Stream<Map.Entry<String, Work>> filtered = stream.filter(entry -> entry.getValue().getAuthor().getId().equals(value));
      filtered.forEach(entry -> filteredMap.put(entry.getKey(), entry.getValue()));
      return filteredMap;
   }

   private Map<String, Work> filterByAuthorName(Map<String, Work> works, String value) {
      Map<String, Work> filteredMap = new LinkedHashMap<>();
      Stream<Map.Entry<String, Work>> stream = works.entrySet().stream();
      Stream<Map.Entry<String, Work>> filtered;
      if (value.startsWith("\"") && value.endsWith("\"")) {
         filtered = stream.filter(entry -> entry.getValue().getAuthor().getName().equalsIgnoreCase(value.replace("\"", "")));
      } else {
         filtered = stream.filter(entry -> entry.getValue().getAuthor().getName().toLowerCase().contains(value.toLowerCase()));
      }
      filtered.forEach(entry -> filteredMap.put(entry.getKey(), entry.getValue()));
      return filteredMap;
   }

   private Map<String, Work> filterByTitle(Map<String, Work> works, String value) {
      Map<String, Work> filteredMap = new LinkedHashMap<>();
      Stream<Map.Entry<String, Work>> stream = works.entrySet().stream();
      Stream<Map.Entry<String, Work>> filtered = stream.filter(entry -> entry.getValue().getTitle().toLowerCase().contains(value.toLowerCase()));
      filtered.forEach(entry -> filteredMap.put(entry.getKey(), entry.getValue()));
      return filteredMap;
   }

   private Map<String, Work> sort(Map<String, Work> works, String fieldOrder) {
      if (fieldOrder != null) {
         String[] split = fieldOrder.split("-");
         if (split.length == 2) {
            return sort(works, split[0], split[1]);
         }
      }
      return works;
   }

   private Map<String, Work> sort(Map<String, Work> works, String field, String order) {
      Comparator<Map.Entry<String, Work>> bookComparator = getComparator(field.toLowerCase(), order.toLowerCase());
      Map<String, Work> sortedMap = new LinkedHashMap<>();
      Stream<Map.Entry<String, Work>> stream = works.entrySet().stream();
      if (bookComparator != null) {
         stream.sorted(bookComparator).forEach(e -> sortedMap.put(e.getKey(), e.getValue()));
      } else {
         return works;
      }

      return sortedMap;
   }

   private Comparator<Map.Entry<String, Work>> getComparator(String field, String order) {
      if (field.equals(Book.TITLE)) {
         return getComparatorTitle(order);
      }
      if (field.equals(Book.AUTHOR)) {
         return getComparatorAuthor(order);
      }
      return null;
   }

   private Comparator<Map.Entry<String, Work>> getComparatorTitle(String order) {
      if ("asc".equals(order)) {
         return (o1, o2) -> o1.getValue().getTitle().toLowerCase().compareTo(o2.getValue().getTitle().toLowerCase());
      }
      if ("desc".equals(order)) {
         return (o1, o2) -> o2.getValue().getTitle().toLowerCase().compareTo(o1.getValue().getTitle().toLowerCase());
      }
      return null;
   }

   private Comparator<Map.Entry<String, Work>> getComparatorAuthor(String order) {
      if ("asc".equals(order)) {
         return (o1, o2) -> o1.getValue().getAuthor().getName().toLowerCase().compareTo(o2.getValue().getAuthor().getName().toLowerCase());
      }
      if ("desc".equals(order)) {
         return (o1, o2) -> o2.getValue().getAuthor().getName().toLowerCase().compareTo(o1.getValue().getAuthor().getName().toLowerCase());
      }
      return null;
   }

}
