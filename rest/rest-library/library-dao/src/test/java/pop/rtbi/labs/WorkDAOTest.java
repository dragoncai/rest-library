package pop.rtbi.labs;

import org.fest.assertions.Assertions;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.fest.assertions.Assertions.*;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA.
 * User: ycai
 * Date: 09/07/2015
 * Time: 10:22
 */
public class WorkDAOTest {
   public static final String NAME_A = "A";
   public static final String NAME_AAA = "Aaa";
   public static final String NAME_B = "B";
   public static final String NAME_C = "C";

   public static final IAuthor NOVELIST = new Novelist();
   public static final IAuthor NOVELIST_AAA = new Novelist(NAME_AAA);
   public static final IAuthor NOVELIST_A = new Novelist(NAME_A);
   public static final IAuthor NOVELIST_B = new Novelist(NAME_B);
   public static final IAuthor NOVELIST_C = new Novelist(NAME_C);

   public static final Work BOOK = new Book();
   public static final Work BOOK_A = new Book(NAME_A, NOVELIST);
   public static final Work BOOK_AAA = new Book(NAME_AAA, NOVELIST);
   public static final Work BOOK_A_AUTHOR_A = new Book(NAME_A, NOVELIST_A);
   public static final Work BOOK_A_AUTHOR_B = new Book(NAME_A, NOVELIST_B);
   public static final Work BOOK_A_AUTHOR_C = new Book(NAME_A, NOVELIST_C);
   public static final Work BOOK_B_AUTHOR_A = new Book(NAME_B, NOVELIST_A);
   public static final Work BOOK_B_AUTHOR_B = new Book(NAME_B, NOVELIST_B);
   public static final Work BOOK_C_AUTHOR_A = new Book(NAME_C, NOVELIST_A);

   public static final String SORT_BY = "sort-by";
   public static final String SORT_BY_DELIMITER = "-";
   public static final String SORT_BY_ASCENDING = "asc";
   public static final String SORT_BY_DESCENDING = "desc";

   private WorkDAO workDAO = WorkDAO.INSTANCE;

   private void createBook(Work... books) {
      for (Work book : books) {
         workDAO.createBook(book);
      }
   }

   private Map<String, Work> getWorks(Work... books) {
      Map<String, Work> expected = new LinkedHashMap<>();
      for (Work book : books) {
         expected.put(book.getId(), book);
      }
      return expected;
   }

   private List<Work> getAuthorsAsList(Map<String, Work> authors) {
      return authors.values().stream().collect(Collectors.toList());
   }

   private void assertEqualsMapConsideringOrder(Map<String, Work> expected, Map<String, Work> actual) {
      assertThat(getAuthorsAsList(actual)).isEqualTo(getAuthorsAsList(expected));
   }

   @After
   public void tearDown() {
      workDAO.readBooks().clear();
   }

   @Test
   public void bookDAOReadBooksShouldReturnAMapOfBook() {
      workDAO.readBooks().put(BOOK.getId(), BOOK);

      Map<String, Work> bookMap = new HashMap<>();
      bookMap.put(BOOK.getId(), BOOK);

      assertEqualsMapConsideringOrder(bookMap, workDAO.readBooks());
   }

   @Test
   public void bookDAOCreateBookShouldAddABookToTheInstance() {
      workDAO.createBook(BOOK);

      Map<String, Work> bookMap = new HashMap<>();
      bookMap.put(BOOK.getId(), BOOK);

      assertEqualsMapConsideringOrder(bookMap, workDAO.readBooks());
   }

   @Test
   public void bookDAOReadBookShouldReturnTheExpectedBookByItsId() {
      Work expected = BOOK;
      workDAO.createBook(expected);

      assertEquals(expected, workDAO.readBook(expected.getId()));
   }

   @Test
   public void bookDAODeleteBookShouldRemoveTheBookFromTheInstance() {
      Work expected = BOOK;
      workDAO.createBook(expected);
      workDAO.deleteBook(expected.getId());
      assertEquals(null, workDAO.readBook(expected.getId()));
   }

   @Test
   public void bookDAOUpdateABookShouldModifyTheBookFromTheInstance() {
      Work oldBook = BOOK;
      workDAO.createBook(oldBook);
      Work newBook = BOOK_A;
      workDAO.updateBook(oldBook.getId(), newBook);
      assertEquals(newBook.hashCode(), workDAO.readBook(oldBook.getId()).hashCode());
   }

   @Test
   public void bookDAOCreateBookShouldReturnBookId() {
      assertEquals(BOOK_A_AUTHOR_A.getId(), workDAO.createBook(BOOK_A_AUTHOR_A));
   }

   @Test
   public void bookDAOCanNotAddAlreadyExistingBookWithSameTitleSameAuthorThenShouldReturnNull() {
      workDAO.createBook(BOOK_A_AUTHOR_A);
      assertEquals(null, workDAO.createBook(BOOK_A_AUTHOR_A));
   }

   @Test
   public void bookDAOCreateTwoDifferentTitleBooksShouldCreateBothBooks() {
      workDAO.createBook(BOOK_A_AUTHOR_A);

      assertEquals(BOOK_B_AUTHOR_A.getId(), workDAO.createBook(BOOK_B_AUTHOR_A));
   }

   @Test
   public void bookDAOCreateTwoDifferentAuthorBooksShouldCreateBothBooks() {
      workDAO.createBook(BOOK_A_AUTHOR_A);
      assertEquals(BOOK_A_AUTHOR_B.getId(), workDAO.createBook(BOOK_A_AUTHOR_B));
   }

   @Test
   public void bookDAOUpdateABookShouldReturnTheNewBook() {
      workDAO.createBook(BOOK_A_AUTHOR_A);
      Work actual = workDAO.updateBook(BOOK_A_AUTHOR_A.getId(), BOOK_A_AUTHOR_B);
      assertEquals(BOOK_A_AUTHOR_B.getAuthor(), actual.getAuthor());
      assertEquals(BOOK_A_AUTHOR_A.getId(), actual.getId());
   }

   @Test
   public void bookDAOUpdateABookShouldReturnTheSameId() {
      workDAO.createBook(BOOK_A_AUTHOR_A);
      assertEquals(BOOK_A_AUTHOR_A.getId(), workDAO.updateBook(BOOK_A_AUTHOR_A.getId(), BOOK_A_AUTHOR_B).getId());
   }

   @Test
   public void bookDAOHandleQueriesWithNullValuesShouldReturnTheOriginalMap() {
      createBook(BOOK_A_AUTHOR_A, BOOK_B_AUTHOR_A, BOOK_C_AUTHOR_A);

      Map<String, Work> expected = workDAO.readBooks();

      assertEqualsMapConsideringOrder(expected, workDAO.sortAndFilter(null, null));
   }

   @Test
   public void bookDAOHandleQueriesWithFilterByTitleShouldReturnTheFilteredMap() {
      workDAO.createBook(BOOK_A);

      MultivaluedMap<String, String> query = new MultivaluedHashMap<>();
      query.add(Book.TITLE, BOOK_A.getTitle());

      assertEquals(1, workDAO.sortAndFilter(null, query).size());
   }

   @Test
   public void bookDAOHandleQueriesWithFilterByTitleWithMatchingSubStringShouldReturnTheFilteredMap() {
      workDAO.createBook(BOOK_AAA);
      MultivaluedMap<String, String> query = new MultivaluedHashMap<>();
      query.add(Book.TITLE, NAME_A);

      assertEquals(1, workDAO.sortAndFilter(null, query).size());
   }

   @Test
   public void bookDAOHandleQueriesWithFilterByTitleWithNoMatchShouldReturnTheFilteredMap() {
      workDAO.createBook(BOOK_A);

      MultivaluedMap<String, String> query = new MultivaluedHashMap<>();
      query.add(Book.TITLE, NAME_B);

      assertEquals(0, workDAO.sortAndFilter(null, query).size());
   }

   @Test
   public void bookDAOHandleQueriesWithFilterByAuthorShouldReturnTheFilteredMap() {
      workDAO.createBook(BOOK_A_AUTHOR_A);

      MultivaluedMap<String, String> query = new MultivaluedHashMap<>();
      query.add(Book.AUTHOR, NAME_A);

      assertEquals(1, workDAO.sortAndFilter(null, query).size());
   }

   @Test
   public void bookDAOHandleQueriesWithFilterByExactAuthorShouldReturnTheFilteredMap() {
      workDAO.createBook(new Book(NAME_A, NOVELIST_AAA));
      workDAO.createBook(BOOK_A_AUTHOR_A);

      MultivaluedMap<String, String> query = new MultivaluedHashMap<>();
      query.add(Book.AUTHOR, "\"" + NAME_A + "\"");

      assertEquals(1, workDAO.sortAndFilter(null, query).size());
   }

   @Test
   public void bookDAOHandleQueriesWithFilterByContainingAuthorShouldReturnTheFilteredMap() {
      workDAO.createBook(new Book(NAME_A, NOVELIST_AAA));
      workDAO.createBook(BOOK_A_AUTHOR_A);

      MultivaluedMap<String, String> query = new MultivaluedHashMap<>();
      query.add(Book.AUTHOR, NAME_A);

      assertEquals(2, workDAO.sortAndFilter(null, query).size());
   }

   @Test
   public void bookDAOHandleQueriesWithAscendingSortingByTitleShouldReturnTheSortedMap() {
      Map<String, Work> expected = getWorks(BOOK_A_AUTHOR_A, BOOK_B_AUTHOR_A, BOOK_C_AUTHOR_A);

      createBook(BOOK_B_AUTHOR_A, BOOK_C_AUTHOR_A, BOOK_A_AUTHOR_A);

      String sortBy = Book.TITLE + SORT_BY_DELIMITER + SORT_BY_ASCENDING;

      assertEqualsMapConsideringOrder(expected, workDAO.sortAndFilter(sortBy, null));
   }

   @Test
   public void bookDAOHandleQueriesWithSortingShouldReturnSortedListButOriginalListShouldNotChange() {
      createBook(BOOK_A_AUTHOR_A, BOOK_B_AUTHOR_A, BOOK_C_AUTHOR_A);

      Map<String, Work> expected = workDAO.readBooks();

      String sortBy = Book.TITLE + SORT_BY_DELIMITER + SORT_BY_ASCENDING;

      workDAO.sortAndFilter(sortBy, null);

      assertEqualsMapConsideringOrder(expected, workDAO.readBooks());
   }

   @Test
   public void bookDAOHandleQueriesWithDescendingSortingByTitleShouldReturnTheSortedMap() {
      Map<String, Work> expected = getWorks(BOOK_C_AUTHOR_A, BOOK_B_AUTHOR_A, BOOK_A_AUTHOR_A);

      createBook(BOOK_B_AUTHOR_A, BOOK_A_AUTHOR_A, BOOK_C_AUTHOR_A);

      String sortBy = Book.TITLE + SORT_BY_DELIMITER + SORT_BY_DESCENDING;

      assertEqualsMapConsideringOrder(expected, workDAO.sortAndFilter(sortBy, null));
   }

   @Test
   public void bookDAOHandleQueriesWithDescendingSortingByWrongFieldShouldReturnTheOriginalMap() {
      createBook(BOOK_A_AUTHOR_A, BOOK_B_AUTHOR_A, BOOK_C_AUTHOR_A);

      Map<String, Work> expected = workDAO.readBooks();

      String sortBy = "some_random-desc";

      assertEqualsMapConsideringOrder(expected, workDAO.sortAndFilter(sortBy, null));
   }

   @Test
   public void bookDAOHandleQueriesWithAscendingSortingByAuthorNameShouldReturnTheSortedMap() {
      Map<String, Work> expected = getWorks(BOOK_A_AUTHOR_A, BOOK_A_AUTHOR_B, BOOK_A_AUTHOR_C);

      createBook(BOOK_A_AUTHOR_B, BOOK_A_AUTHOR_C, BOOK_A_AUTHOR_A);

      String sortBy = Book.AUTHOR + SORT_BY_DELIMITER + SORT_BY_ASCENDING;

      assertEqualsMapConsideringOrder(expected, workDAO.sortAndFilter(sortBy, null));
   }

   @Test
   public void bookDAOHandleQueriesWithFilteringByAuthorIdShouldReturnTheFilteredMap() {
      Map<String, Work> expected = getWorks(BOOK_C_AUTHOR_A, BOOK_A_AUTHOR_A);

      createBook(BOOK_B_AUTHOR_B, BOOK_C_AUTHOR_A, BOOK_A_AUTHOR_A);

      MultivaluedMap<String, String> query = new MultivaluedHashMap<>();
      query.add(Book.AUTHOR_ID, NOVELIST_A.getId());

      assertEqualsMapConsideringOrder(expected, workDAO.sortAndFilter(null, query));
   }

   @Test
   public void bookDAOHandleQueriesWithFilteringByAuthorIdAndSortedByTitleShouldReturnTheFilteredAndSortedMap() {
      Map<String, Work> expected = getWorks(BOOK_A_AUTHOR_A, BOOK_C_AUTHOR_A);

      createBook(BOOK_B_AUTHOR_B, BOOK_C_AUTHOR_A, BOOK_A_AUTHOR_A);

      MultivaluedMap<String, String> query = new MultivaluedHashMap<>();
      query.add(Book.AUTHOR_ID, NOVELIST_A.getId());

      assertEqualsMapConsideringOrder(expected, workDAO.sortAndFilter("title-asc", query));
   }
}