package pop.rtbi.labs;

/**
 * Created with IntelliJ IDEA.
 * User: ycai
 * Date: 23/07/2015
 * Time: 10:38
 */
public interface Work {
    String TITLE = "title";
    String AUTHOR = "author";
    String AUTHOR_ID = "author-id";
    String ID = "id";

    IAuthor getAuthor();

   String getTitle();

   String getId();
}
