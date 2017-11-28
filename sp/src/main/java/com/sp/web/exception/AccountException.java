/*
 * Copyright 2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sp.web.exception;

/**
 * Base class of the AccountException hierarchy. Marked abstract, as its designed to be subclasses. A checked Exception,
 * as AccountExceptions are recoverable business exceptions.
 * 
 * @author pruhil
 */
@SuppressWarnings("serial")
public abstract class AccountException extends SPException {

  /**
   * Constructor for account exception
   * 
   * @param message
   *          for the account Exception
   */
  public AccountException(String message) {
    super(message);
  }

  /**
   * <code>Constructor</code> with throwable support
   * 
   * @param message
   *          exception message
   * @param cause
   *          throwable exception.
   */
  public AccountException(String message, Throwable cause) {
    super(message, cause);
  }

}
