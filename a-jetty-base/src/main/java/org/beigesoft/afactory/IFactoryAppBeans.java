package org.beigesoft.afactory;

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
 * <p>Abstraction of application beans factory.
 * It is usefull when it is not used for any reason
 * an IOC XML configurable framework.
 * </p>
 *
 * @author Yury Demidenko
 */
public interface IFactoryAppBeans {

  /**
   * <p>Get bean in lazy mode (if bean is null then initialize it).</p>
   * @param pBeanName - bean name
   * @return Object - requested bean
   * @throws Exception - an exception
   */
  Object lazyGet(String pBeanName) throws Exception;
}
