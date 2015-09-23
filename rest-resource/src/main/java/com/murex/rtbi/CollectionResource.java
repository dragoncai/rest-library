package com.murex.rtbi;

import com.fasterxml.jackson.annotation.JsonRootName;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlRootElement;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: ycai
 * Date: 30/07/2015
 * Time: 10:30
 */
@JsonRootName(value = "collection")
public class CollectionResource extends LinkedHashMap<String, Object> {
    public static final String OFFSET = "offset";
    public static final String LIMIT = "limit";
    public static final String TOTAL_COUNT = "totalCount";
    private UriInfo uriInfo;
   private String collectionName;
   private int totalCount;
   private int start;
   private int end;
   private int limit;

   public CollectionResource(UriInfo uriInfo, List c, String collectionName, int totalCount) {
      this(uriInfo, c, collectionName, 0, getLimit(c), totalCount);
   }

   public CollectionResource(UriInfo uriInfo, List collection, String collectionName, int offset, int limit, int totalCount) {
      this.uriInfo = uriInfo;
      this.collectionName = collectionName;
      this.totalCount = totalCount;
      this.limit = limit;

      start = Math.min(offset, collection.size());
      end = Math.min(offset + this.limit, collection.size());

      addCollectionPaginationInformation();

      addCollectionPaginated(collection);
   }

   private static int getLimit(List c) {
      return c != null ? c.size() : 0;
   }

   private void addCollectionPaginated(List collection) {
      if (!collection.isEmpty()) {
         put(collectionName, paginate(collection, start, end));
         buildPaginationLinks(start, limit, end, totalCount);
      }
   }

   private void addCollectionPaginationInformation() {
      put(OFFSET, start);
      put(LIMIT, limit);
      put(TOTAL_COUNT, totalCount);
   }

   private List paginate(List collection, int start, int end) {
       return collection.subList(start, end);
   }

   private void buildPaginationLinks(int start, int limit, int end, int totalCount) {
      if (end < totalCount) {
         URI uri = buildNextLinkUri(limit, end);
         put("next", uri.toString());
      }

      if (start != 0) {
         URI uri = buildPreviousLinkUri(start, limit);
         put("prev", uri.toString());
      }
   }

   private URI buildNextLinkUri(int limit, int end) {
      return buildLinkUri(limit, end);
   }

   private URI buildPreviousLinkUri(int start, int limit) {
      int offset = start - limit > 0 ? start - limit : 0;
      return buildLinkUri(limit, offset);
   }

   private URI buildLinkUri(int limit, int offset) {

      UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder().clone();

      for (Map.Entry<String, List<String>> queryParam : uriInfo.getQueryParameters().entrySet()) {
         uriBuilder.queryParam(queryParam.getKey(), queryParam.getValue().toArray());
      }

      return uriBuilder
           .replaceQueryParam("offset", offset)
           .replaceQueryParam("limit", limit)
           .build();
   }
}
