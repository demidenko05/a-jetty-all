//
//  ========================================================================
//  Copyright (c) 1995-2016 Mort Bay Consulting Pty. Ltd.
//  ------------------------------------------------------------------------
//  All rights reserved. This program and the accompanying materials
//  are made available under the terms of the Eclipse Public License v1.0
//  and Apache License v2.0 which accompanies this distribution.
//
//      The Eclipse Public License is available at
//      http://www.eclipse.org/legal/epl-v10.html
//
//      The Apache License v2.0 is available at
//      http://www.opensource.org/licenses/apache2.0.php
//
//  You may elect to redistribute this code under either of these licenses.
//  ========================================================================
//

package org.eclipse.jetty.webapp;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;

import org.beigesoft.lang.IUrlClassLoader;

/** 
 * This is same class-loader that load A-Jetty, that is either Android
 * or standard Java parent classloader. This is means that all classes A-Jetty
 * and web application must be in same jar/dex file.
 * @author Yury Demidenko
 */
public class WebAppClassLoaderEmbedded extends ClassLoader implements IUrlClassLoader
{
  /**
   * <p>Parent ClassLoader.</p>
   **/
  private final ClassLoader delegate;

  public WebAppClassLoaderEmbedded(
    WebAppClassLoader.Context context) throws IOException {
    delegate = WebAppClassLoaderEmbedded.class.getClassLoader();
  }

  /**
   * <p>Load classr.</p>
   * @param pName name
   * @return Class<?> class
   * @throws ClassNotFoundException if not found
   **/
  @Override
  public final Class<?> loadClass(
    final String pName) throws ClassNotFoundException {
    return this.delegate.loadClass(pName);
  }
  
  //Stubs:
  @Override
  public final void addUrl(final URL pUrl) {
    //nothing
  }

  @Override
  public final Enumeration<URL> getResources(
    final String name) throws IOException {
    return null;
  }

  @Override
  public final URL getResource(final String name) {
    return null;
  }

  /**
   * <p>Just stub for find class.</p>
   * @param pName name
   * @return Class<?> class
   * @throws ClassNotFoundException always
   **/
  @Override
  public final Class<?> findClass(
    final String pName) throws ClassNotFoundException {
    throw new ClassNotFoundException();
  }
}
