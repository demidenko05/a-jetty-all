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

import org.eclipse.jetty.webapp.WebAppClassLoader;

import org.beigesoft.afactory.IFactoryParam;

/**
 * <p>Factory of WebAppClassLoaderAndroid.</p>
 *
 * @author Yury Demidenko
 */
public class FactoryWebAppClassLoaderAndroid
  implements
    IFactoryParam<WebAppClassLoaderAndroid, WebAppClassLoader.Context> {

  /**
   * <p>Android context.</p>
   **/
  private final Context context;

  /**
   * <p>Setter for context.</p>
   * @param pContext reference
   **/
  public FactoryWebAppClassLoaderAndroid(final Context pContext) {
    this.context = pContext;
  }

  /**
   * <p>Create a bean with abstract params.</p>
   * @param pParam parameter
   * @return M request(or) scoped bean
   */
  @Override
  public final WebAppClassLoaderAndroid create(
    final WebAppClassLoader.Context pContext) throws Exception {
    WebAppClassLoaderAndroid result = new WebAppClassLoaderAndroid(pContext);
    result.setOptimizedDirectory(this.context.getFilesDir().getAbsolutePath());
    result.init();
    return result;
  }
}
