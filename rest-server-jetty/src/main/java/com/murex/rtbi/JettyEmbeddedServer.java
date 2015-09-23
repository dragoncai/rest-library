package com.murex.rtbi;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.servlet.ServletContainer;

import javax.ws.rs.core.Application;
import java.net.InetSocketAddress;

/**
 * Created with IntelliJ IDEA.
 * User: ycai
 * Date: 10/09/2015
 * Time: 17:29
 */
public class JettyEmbeddedServer {

   private final Server server;
   private final ServletContextHandler contextHandler;
   private final ServletContainer servletContainer;
   private final ServletHolder servletHolder;

   private String urlMapping = "/*";

   public JettyEmbeddedServer(Application application, String host, int port) {
      InetSocketAddress address = InetSocketAddress.createUnresolved(host, port);
      server = new Server(address);
      contextHandler = new ServletContextHandler();
      servletContainer = new ServletContainer(ResourceConfig.forApplication(application));
      servletHolder = new ServletHolder(servletContainer);
      contextHandler.addServlet(servletHolder, urlMapping);
      server.setHandler(contextHandler);

      setContextPath("");
      initialiseInitParams();
   }

   public void setContextPath(String contextPath) {
      contextHandler.setContextPath(contextPath);
   }

   public Server getServer() {
      return server;
   }

   public ServletContextHandler getContextHandler() {
      return contextHandler;
   }

   public ServletContainer getServletContainer() {
      return servletContainer;
   }

   public ServletHolder getServletHolder() {
      return servletHolder;
   }

   public String getUrlMapping() {
      return urlMapping;
   }

   public void setUrlMapping(String urlMapping) {
      this.urlMapping = urlMapping;
   }

   private void initialiseInitParams() {
      setInitParam(ServerProperties.METAINF_SERVICES_LOOKUP_DISABLE, "true");
      setInitParam(ServerProperties.RESPONSE_SET_STATUS_OVER_SEND_ERROR, "true");
   }

   public boolean setInitParam(String paramName, String paramValue) {
      return servletHolder.getRegistration().setInitParameter(paramName, paramValue);
   }

   public void start() throws Exception {
      server.start();
   }

}
