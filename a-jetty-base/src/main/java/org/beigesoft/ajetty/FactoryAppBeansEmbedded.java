/*
BSD 2-Clause License

Copyright (c) 2019, Beigesoft™
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

* Redistributions of source code must retain the above copyright notice, this
  list of conditions and the following disclaimer.

* Redistributions in binary form must reproduce the above copyright notice,
  this list of conditions and the following disclaimer in the documentation
  and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.beigesoft.ajetty;

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
