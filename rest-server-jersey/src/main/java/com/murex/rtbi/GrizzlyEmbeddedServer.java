package com.murex.rtbi;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.servlet.FilterRegistration;
import org.glassfish.grizzly.servlet.ServletRegistration;
import org.glassfish.grizzly.servlet.WebappContext;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.grizzly2.servlet.GrizzlyWebContainerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.servlet.ServletProperties;

import javax.servlet.DispatcherType;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;
import java.util.EnumSet;
import java.util.Map;

public class GrizzlyEmbeddedServer {

   private HttpServer server;
   private int port;
   private String host;
   private Map<String, String> initParams;
   private WebappContext webappContext;
   private Application resourceConfig;

   private GrizzlyEmbeddedServer() {
   }

   public GrizzlyEmbeddedServer(Application application) {
      resourceConfig = application;

      webappContext = new WebappContext("grizzly web context", "");

//      FilterRegistration testFilterReg = webappContext.addFilter("TestFilter", TestFilter.class);
//      testFilterReg.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), "/*");

      ServletRegistration servletRegistration = webappContext.addServlet("Jersey", org.glassfish.jersey.servlet.ServletContainer.class);
      servletRegistration.addMapping("/*");
      servletRegistration.setInitParameter(ServletProperties.JAXRS_APPLICATION_CLASS, application.getClass().getName());
      servletRegistration.setInitParameter(ServerProperties.METAINF_SERVICES_LOOKUP_DISABLE, "true");
      servletRegistration.setInitParameter(ServerProperties.RESPONSE_SET_STATUS_OVER_SEND_ERROR, "true");

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
      URI uri = UriBuilder.fromUri(host).port(port).path("/").build();

//      addServletInitParam(ServerProperties.WADL_GENERATOR_CONFIG, "org.glassfish.jersey.examples.extendedwadl"
//           + ".SampleWadlGeneratorConfig");

      server = HttpServer.createSimpleServer(null, host, port);
//              .create(uri, ServletContainer.class, initParams);
      webappContext.deploy(server);
      server.start();
   }

   public void addServletInitParam(String wadlGeneratorConfig, String value) {
      initParams.put(wadlGeneratorConfig, value);
   }

   public void stop() {
      server.shutdown();
   }

}
