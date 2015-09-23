package pop.rtbi.labs;

import javax.ws.rs.client.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutionException;

/**
 * Created with IntelliJ IDEA.
 * User: ycai
 * Date: 27/05/15
 * Time: 11:25
 */
public class Client {
	public static void main(String[] args) throws ExecutionException, InterruptedException, IOException {
		String name = args[0];

		javax.ws.rs.client.Client client = ClientBuilder.newClient();
		final WebTarget target = client.target("http://localhost:9999/chat");
		target.request().async().get(new InvocationCallback<String>() {
			@Override
			public void completed(String s) {
				System.out.println("> "+ s);
				target.request().async().get(this);
			}

			@Override
			public void failed(Throwable throwable) {
			}
		});

		while (true){
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			String message = br.readLine();
			target.request().post(Entity.text(name + ": " + message));
		}
	}
}
