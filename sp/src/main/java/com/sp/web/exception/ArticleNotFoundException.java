package com.sp.web.exception;

/**
 * @author Dax Abraham
 *
 *         The exception when article is not found.
 */
public class ArticleNotFoundException extends SPException {

  private static final long serialVersionUID = 8821149438811220061L;

  public ArticleNotFoundException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  public ArticleNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }

  public ArticleNotFoundException(String message) {
    super(message);
  }

  public ArticleNotFoundException(Throwable cause) {
    super(cause);
  }
}
