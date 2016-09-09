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

/**
 * <p>Abstraction of nonstatic (memory-friendly) Logger,
 * that must work also on Android debug logging (sls4j can't).</p>
 *
 * @author Yury Demidenko
 */
public interface ILogger {

  /**
   * <p>Make debug log.</p>
   * @param pClazz of debudgged bean
   * @param pMsg message
   **/
  void debug(Class<?> pClazz, String pMsg);

  /**
   * <p>Make info log.</p>
   * @param pClazz of bean
   * @param pMsg message
   **/
  void info(Class<?> pClazz, String pMsg);

  /**
   * <p>Make error log.</p>
   * @param pClazz of bean
   * @param pMsg message
   **/
  void error(Class<?> pClazz, String pMsg);

  /**
   * <p>Make warn log.</p>
   * @param pClazz of bean
   * @param pMsg message
   **/
  void warn(Class<?> pClazz, String pMsg);

  /**
   * <p>Set is show debug messages.</p>
   * @param pIsShowDebugMessage is show debug messages?
   **/
  void setIsShowDebugMessages(boolean pIsShowDebugMessage);

  /**
   * <p>Get is show debug messages.</p>
   * @return is show debug messages?
   **/
  boolean getIsShowDebugMessages();
}
