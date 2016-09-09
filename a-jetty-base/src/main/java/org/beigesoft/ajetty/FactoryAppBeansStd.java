package org.beigesoft.ajetty;

/*
 * Beigesoft ™
 *
 * Licensed under the Apache License, Version 2.0
 *
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */

import org.beigesoft.afactory.IFactoryAppBeans;

/**
 * <p>Factory app-beans for standart Java.</p>
 *
 * @author Yury Demidenko
 */
public class FactoryAppBeansStd implements IFactoryAppBeans {

  /**
   * <p>Factory WebAppClassLoaderStd.</p>
   **/
  private FactoryWebAppClassLoaderStd factoryWebAppClassLoaderStd;

  /**
   * <p>Get bean in lazy mode (if bean is null then initialize it).</p>
   * @param pBeanName - bean name
   * @return Object - requested bean
   * @throws Exception - an exception
   */
  @Override
  public final synchronized Object lazyGet(
    final String pBeanName) throws Exception {
    if ("IFactoryParam<IUrlClassLoader, WebAppClassLoader.Context>"
      .equals(pBeanName)) {
      return lazyGetFactoryWebAppClassLoaderStd();
    }
    throw new Exception("There is no bean: " + pBeanName);
  }

  /**
   * <p>Getter for factoryWebAppClassLoaderStd.</p>
   * @return FactoryWebAppClassLoaderStd
   **/
  public final FactoryWebAppClassLoaderStd
    lazyGetFactoryWebAppClassLoaderStd() {
    if (this.factoryWebAppClassLoaderStd == null) {
      this.factoryWebAppClassLoaderStd = new FactoryWebAppClassLoaderStd();
    }
    return this.factoryWebAppClassLoaderStd;
  }
}
