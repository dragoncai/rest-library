package pop.rtbi.labs;

import javafx.util.Pair;
import org.junit.After;
import org.junit.Test;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA.
 * User: ycai
 * Date: 09/07/2015
 * Time: 09:54
 */
public class AuthorDAOTest {

   public static final AuthorDAO AUTHOR_DAO = AuthorDAO.INSTANCE;

   public static final IAuthor NOVELIST = new Novelist();
   public static final IAuthor NOVELIST_A = new Novelist("A");
   public static final IAuthor NOVELIST_B = new Novelist("B");
   public static final IAuthor NOVELIST_C = new Novelist("C");
   private static final IAuthor NOVELIST_AA = new Novelist("Aa");
   private static final IAuthor NOVELIST_AB = new Novelist("Ab");

   public static final String SORT_BY_DELIMITER = "-";
   public static final String SORT_BY_ASCENDING = "asc";
   public static final String SORT_BY_DESCENDING = "desc";

   @After
   public void tearDown() {
      getAuthorsFromDB().clear();
   }

   private String addAuthor(IAuthor novelist) {
      return AUTHOR_DAO.createAuthor(novelist);
   }

   private Map<String, IAuthor> getAuthorsFromDB() {
      return AUTHOR_DAO.readAuthors();
   }

   private Map<String, IAuthor> createAuthorsMap(IAuthor... novelists) {
      Map<String, IAuthor> authorMap = new LinkedHashMap<>();
      for (IAuthor novelist : novelists) {
         authorMap.put(novelist.getId(), novelist);
      }
      return authorMap;
   }

   private void addAuthors() {
      addAuthor(NOVELIST_B);
      addAuthor(NOVELIST_C);
      addAuthor(NOVELIST_A);
   }

   private List<IAuthor> getAuthorsAsList(Map<String, IAuthor> authors) {
      return authors.values().stream().collect(Collectors.toList());
   }

   private void assertEqualsMapConsideringOrder(Map<String, IAuthor> expected, Map<String, IAuthor> actual) {
      assertThat(getAuthorsAsList(actual)).isEqualTo(getAuthorsAsList(expected));
   }

   @Test
   public void authorDAOReadAuthorsShouldReturnAMapOfAuthor() {
      getAuthorsFromDB().put(NOVELIST.getId(), NOVELIST);

      Map<String, IAuthor> authorMap = new HashMap<>();
      authorMap.put(NOVELIST.getId(), NOVELIST);

      assertEquals(authorMap, getAuthorsFromDB());
   }

   @Test
   public void authorDAOCreateAuthorShouldAddToAAuthorToTheInstance() {
      addAuthor(NOVELIST);

      assertEquals(NOVELIST.getId(), getAuthorsFromDB().get(NOVELIST.getId()).getId());
      assertEquals(NOVELIST.getName(), getAuthorsFromDB().get(NOVELIST.getId()).getName());
   }

   @Test
   public void authorDAODeleteAuthorShouldRemoveTheExpectedAuthorFromTheInstance() {
      addAuthor(NOVELIST);
      AUTHOR_DAO.deleteAuthor(NOVELIST.getId());

      assertEquals(0, getAuthorsFromDB().size());
   }


   @Test
   public void authorDAOUpdateAuthorShouldModifyTheRightAuthorFromTheInstance() {
      addAuthor(NOVELIST);
      String expectedName = "newName";
      AUTHOR_DAO.updateAuthor(NOVELIST.getId(), new Novelist(expectedName));

      assertEquals(expectedName, getAuthorsFromDB().get(NOVELIST.getId()).getName());
   }

   @Test
   public void authorDAOReadAuthorShouldReturnTheAuthorByItsId() {
      addAuthor(NOVELIST);
      assertEquals(NOVELIST, AUTHOR_DAO.readAuthor(NOVELIST.getId()));
   }

   @Test
   public void authorDAOCreateAuthorShouldNotCreateADuplicateAuthorAndReturnNull() {
      addAuthor(NOVELIST);
      String id = addAuthor(NOVELIST);

      assertThat(id).isNull();
      assertThat(getAuthorsFromDB()).hasSize(1);
   }

   @Test
   public void authorDAOSortingQueryForAscendingShouldReturnASortedMap() {
      Map<String, IAuthor> expected = createAuthorsMap(NOVELIST_A, NOVELIST_B, NOVELIST_C);

      String sortBy = IAuthor.NAME + SORT_BY_DELIMITER + SORT_BY_ASCENDING;

      addAuthors();

      Map<String, IAuthor> sortedAuthors = AUTHOR_DAO.sortAndFilter(sortBy, null);

      assertEqualsMapConsideringOrder(expected, sortedAuthors);
   }

   @Test
   public void authorDAOSortingQueryForDescendingShouldReturnASortedMap() {
      Map<String, IAuthor> expected = createAuthorsMap(NOVELIST_C, NOVELIST_B, NOVELIST_A);

      String sortBy = IAuthor.NAME + SORT_BY_DELIMITER + SORT_BY_DESCENDING;

      addAuthors();

      Map<String, IAuthor> sortedAuthors = AUTHOR_DAO.sortAndFilter(sortBy, null);

      assertEqualsMapConsideringOrder(expected, sortedAuthors);
   }

   @Test
   public void authorDAOSortingShouldReturnSortedListButOriginalListShouldNotChange() {
      String sortBy = IAuthor.NAME + SORT_BY_DELIMITER + SORT_BY_DESCENDING;

      addAuthors();

      Map<String, IAuthor> before = getAuthorsFromDB();

      Map<String, IAuthor> sortedAuthors = AUTHOR_DAO.sortAndFilter(sortBy, null);

      Map<String, IAuthor> after = getAuthorsFromDB();

      assertEqualsMapConsideringOrder(before, after);
      assertThat(getAuthorsAsList(sortedAuthors)).isNotEqualTo(getAuthorsAsList(before));
   }

   @Test
   public void authorDAOSortingByWrongFieldShouldReturnOriginalMap() {
      String sortBy = "wrong_field-desc";

      addAuthors();

      Map<String, IAuthor> before = getAuthorsFromDB();
      Map<String, IAuthor> sortedAuthors = AUTHOR_DAO.sortAndFilter(sortBy, null);

      assertEqualsMapConsideringOrder(before, sortedAuthors);
   }

   @Test
   public void authorDAOSortingByWrongQueryShouldReturnOriginalMap() {
      String sortBy = "wrong";

      addAuthors();

      Map<String, IAuthor> before = getAuthorsFromDB();
      Map<String, IAuthor> sortedAuthors = AUTHOR_DAO.sortAndFilter(sortBy, null);

      assertEqualsMapConsideringOrder(before, sortedAuthors);
   }

   @Test
   public void authorDAOFilteringByExactNameShouldReturnExpectedMap() {
      Map<String, IAuthor> expected = createAuthorsMap(NOVELIST_A);
      addAuthors();

      MultivaluedMap<String, String> filter = new MultivaluedHashMap<>();
      filter.add(IAuthor.NAME, NOVELIST_A.getName());
      Map<String, IAuthor> sortedAuthors = AUTHOR_DAO.sortAndFilter(null, filter);

      assertEqualsMapConsideringOrder(expected, sortedAuthors);
   }

   @Test
   public void authorDAOFilteringWithNoMatchShouldReturnEmptyMap() {
      MultivaluedMap<String, String> filter = new MultivaluedHashMap<>();
      filter.add(IAuthor.NAME, "Z");
      addAuthors();

      assertThat(AUTHOR_DAO.sortAndFilter(null, filter)).hasSize(0);
   }

   @Test
   public void authorDAOFilteringWithNoMatchShouldNotAffectOriginal() {
      MultivaluedMap<String, String> filter = new MultivaluedHashMap<>();
      filter.add(IAuthor.NAME, "Z");
      addAuthors();

      assertThat(AUTHOR_DAO.sortAndFilter(null, filter)).hasSize(0);
      int totalCount = AUTHOR_DAO.getTotalCount();
      assertThat(totalCount).isNotEqualTo(0);
   }

   @Test
   public void authorDAOFilteringByContainedNameShouldReturnExpectedMap() {
      Map<String, IAuthor> expected = createAuthorsMap(NOVELIST_AA);

      addAuthor(NOVELIST_AA);

      MultivaluedMap<String, String> filter = new MultivaluedHashMap<>();
      filter.add(IAuthor.NAME, "a");

      assertEqualsMapConsideringOrder(expected, AUTHOR_DAO.sortAndFilter(null, filter));
   }

   @Test
   public void authorDAOFilteringWithNullValueShouldReturnWithoutFiltering() {
      addAuthors();

      Map<String, IAuthor> expected = getAuthorsFromDB();

      MultivaluedMap<String, String> filter = new MultivaluedHashMap<>();
      filter.add(IAuthor.NAME, null);

      assertEqualsMapConsideringOrder(expected, AUTHOR_DAO.sortAndFilter(null, filter));
   }

   @Test
   public void authorDAOFilteringWithWrongQueryShouldReturnWithoutFiltering() {
      addAuthors();

      Map<String, IAuthor> expected = getAuthorsFromDB();

      MultivaluedMap<String, String> filter = new MultivaluedHashMap<>();
      filter.add("wrongField", "wrongValue");

      assertEqualsMapConsideringOrder(expected, AUTHOR_DAO.sortAndFilter(null, filter));
   }

   @Test
   public void authorDAOFilteringAndSortingInOneRequestShouldReturnAFilteredAndSortedMap() {
      Map<String, IAuthor> expected = createAuthorsMap(NOVELIST_AB, NOVELIST_AA);

      String sortBy = IAuthor.NAME + SORT_BY_DELIMITER + SORT_BY_DESCENDING;
      MultivaluedMap<String, String> filter = new MultivaluedHashMap<>();
      filter.add(IAuthor.NAME, NOVELIST_A.getName());

      addAuthor(NOVELIST_AA);
      addAuthor(NOVELIST_B);
      addAuthor(NOVELIST_AB);

      Map<String, IAuthor> sortedAndFilteredAuthors = AUTHOR_DAO.sortAndFilter(sortBy, filter);

      assertEqualsMapConsideringOrder(expected, sortedAndFilteredAuthors);
   }

   @Test
   public void authorDAODoubleFilteringInOneRequestShouldReturnAFilteredMap() {
      Map<String, IAuthor> expected = createAuthorsMap(NOVELIST_AB);

      MultivaluedMap<String, String> filters = new MultivaluedHashMap<>();
      filters.add(IAuthor.NAME, NOVELIST_A.getName());
      filters.add(IAuthor.NAME, NOVELIST_B.getName());

      addAuthor(NOVELIST_AA);
      addAuthor(NOVELIST_B);
      addAuthor(NOVELIST_AB);

      Map<String, IAuthor> actual = AUTHOR_DAO.sortAndFilter(null, filters);

      assertEqualsMapConsideringOrder(expected, actual);
   }

   @Test
   public void authorDAOWithNullQueryShouldReturnOriginal() {
      addAuthors();

      Map<String, IAuthor> expected = getAuthorsFromDB();

      assertEqualsMapConsideringOrder(expected, AUTHOR_DAO.sortAndFilter(null, null));
   }


}
