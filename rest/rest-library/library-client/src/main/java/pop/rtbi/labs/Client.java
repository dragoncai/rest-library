package pop.rtbi.labs;

import org.glassfish.jersey.client.ClientConfig;
import pop.rtbi.labs.model.post.PostPublisher;
import pop.rtbi.labs.model.post.PostReview;
import pop.rtbi.labs.model.post.PostWork;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: ycai
 * Date: 29/06/2015
 * Time: 11:19
 */
public class Client {
   private static final String J_R_R_TOLKIEN = "J.R.R. Tolkien";
   private static Book[] books = {
        new Book("The Life-Changing Magic of Tidying Up: The Japanese Art of Decluttering and Organizing", new Novelist("Marie Kondo")),
        new Book("The Return of the King", new Novelist(J_R_R_TOLKIEN)),
        new Book("All the Light We Cannot See: A Novel", new Novelist("Anthony Doerr")),
        new Book("Oh, The Places You'll Go!", new Novelist("Dr. Seuss")),
        new Book("The Girl on the Train", new Novelist("Paula Hawkins")),
        new Book("Go Set a Watchman: A Novel", new Novelist("Harper Lee")),
        new Book("The Fellowship of the Ring", new Novelist(J_R_R_TOLKIEN)),
        new Book("The Wright Brothers", new Novelist("David McCullough")),
        new Book("The Two Towers", new Novelist(J_R_R_TOLKIEN)),
        new Book("First 100 Words", new Novelist("Roger Priddy")),
        new Book("My experiments with Truth", new Novelist("Mahatma M.K.Gandhi")),
        new Book("Far from the Madding Crowd", new Novelist("Thomas Hardy")),
        new Book("Geetanjali", new Novelist("Rabindra Nath Tagore")),
        new Book("One Day in the Life of Ivan Denisovitch", new Novelist("Alexander Solzhenitsyn")),
        new Book("The Merchant of venice", new Novelist("William shakespeare")),
        new Book("The Moon and Six pense", new Novelist("Somerset Maughan")),
        new Book("Pilgrim's Progress from this world to that which is to come", new Novelist("John Bunyan")),
        new Book("A Tale of Two Cities", new Novelist("Charles Dickens")),
        new Book("Utopia", new Novelist("Sir Thomas Moor")),
        new Book("Origin of species", new Novelist("charles Darwin")),
        new Book("David Copperfield", new Novelist("Charles Dickens")),
        new Book("A passage to India", new Novelist("E.M.Forster")),
        new Book("Gulliver's Travels", new Novelist("Jonathan Swift")),
        new Book("Discovery of India", new Novelist("Pandit Jawaharlal Nehru")),
        new Book("The Vicar of Wakefield", new Novelist("Oliver Goldsmith")),
        new Book("The Decline and Fall of the Roman Empire", new Novelist("Edward Gibbon")),
        new Book("The Lady of the Last Minstrel", new Novelist("Sir Walter Scott")),
        new Book("Pride and Prejudice", new Novelist("Jane Austen")),
        new Book("Time Machine", new Novelist("H.G. Wells")),
        new Book("Arthashastra", new Novelist("Kautilya")),
        new Book("Le Contract Social", new Novelist("Jean Jacques Rousseau")),
        new Book("Avigyan Sakuntalam", new Novelist("Kalidas")),
        new Book("Anand Math", new Novelist("Bankimchandra Chattopadhyay")),
        new Book("Mein Kampf", new Novelist("Adolf Hitler")),
        new Book("Ain-i-Akbari", new Novelist("Abul Fazal")),
        new Book("Akbar-Nama", new Novelist("Abul Fazal")),
        new Book("Shakuntala", new Novelist("Kalidas")),
        new Book("War and peace", new Novelist("Leo Tolstoy")),
        new Book("A Dangerous place", new Novelist("D.P. Moynihan")),
        new Book("Raghuvamsa", new Novelist("Kalidas")),
        new Book("Adventures of Sherlock Holmes", new Novelist("Arthur Conan Doyle")),
        new Book("Adventures of Tom Saweyer", new Novelist("Mark Twain")),
        new Book("Agni Veena", new Novelist("Kazi Nasrul Islam")),
        new Book("Alice in Wonderland", new Novelist("Lewis Carrol")),
        new Book("Ancient Mariner", new Novelist("Coleridge")),
        new Book("Animal Farm", new Novelist("George Orwell")),
        new Book("Anna Karenina", new Novelist("Tolstoy")),
        new Book("Antony and Cleopatra", new Novelist("Shakespeare")),
        new Book("Arms and the Man", new Novelist("G.B.Shaw")),
        new Book("Around the World in eighty days", new Novelist("Jules Verne")),
        new Book("Baburnama", new Novelist("Babur")),
        new Book("Ben Hur", new Novelist("Lewis Wallace")),
        new Book("Bhagwat Gita", new Novelist("Ved Vyas")),
        new Book("Bisarjan", new Novelist("R.N.Tagore")),
        new Book("Canterbury Tales", new Novelist("Chaucer")),
        new Book("Chitra", new Novelist("R.N.Tagore")),
        new Book("Count of Monte Cristo", new Novelist("Alexander Dumas")),
        new Book("Crime and Punishment", new Novelist("Dostoevsky")),
        new Book("Das Kapital", new Novelist("Karl Marx")),
        new Book("Divine Comedy", new Novelist("Dante")),
        new Book("Dr.Jekyll and Mr.Hyde", new Novelist("Stevenson")),
        new Book("Don Quixote", new Novelist("Cervantes")),
        new Book("Dr.Zhivago", new Novelist("Boris Pasternak")),
        new Book("For whom the Bell Tolls", new Novelist("Ernest Hemingway")),
        new Book("Forsyte Saga", new Novelist("John Galsworthy")),
        new Book("Freedom at Midnight", new Novelist("Dominique Lapierre")),
        new Book("Gathering Storm", new Novelist("Winston Churchill")),
        new Book("Geet Govind", new Novelist("Jaya Dev")),
        new Book("Gora", new Novelist("R.N. Tagore")),
        new Book("Hamlet", new Novelist("Shakespeare")),
        new Book("Harsha Charit", new Novelist("Bana Bhatt")),
        new Book("Hunchback of Notre Dame", new Novelist("Victor Hugo")),
        new Book("Hungry Stones", new Novelist("R.N.Tagore")),
        new Book("Illiad", new Novelist("Homer")),
        new Book("Invisible Man", new Novelist("H.G.Wells")),
        new Book("Ivanhoe", new Novelist("Walter Scott")),
        new Book("Jungle Book", new Novelist("Rudyard Kipling")),
        new Book("Kadambari", new Novelist("Bana Bhatt")),
        new Book("Kidnapped", new Novelist("R.L.Stevenson")),
        new Book("King Lear", new Novelist("Shakespeare")),
        new Book("Kumar Sambhav", new Novelist("Kalidas")),
        new Book("Last Days of Pompeii", new Novelist("Bulwar Lytton")),
        new Book("Les Miserable", new Novelist("Victor Hugo")),
        new Book("Life Divine", new Novelist("Shri Aurobindo")),
        new Book("Mahabharata", new Novelist("Vyas")),
        new Book("Man and Superman", new Novelist("G.B.Shaw")),
        new Book("Meghdut", new Novelist("Kalidas")),
        new Book("Mother", new Novelist("Maxim Gorky")),
        new Book("Odyssey", new Novelist("Homer")),
        new Book("Oliver Twist", new Novelist("Charles Dickens")),
        new Book("Othello", new Novelist("Shakespeare")),
        new Book("Panchatantra", new Novelist("Vishnu Sharma")),
        new Book("Paradise lost", new Novelist("John Milton")),
        new Book("Pickwick Papers", new Novelist("Charles Dickens")),
        new Book("Plague", new Novelist("Albert Camus")),
        new Book("The Post Office", new Novelist("R.N.Tagore")),
        new Book("Principia", new Novelist("Issac Newton")),
        new Book("Ramayana", new Novelist("Valmiki")),
        new Book("Robinson Crusoe", new Novelist("Daniel Defoe")),
        new Book("Shah Nama", new Novelist("Firdausi")),
        new Book("Shape of things to come", new Novelist("H.G.Wells")),
        new Book("Three Musketeers", new Novelist("Alexander Dumas")),
        new Book("The Tempest", new Novelist("Shakespeare")),
        new Book("Tom Sawyer", new Novelist("Mark Twain")),
        new Book("Treasure Island", new Novelist("R.L.Stevenson")),
        new Book("Ulysses", new Novelist("James Joyce")),
        new Book("Uncle Tom's Cabin", new Novelist("Mrs.Harriet Stowe")),
        new Book("Waste Land", new Novelist("T.S.Eliot")),
        new Book("Nineteen Eighty-four", new Novelist("George Orwell")),
        new Book("Sunny Days", new Novelist("Sunil Gavaskar")),
        new Book("Faust", new Novelist("Goethe")),
        new Book("Arabian Nights", new Novelist("Sir Richard Burton")),
        new Book("The City of Joy", new Novelist("Dominique Lapierre")),
        new Book("The One Day Wonders", new Novelist("Sunil Gavaskar")),
        new Book("Silas Marner", new Novelist("George Eliot")),
        new Book("Bachelor of Arts", new Novelist("R.K.Narayan")),
        new Book("China Passage", new Novelist("John Kenneth Galbraith")),
        new Book("A Suitable Boy", new Novelist("Vikram Seth")),
        new Book("A Voice For Freedom", new Novelist("Nayantara Saigal")),
        new Book("A Week with Gandhi", new Novelist("Louis Fisher")),
        new Book("A Woman's Life", new Novelist("Guy de Maupassaut")),
        new Book("Age of Reason", new Novelist("Jean Paul Sartre")),
        new Book("Asian Drama", new Novelist("Gunnar Myrdal")),
        new Book("The Bubble", new Novelist("Mulk Raj Anand")),
        new Book("Ben Hur", new Novelist("Lewis Wallace")),
        new Book("The Castle", new Novelist("Franz Kalka")),
        new Book("Chandalika", new Novelist("Rabindra Nath Tagore")),
        new Book("The Class", new Novelist("Erich Byron")),
        new Book("The Clown", new Novelist("Heinrich Boll")),
        new Book("Comedy of Errors", new Novelist("William Shakespeare")),
        new Book("Communist Manifesto", new Novelist("Karl Marx")),
        new Book("Confessions", new Novelist("Jean Jacques Rousseau")),
        new Book("The Court Dancer", new Novelist("Rabindra Nath Tagore")),
        new Book("Death of a City", new Novelist("Amrita Pritam")),
        new Book("Decline and Fall of the Roman", new Novelist("Edward Gibbon Empire")),
        new Book("Essays of Gita", new Novelist("Sir Aurobindo Ghosh")),
        new Book("French Revolution", new Novelist("Thomas Carlyle")),
        new Book("Ganadevata", new Novelist("Tara Shankar Bandopadhyaya")),
        new Book("Glimpses of World History", new Novelist("Jawaharlal Nehru")),
        new Book("The Godfather", new Novelist("Mario Puzo")),
        new Book("Grammar of Politics", new Novelist("Harold T.Laski")),
        new Book("Guide", new Novelist("R.K.Narayan")),
        new Book("Hindu View of Life", new Novelist("Dr.S.Radha Krishnan")),
        new Book("Hungry Stones", new Novelist("Rabindra Nath Tagore")),
        new Book("India Divided", new Novelist("Dr.Rajendra Prasad")),
        new Book("Jurassik Park", new Novelist("Michael Crichton")),
        new Book("Kidnapped", new Novelist("Robert Louis Stevenson")),
        new Book("Richard Nixon", new Novelist("Leaders")),
        new Book("Mahatma Gandhi", new Novelist("Romain Rolland")),
        new Book("The Masters", new Novelist("C.P.Shaw")),
        new Book("My Truth", new Novelist("Indira Gandhi")),
        new Book("Old Man and the Sea", new Novelist("Earnest Hemingway")),
        new Book("The Other Side of Midnight", new Novelist("Sindye Sheldon")),
        new Book("Pride and Prejudice", new Novelist("Jane Austen")),
        new Book("Shape of Things to Come", new Novelist("H.G.Wells")),
        new Book("Sons and Lovers", new Novelist("D.H.Lawrence")),
        new Book("Treasure Island", new Novelist("R.L.Stevenson")),
        new Book("Valley of Dolls", new Novelist("Jacqueline Susann")),
        new Book("Wealth of Nations", new Novelist("Adam Smith"))
   };

   private Client() {
   }

   public static void main(String[] args) throws IOException {
      //TODO see WADL file
      ClientConfig clientConfig = new ClientConfig();
      clientConfig.register(JsonProvider.class);
      clientConfig.register(XmlProvider.class);
      for (Book b : books) {
         javax.ws.rs.client.Client client = ClientBuilder.newClient(clientConfig);
         client.target("http://0.0.0.0:8080/").path("books").request().post(Entity.entity(b, MediaType.APPLICATION_XML));
      }
      javax.ws.rs.client.Client client = ClientBuilder.newClient(clientConfig);
      PostPublisher pocket = new PostPublisher("Pocket");
      String location = client.target("http://0.0.0.0:8080/").path("publishers").request().post(Entity.entity(pocket, MediaType.APPLICATION_XML)).getHeaderString(HttpHeaders.LOCATION);
      client.target(location).path("works").request().post(Entity.entity(new PostWork(books[0].getId()), MediaType.APPLICATION_XML));
      client.target(location).path("works").request().post(Entity.entity(new PostWork(books[1].getId()), MediaType.APPLICATION_XML));
      client.target(location).path("reviews").request().post(Entity.entity(new PostReview(books[1].getId(), new Review(5, "GREAT")), MediaType.APPLICATION_XML));
      client.target(location).path("reviews").request().post(Entity.entity(new PostReview(books[1].getId(), new Review(1, "BAD")), MediaType.APPLICATION_XML));
      client.target(location).path("reviews").request().post(Entity.entity(new PostReview(books[1].getId(), new Review(5, "PERFECT")), MediaType.APPLICATION_XML));

   }

}
