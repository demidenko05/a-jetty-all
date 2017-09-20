package org.beigesoft.ajetty;

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

import org.eclipse.jetty.webapp.WebAppClassLoader;
import org.eclipse.jetty.webapp.WebAppClassLoaderEmbedded;

import org.beigesoft.afactory.IFactoryParam;

/**
 * <p>Factory of WebAppClassLoaderEmbedded.</p>
 *
 * @author Yury Demidenko
 */
public class FactoryWebAppClassLoaderEmbedded implements
  IFactoryParam<WebAppClassLoaderEmbedded, WebAppClassLoader.Context> {

  /**
   * <p>Create a bean with abstract params.</p>
   * @param pParam parameter
   * @return M request(or) scoped bean
   */
  @Override
  public final WebAppClassLoaderEmbedded create(
    final WebAppClassLoader.Context pContext) throws Exception {
    return new WebAppClassLoaderEmbedded(pContext);
  }
}
