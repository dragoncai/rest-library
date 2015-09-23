package com.murex.rtbi;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.ws.rs.ClientErrorException;

import static javax.ws.rs.core.Response.Status;

/**
 * Created with IntelliJ IDEA.
 * User: ycai
 * Date: 30/07/2015
 * Time: 16:34
 */
public class ErrorResource {
   public static final String NOT_IDENTIFIED_ERROR = "Not identified error";
   private int errorCode;
   private String errorMessage;
   private Object errorDetails;

   public ErrorResource() {
      this(Status.INTERNAL_SERVER_ERROR, NOT_IDENTIFIED_ERROR, null);
   }

   public ErrorResource(Status errorCode, String errorMessage) {
      this(errorCode, errorMessage, null);
   }

   public ErrorResource(int errorCode, String errorMessage) {
      this(errorCode, errorMessage, null);
   }

   public ErrorResource(int errorCode, String errorMessage, Object errorDetails) {
      this.errorCode = errorCode;
      this.errorMessage = errorMessage;
      this.errorDetails = errorDetails;
   }

   public ErrorResource(Status errorCode, String errorMessage, Object errorDetails) {
      this(errorCode.getStatusCode(), errorMessage, errorDetails);
   }

   public ErrorResource(ClientErrorException exception, String errorMessage, Object errorDetails) {
      this(exception.getResponse().getStatus(), errorMessage, errorDetails);
   }

   public ErrorResource(ClientErrorException exception, Object errorDetails) {
      this(exception.getResponse().getStatus(), exception.getMessage(), errorDetails);
   }

   public ErrorResource(ClientErrorException exception) {
      this(exception, exception.getResponse().getEntity());
   }

   public int getErrorCode() {
      return errorCode;
   }

   public String getErrorMessage() {
      return errorMessage;
   }

   @JsonInclude(JsonInclude.Include.NON_NULL)
   public Object getErrorDetails() {
      return errorDetails;
   }
}
