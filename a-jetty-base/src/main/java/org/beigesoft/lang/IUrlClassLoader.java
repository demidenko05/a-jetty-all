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

package org.beigesoft.lang;

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
