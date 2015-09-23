package pop.rtbi.labs;

import java.util.*;
import java.util.stream.Stream;

/**
 * Created with IntelliJ IDEA.
 * User: ycai
 * Date: 10/06/2015
 * Time: 11:31
 */
public enum AuthorDAO {
   INSTANCE;

   private transient Map<String, IAuthor> authors = new LinkedHashMap<>();

   public IAuthor readAuthor(String id) {
      return readAuthors().get(id);
   }

   public Map<String, IAuthor> readAuthors() {
      return authors;
   }

   public String createAuthor(IAuthor author) {
      if (getDuplicatedAuthor(author) != null) {
         return null;
      }

      String id = author.getId();
      authors.put(id, author);
      return id;
   }

   public void deleteAuthor(String id) {
      authors.remove(id);
   }

   public IAuthor updateAuthor(String id, IAuthor author) {
      IAuthor value = author;
      if (author instanceof Novelist) {
         value = new Novelist(id, author.getName());
      } else if (author instanceof Journalist) {
         value = new Journalist(id, author.getName());
      }
      authors.replace(id, value);
      return value;
   }

   public int getTotalCount() {
      return authors.size();
   }

   public IAuthor getDuplicatedAuthor(IAuthor author) {
      Optional<IAuthor> optional = authors.values().stream().filter(author1 -> author1.hashCode() == author.hashCode()).findAny();
      if (optional.isPresent()) {
         return optional.get();
      }
      return null;
   }

   public Map<String, IAuthor> sortAndFilter(String sortBy, Map<String, List<String>> filters) {
      Map<String, IAuthor> result = authors;
      if (filters != null) {
         result = filter(filters, result);
      }
      if (sortBy != null) {
         result = sort(result, sortBy);
      }
      return result;
   }

   private Map<String, IAuthor> filter(Map<String, List<String>> filters, Map<String, IAuthor> result) {
      for (Map.Entry<String, List<String>> query : filters.entrySet()) {
         for (String value : query.getValue()) {
            result = filter(result, query.getKey(), value);
         }
      }
      return result;
   }

   private Map<String, IAuthor> filter(Map<String, IAuthor> authors, String query, String value) {
      if (value == null || !query.equalsIgnoreCase(Novelist.NAME)) {
         return authors;
      }

      Map<String, IAuthor> filteredMap = new LinkedHashMap<>();
      authors.entrySet().stream().filter(a -> a.getValue().getName().toLowerCase().contains(value.toLowerCase())).forEach(entry -> filteredMap.put(entry.getKey(), entry.getValue()));

      return filteredMap;
   }

   private Map<String, IAuthor> sort(Map<String, IAuthor> authors, String fieldOrder) {
      if (fieldOrder != null) {
         String[] split = fieldOrder.split("-");
         if (split.length == 2) {
            return sort(authors, split[0], split[1]);
         }
      }
      return authors;
   }

   private Map<String, IAuthor> sort(Map<String, IAuthor> authors, String field, String order) {
      Comparator<Map.Entry<String, IAuthor>> authorComparator = getComparator(field, order);
      Map<String, IAuthor> sortedMap = new LinkedHashMap<>();
      Stream<Map.Entry<String, IAuthor>> stream = authors.entrySet().stream();
      if (field != null && order != null && authorComparator != null) {
         stream.sorted(authorComparator).forEach(e -> sortedMap.put(e.getKey(), e.getValue()));
      } else {
         return authors;
      }

      return sortedMap;
   }

   private Comparator<Map.Entry<String, IAuthor>> getComparator(String field, String order) {
      if (field.equalsIgnoreCase(Novelist.NAME)) {
         if ("asc".equalsIgnoreCase(order)) {
            return (o1, o2) -> o1.getValue().getName().toLowerCase().compareTo(o2.getValue().getName().toLowerCase());
         }
         if ("desc".equalsIgnoreCase(order)) {
            return (o1, o2) -> o2.getValue().getName().toLowerCase().compareTo(o1.getValue().getName().toLowerCase());
         }
      }
      return null;
   }
}