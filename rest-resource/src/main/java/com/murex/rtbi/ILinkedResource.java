package com.murex.rtbi;

import java.net.URI;

/**
 * Created with IntelliJ IDEA.
 * User: ycai
 * Date: 15/09/2015
 * Time: 15:52
 */
public interface ILinkedResource {

   Object getEntity();
   URI getHref();
}
