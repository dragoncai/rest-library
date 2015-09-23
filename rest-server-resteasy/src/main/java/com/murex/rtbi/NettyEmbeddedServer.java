package com.murex.rtbi;

import org.jboss.resteasy.plugins.server.netty.NettyJaxrsServer;
import org.jboss.resteasy.spi.ResteasyDeployment;

import javax.ws.rs.core.Application;
import java.io.IOException;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: ycai
 * Date: 23/06/2015
 * Time: 12:28
 */
public class NettyEmbeddedServer {
   private Application application;
   ResteasyDeployment deployment;
   private NettyJaxrsServer server;
   private int port;
   private String host;

   private class RESTEasyApplication extends Application {

      private Set<Class<?>> set;

      public RESTEasyApplication(Application application) {
         set = application.getClasses();
      }

      @Override
      public Set<Class<?>> getClasses() {
         return set;
      }

      public void register(Class<?> e) {
         set.add(e);
      }
   }

   private NettyEmbeddedServer() {
   }

   public NettyEmbeddedServer(Application application) {
      server = new NettyJaxrsServer();
      this.application = getApplication(application);
      deployment = new ResteasyDeployment();
      deployment.setApplication(this.application);
   }

   private Application getApplication(final Application application) {
      return new RESTEasyApplication(application);
   }

   public int getPort() {
      return port;
   }

   public void setPort(int port) {
      this.port = port;
   }

   public String getHost() {
      return host;
   }

   public void setHost(String host) {
      this.host = host;
   }

   public void start() throws IOException {
      server.setDeployment(deployment);
      server.setPort(port);
      server.start();
   }

   public void stop() {
      server.stop();
   }
}
