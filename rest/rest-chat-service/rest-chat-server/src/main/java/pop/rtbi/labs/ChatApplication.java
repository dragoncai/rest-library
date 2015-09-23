package pop.rtbi.labs;

import org.glassfish.jersey.server.ResourceConfig;

/**
 * Created with IntelliJ IDEA.
 * User: ycai
 * Date: 27/05/15
 * Time: 11:34
 */
public class ChatApplication extends ResourceConfig {
	public ChatApplication() {
		packages(true, "pop.rtbi.labs");
	}
}
