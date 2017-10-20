package org.beigesoft.lang;

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

import java.util.Enumeration;
import java.io.IOException;
import java.net.URL;

/**
 * <p>Cross platform abstraction of UrlClassLoader.</p>
 *
 * @author Yury Demidenko
 */
public interface IUrlClassLoader {

  /**
   * <p>Find class.</p>
   * @param pName class name
   * @return class if exist
   * @throws ClassNotFoundException ClassNotFoundException
   **/
  Class<?> findClass(String pName) throws ClassNotFoundException;

  /**
   * <p>Add URL.</p>
   * @param pUrl URL
   **/
  void addUrl(URL pUrl);

  /**
   * <p>Get resources.</p>
   * @param pName class name
   * @return resources
   * @throws IOException IOException
   **/
  Enumeration<URL> getResources(String pName) throws IOException;

  /**
   * <p>Get resource.</p>
   * @param pName class name
   * @return resource
   **/
  URL getResource(String pName);

  /**
   * <p>Load class.</p>
   * @param pName class name
   * @return class
   * @throws ClassNotFoundException ClassNotFoundException
   **/
  Class<?> loadClass(String pName) throws ClassNotFoundException;
}
