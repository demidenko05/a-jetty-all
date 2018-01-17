package org.beigesoft.ajetty;

/*
 * Copyright (c) 2018 Beigesoft ™
 *
 * Licensed under the GNU General Public License (GPL), Version 2.0
 * (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.en.html
 */

/**
 * <p>Generic exception for A-Jetty start.</p>
 *
 * @author Yury Demidenko
 */
public class ExceptionStart extends Exception {


  /**
   * <p>Constructor default.</p>
   **/
  public ExceptionStart() {
  }

  /**
   * <p>Constructor MSG.</p>
   * @param pMsg message
   **/
  public ExceptionStart(final String pMsg) {
    super(pMsg);
  }

  /**
   * <p>Constructor CAUSE.</p>
   * @param pCause parent exception
   **/
  public ExceptionStart(final Throwable pCause) {
    super(pCause);
  }

  /**
   * <p>Constructor MSG CAUSE.</p>
   * @param pMsg message
   * @param pCause parent exception
   **/
  public ExceptionStart(final String pMsg, final Throwable pCause) {
    super(pMsg, pCause);
  }
}
