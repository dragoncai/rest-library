package pop.rtbi.labs;

/**
 * Created with IntelliJ IDEA.
 * User: ycai
 * Date: 03/06/15
 * Time: 11:47
 */
public final class LibraryPaths {
   public static final String BASE_URI = "http://0.0.0.0";
   public static final int PORT = 8080;
   public static final int TESTING_PORT = 9998;
   public static final String BASE_PATH = "/";

   public static final String BOOKS_PATH = "books";
   public static final String AUTHORS_PATH = "authors";
   public static final String PUBLISHERS_PATH = "publishers";

   public static final String SORT_BY_QUERY = "sort-by";
   public static final String SORT_BY_DELIMITER = "-";
   public static final String ASC_SORT = "asc";
   public static final String DESC_SORT = "desc";

   public static final String OFFSET_QUERY = "offset";
   public static final String LIMIT_QUERY = "limit";

   private LibraryPaths() {
   }

   public static String getBookPath(String id) {
      return "books/" + id;
   }

   public static String getAuthorPath(String id) {
      return "authors/" + id;
   }

   public static String getAuthorBooksPath(String id) {
      return "authors/" + id + "/books";
   }
}
