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

import org.eclipse.jetty.webapp.WebAppClassLoader;
import org.eclipse.jetty.webapp.WebAppClassLoaderStd;

import org.beigesoft.afactory.IFactoryParam;

/**
 * <p>Factory of WebAppClassLoaderStd.</p>
 *
 * @author Yury Demidenko
 */
public class FactoryWebAppClassLoaderStd
  implements IFactoryParam<WebAppClassLoaderStd, WebAppClassLoader.Context> {

  /**
   * <p>Create a bean with abstract params.</p>
   * @param pParam parameter
   * @return M request(or) scoped bean
   */
  @Override
  public final WebAppClassLoaderStd create(
    final WebAppClassLoader.Context pContext) throws Exception {
    return new WebAppClassLoaderStd(pContext);
  }
}
