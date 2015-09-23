package pop.rtbi.labs.representation;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

/**
 * Dynamically appends self linking methods to resources that have id as an instance variable
 * For example: If Novelist is a class with a field id.
 * A method getLink() will be dynamically generated into it that returns a CustomLink object
 * which in turn has a link to /author/id with a self tag description
 * In XML, this will look something like <link href="/authors/4b13fb5503454b5b89a316fa72fd7040" rel="self"/>
 * Similarly in JSON, "link":{"href":"/authors/8b50ef1876734116b6cc69a264913763","rel":"self"}
 * Created by mkobeissi on 21/07/2015.
 */
public final class ResourcesSelfLinker {

   private static final Logger LOGGER = LoggerFactory.getLogger(ResourcesSelfLinker.class);

   private ResourcesSelfLinker() {

   }

   public static void injectSelfLinking() {
      try {
         javaAssistResourceSelfLinking(ResourcesSelfLinker::getSelfLinkingResources);
      } catch (Exception e) {
         LOGGER.error("Failed to self link resources", e);
      }
   }

   private static Map<String, String> getSelfLinkingResources() {
      Map<String, String> resourceClassPaths = new HashMap<>();
      resourceClassPaths.put("pop.rtbi.labs.Novelist", "authors");
      resourceClassPaths.put("pop.rtbi.labs.Book", "books");
      resourceClassPaths.put("pop.rtbi.labs.Journalist", "authors");
      return resourceClassPaths;
   }

   private static void javaAssistResourceSelfLinking(Supplier<Map<String, String>> supplier) throws CannotCompileException, NotFoundException {
      Map<String, String> resourceClassPaths = supplier.get();
      ClassPool pool = ClassPool.getDefault();

      for (Map.Entry<String, String> entry : resourceClassPaths.entrySet()) {
         CtClass ctClass = pool.get(entry.getKey());
         addSelfLink(ctClass, entry.getValue());
         ctClass.toClass();
      }
   }

   private static void addSelfLink(CtClass resourceClass, String resourcePath) throws CannotCompileException {
      resourceClass.addMethod(CtMethod.make(buildSelfLinkMethod(resourcePath), resourceClass));
   }

   private static String buildSelfLinkMethod(final String resourcePath) {
      String resourcePathId = "\"/" + resourcePath + "/\" + id";
      String selfLinkTag = "\"self\"";
      return "public pop.rtbi.labs.representation.CustomLink getLink() { return new pop.rtbi.labs.representation.CustomLink(" + resourcePathId + ", " + selfLinkTag + ") ;}";
   }
}
