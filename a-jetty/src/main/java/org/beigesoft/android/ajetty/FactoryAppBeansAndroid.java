package org.beigesoft.android.ajetty;

/*
 * Copyright (c) 2015-2017 Beigesoft ™
 *
 * Licensed under the GNU General Public License (GPL), Version 2.0
 * (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.en.html
 */

import android.content.Context;

import org.beigesoft.afactory.IFactoryAppBeans;

/**
 * <p>Factory app-beans for standart Java.</p>
 *
 * @author Yury Demidenko
 */
public class FactoryAppBeansAndroid implements IFactoryAppBeans {

  /**
   * <p>Factory of WebAppClassLoaderAndroid.</p>
   **/
  private FactoryWebAppClassLoaderAndroid factoryWebAppClassLoaderAndroid;

  /**
   * <p>Android context.</p>
   **/
  private final Context context;

  /**
   * <p>Setter for context.</p>
   * @param pContext reference
   **/
  public FactoryAppBeansAndroid(final Context pContext) {
    this.context = pContext;
  }


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
      return lazyGetFactoryWebAppClassLoaderAndroid();
    }
    throw new Exception("There is no bean: " + pBeanName);
  }

  /**
   * <p>Getter for factoryWebAppClassLoaderAndroid.</p>
   * @return FactoryWebAppClassLoaderAndroid
   **/
  public final FactoryWebAppClassLoaderAndroid
    lazyGetFactoryWebAppClassLoaderAndroid() {
    if (this.factoryWebAppClassLoaderAndroid == null) {
      this.factoryWebAppClassLoaderAndroid =
        new FactoryWebAppClassLoaderAndroid(this.context);
    }
    return this.factoryWebAppClassLoaderAndroid;
  }
}
