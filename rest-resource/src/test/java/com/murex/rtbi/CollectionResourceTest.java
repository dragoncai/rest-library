package com.murex.rtbi;

import org.junit.Test;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.stub;

/**
 * Created with IntelliJ IDEA.
 * User: ycai
 * Date: 30/07/2015
 * Time: 11:44
 */
public class CollectionResourceTest {
   UriInfo uriInfo;

   protected UriInfo mockUriInfo(UriBuilder uriBuilder) {
      UriInfo uriInfo = mock(UriInfo.class);

      MultivaluedMap<String, String> multivaluedMap = new MultivaluedHashMap<>();
      stub(uriInfo.getQueryParameters()).toReturn(multivaluedMap);

      stub(uriInfo.getAbsolutePathBuilder()).toReturn(uriBuilder);
      stub(uriInfo.getBaseUriBuilder()).toReturn(getServiceUriBuilder());

      return uriInfo;
   }

   protected UriBuilder getServiceUriBuilder() {
      UriBuilder uriBuilder = UriBuilder.fromUri("");
      uriBuilder.port(9998);
      return uriBuilder;
   }

   public CollectionResourceTest() throws URISyntaxException {
      uriInfo = mockUriInfo(UriBuilder.fromUri(""));
   }

   @Test
   public void aNewCollectionResourceOfObjectsShouldReturnAMapWithObjectClassNameAsItemsKeys() {
      List<Object>  objects = new ArrayList<>();
      objects.add(new Object());
      CollectionResource collectionResource = new CollectionResource(uriInfo, objects, "object", objects.size());

      assertThat(collectionResource.get("object")).isNotNull();
   }

   @Test
   public void anEmptyNewCollectionResourceOfObjectsShouldReturnAMapWithoutItems() {
      List<Object>  objects = new ArrayList<>();
      CollectionResource collectionResource = new CollectionResource(uriInfo, objects, "object", objects.size());

      assertThat(collectionResource.get("object")).isNull();
   }

   @Test
   public void anNewCollectionResourceWithLimitEquals0ShouldReturnAnEmptyList() {
      List<Object>  objects = new ArrayList<>();
      objects.add(new Object());
      CollectionResource collectionResource = new CollectionResource(uriInfo, objects, "object", 0, 0, objects.size());

      assertThat((List) collectionResource.get("object")).hasSize(0);
   }

}