package org.beigesoft.afactory;

/*
 * Copyright (c) 2016 Beigesoft ™
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
