package pop.rtbi.labs.resource;


import pop.rtbi.labs.*;
import pop.rtbi.labs.representation.BooksRepresentation;
import pop.rtbi.labs.representation.CustomLink;

import javax.ws.rs.*;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.*;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * Created with IntelliJ IDEA.
 * User: ycai
 * Date: 20/05/15
 * Time: 14:38
 */

@Path(OldLibraryPaths.BOOKS_PATH)
@Produces({APPLICATION_JSON, MediaType.APPLICATION_XML})
public class OldBooksResource {

   private static final String NEXT_RELATION = "next";
   private static final String PREVIOUS_RELATION = "prev";
   @Context
   UriInfo uriInfo;
   @Context
   HttpHeaders httpHeaders;
   @Context
   ResourceContext resourceContext;
   private WorkDAO workDAO = WorkDAO.INSTANCE;

   @Path("/{id}")
   public OldBookResource getBookResource() {
      return new OldBookResource();
   }

   @GET
   public Response getBooks(@DefaultValue("0") @QueryParam("offset") int offset, @DefaultValue("10") @QueryParam("limit") int limit) {
      List<Work> filteredBooks = workDAO.sortAndFilter(uriInfo.getQueryParameters().getFirst(OldLibraryPaths.SORT_BY_QUERY), uriInfo.getQueryParameters()).values().stream().collect(toList());

      int totalCount = filteredBooks.size();
      int start = Math.min(offset, totalCount);
      int end = Math.min(offset + limit, totalCount);
      List<Work> booksPage = getBooksPage(filteredBooks, start, end);
      List<CustomLink> pageLinks = buildPaginationLinks(start, limit, end, filteredBooks);

      return getBooksResponse(booksPage, pageLinks, totalCount);

   }

   private Response getBooksResponse(List<Work> booksPage, List<CustomLink> pageLinks, int totalCount) {
      ResponseBuilder responseBuilder = Response.ok();
      addResponseHeader(pageLinks, responseBuilder);
      return responseBuilder.entity(newBooksRepresentation(booksPage, pageLinks, totalCount)).build();
   }

   private BooksRepresentation newBooksRepresentation(List<Work> booksPage, List<CustomLink> pageLinks, int totalCount) {
      return new BooksRepresentation(toArray(booksPage), pageLinks, totalCount);
   }

   private void addResponseHeader(List<CustomLink> pageLinks, ResponseBuilder responseBuilder) {
      for (CustomLink link : pageLinks) {
         buildHeaderLink(responseBuilder, link.getHref(), link.getRel());
      }
   }

   private Work[] toArray(List<Work> booksPage) {
      return booksPage.toArray(new Work[booksPage.size()]);
   }

   private List<Work> getBooksPage(List<Work> allBooks, int start, int end) {
      return allBooks.subList(start, end);
   }

   private List<CustomLink> buildPaginationLinks(int start, int limit, int end, List<Work> books) {
      List<CustomLink> links = new ArrayList<>();
      if (end < books.size()) {
         URI uri = buildNextLinkUri(limit, end);
         links.add(buildBodyLink(uri, NEXT_RELATION));
      }

      if (start != 0) {
         URI uri = buildPreviousLinkUri(start, limit);
         links.add(buildBodyLink(uri, PREVIOUS_RELATION));
      }
      return links;
   }

   private URI buildNextLinkUri(int limit, int end) {
      return buildLinkUri(limit, end);
   }

   private URI buildPreviousLinkUri(int start, int limit) {
      int offset = start - limit > 0 ? start - limit : 0;
      return buildLinkUri(limit, offset);
   }

   private URI buildLinkUri(int limit, int offset) {

      UriBuilder uriBuilder = uriInfo.getBaseUriBuilder()
           .path(OldLibraryPaths.BOOKS_PATH);

      for (Map.Entry<String, List<String>> queryParam : uriInfo.getQueryParameters().entrySet()) {
         uriBuilder.queryParam(queryParam.getKey(), queryParam.getValue().toArray());
      }

      return uriBuilder
           .replaceQueryParam("offset", offset)
           .replaceQueryParam("limit", limit)
           .build();
   }

   private void buildHeaderLink(ResponseBuilder responseBuilder, String uri, String relation) {
      responseBuilder.links(Link.fromUri(uri).rel(relation).build());
   }

   private CustomLink buildBodyLink(URI uri, String relation) {
      return new CustomLink(uri.toString(), relation);
   }

   @POST
   @Consumes({APPLICATION_JSON, MediaType.APPLICATION_XML})
   public Response postBook(Book book) {
      Book temp = book;

      Work duplicatedBook = workDAO.getDuplicatedBook(temp);
      if (duplicatedBook != null) {
         return Response.status(Response.Status.CONFLICT).entity(duplicatedBook).build();
      }

      Response response = resourceContext.getResource(OldAuthorsResource.class).postAuthor((Novelist) book.getAuthor());
      if (response.getStatus() == Status.CONFLICT.getStatusCode()) {
         temp = new Book(book.getId(), book.getTitle(), (IAuthor) response.getEntity());
      }

      String id = workDAO.createBook(temp);
      return Response.created(uriInfo.getAbsolutePathBuilder().path(id).build()).entity(workDAO.readBook(id)).build();
   }

}
