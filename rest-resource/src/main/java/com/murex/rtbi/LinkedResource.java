package com.murex.rtbi;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.LinkedHashMap;

/**
 * Created with IntelliJ IDEA.
 * User: ycai
 * Date: 29/07/2015
 * Time: 17:05
 */
public class LinkedResource extends LinkedHashMap<String, Object> implements ILinkedResource{
   public static final String HREF = "href";
   public static final String ENTITY = "entity";

   public LinkedResource(URI href) {
      put(HREF, href);
   }

   public LinkedResource(URI href, Object entity) {
      this(href);
      put(ENTITY, entity);
   }

   public LinkedResource(UriBuilder href) {
      put(HREF, href.build());
   }

   public LinkedResource(UriBuilder href, Object entity) {
      this(href.build());
      put(ENTITY, entity);
   }

   public LinkedResource() {
   }

   @Override
   public Object getEntity() {
      return get(ENTITY);
   }

   public <T> T getEntity(Class<T> aClass) {
      return (T) getEntity();
   }

   @Override
   public URI getHref() {
      return (URI) get(HREF);
   }
}
