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
public class FactoryAppBeansEmbedded implements IFactoryAppBeans {

  /**
   * <p>Factory WebAppClassLoaderEmbedded.</p>
   **/
  private FactoryWebAppClassLoaderEmbedded factoryWebAppClassLoaderEmbedded;

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
      return lazyGetFactoryWebAppClassLoaderEmbedded();
    }
    throw new Exception("There is no bean: " + pBeanName);
  }

  /**
   * <p>Getter for factoryWebAppClassLoaderEmbedded.</p>
   * @return FactoryWebAppClassLoaderEmbedded
   **/
  public final FactoryWebAppClassLoaderEmbedded
    lazyGetFactoryWebAppClassLoaderEmbedded() {
    if (this.factoryWebAppClassLoaderEmbedded == null) {
      this.factoryWebAppClassLoaderEmbedded =
        new FactoryWebAppClassLoaderEmbedded();
    }
    return this.factoryWebAppClassLoaderEmbedded;
  }
}
