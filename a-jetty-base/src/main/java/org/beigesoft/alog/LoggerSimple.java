package org.beigesoft.alog;

/*
 * Beigesoft ™
 *
 * Licensed under the Apache License, Version 2.0
 *
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * <p>Simple logger to console.</p>
 *
 * @author Yury Demidenko
 */
public class LoggerSimple implements ILogger {

  /**
   * <p>Is show debug messages.</p>
   **/
  private boolean isShowDebugMessage;

  /**
   * <p>Date formatter.</p>
   **/
  private DateFormat dateFormat =
    new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

  /**
   * <p>Make debug log.</p>
   * @param pClazz of debudgged bean
   * @param pMsg message
   **/
  @Override
  public final void debug(final Class<?> pClazz,
    final String pMsg) {
    if (this.isShowDebugMessage) {
      System.out.println(dateFormat.format(new Date()) + " DEBUG "
        + pClazz.getSimpleName() + " - " + pMsg);
    }
  }

  /**
   * <p>Make info log.</p>
   * @param pClazz of bean
   * @param pMsg message
   **/
  @Override
  public final void info(final Class<?> pClazz,
    final String pMsg) {
      System.out.println(dateFormat.format(new Date()) + " INFO "
        + pClazz.getSimpleName() + " - " + pMsg);
  }

  /**
   * <p>Make error log.</p>
   * @param pClazz of bean
   * @param pMsg message
   **/
  @Override
  public final void error(final Class<?> pClazz,
    final String pMsg) {
    System.err.println(dateFormat.format(new Date()) + " ERROR "
        + pClazz.getSimpleName() + " - " + pMsg);
  }

  /**
   * <p>Make warn log.</p>
   * @param pClazz of bean
   * @param pMsg message
   **/
  @Override
  public final void warn(final Class<?> pClazz,
    final String pMsg) {
      System.out.println(dateFormat.format(new Date()) + " WARN "
        + pClazz.getSimpleName() + " - " + pMsg);
  }

  /**
   * <p>Set is show debug messages.</p>
   * @param pIsShowDebugMessage is show debug messages?
   **/
  @Override
  public final void setIsShowDebugMessages(
    final  boolean pIsShowDebugMessage) {
    this.isShowDebugMessage = pIsShowDebugMessage;
  }

  /**
   * <p>Get is show debug messages.</p>
   * @return is show debug messages?
   **/
  @Override
  public final boolean getIsShowDebugMessages() {
    return this.isShowDebugMessage;
  }
}
