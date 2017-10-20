package org.beigesoft.android.ajetty;

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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

import dalvik.system.DexClassLoader;

import org.eclipse.jetty.webapp.WebAppClassLoader;
import org.eclipse.jetty.webapp.WebAppContext;

import org.beigesoft.lang.IUrlClassLoader;

/**
 * <p>WebAppClassLoader for Android.</p>
 *
 * @author Yury Demidenko
 */
public class WebAppClassLoaderAndroid implements IUrlClassLoader {

  /**
   * <p>DexClassLoader.</p>
   **/
  private DexClassLoader delegate = null;

  /**
   * <p>WebApp Context.</p>
   **/
  private WebAppContext context;

  /**
   * <p>dexPath for DexClassLoader.</p>
   **/
  private String dexPath = "";

  /**
   * <p>optimizedDirectory for DexClassLoader.</p>
   **/
  private String optimizedDirectory;

  /**
   * <p>Only constructor.</p>
   * @param pContext WebAppClassLoader.Context
   * @throws Exception an Exception
   **/
  public WebAppClassLoaderAndroid(
    final WebAppClassLoader.Context pContext) throws Exception {
    context = (WebAppContext) pContext;
  }

  /**
   * <p>Initialize DexClassLoader.</p>
   * @throws Exception an Exception
   **/
  public final void init() throws Exception {
    String libPath = context.getWar() + File.separator + "WEB-INF/lib/";
    File dirLib = new File(libPath);
    addJars(dirLib);
    String classesPath = context.getWar() + File.separator + "WEB-INF/classes/";
    File dirClasses = new File(classesPath);
    addJars(dirClasses);
    this.delegate = new DexClassLoader(this.dexPath, this.optimizedDirectory,
      null, WebAppClassLoaderAndroid.class.getClassLoader());
  }

  /**
   * <p>Add jars into dexPath for DexClassLoader.</p>
   * @param pLib directory
   * @throws Exception an Exception
   **/
  public final void addJars(final File pLib) throws Exception {
    if (pLib.exists() && pLib.isDirectory()) {
      File[] files = pLib.listFiles();
      for (int i = 0; files != null && i < files.length; i++) {
        String fname = files[i].getName().toLowerCase();
        if (!files[i].isDirectory() && fname.endsWith(".jar")) {
          String jar = files[i].getAbsolutePath();
          if ("".equals(this.dexPath)) {
            this.dexPath = jar;
          } else {
            this.dexPath += File.pathSeparator + jar;
          }
        }
      }
    }
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

  /**
   * <p>Just stub for add URL.</p>
   * @param pUrl URL
   **/
  @Override
  public final void addUrl(final URL pUrl) {
    // nothing
  }

  /**
   * <p>Just stub for get resources.</p>
   * @param pName name
   * @return Enumeration<URL> URL set
   * @throws IOException always
   **/
  @Override
  public final Enumeration<URL> getResources(
    final String pName) throws IOException {
    throw new IOException();
  }

  /**
   * <p>Just stub for get resource.</p>
   * @param pName name
   * @return URL URL
   **/
  @Override
  public final URL getResource(final String pName) {
    return null;
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

  //Simple getters and setters:
  /**
   * <p>Getter for delegate.</p>
   * @return DexClassLoader
   **/
  public final DexClassLoader getDelegate() {
    return this.delegate;
  }

  /**
   * <p>Setter for delegate.</p>
   * @param pDelegate reference
   **/
  public final void setDelegate(final DexClassLoader pDelegate) {
    this.delegate = pDelegate;
  }

  /**
   * <p>Getter for dexPath.</p>
   * @return String
   **/
  public final String getDexPath() {
    return this.dexPath;
  }

  /**
   * <p>Setter for dexPath.</p>
   * @param pDexPath reference
   **/
  public final void setDexPath(final String pDexPath) {
    this.dexPath = pDexPath;
  }

  /**
   * <p>Getter for optimizedDirectory.</p>
   * @return String
   **/
  public final String getOptimizedDirectory() {
    return this.optimizedDirectory;
  }

  /**
   * <p>Setter for optimizedDirectory.</p>
   * @param pOptimizedDirectory reference
   **/
  public final void setOptimizedDirectory(final String pOptimizedDirectory) {
    this.optimizedDirectory = pOptimizedDirectory;
  }
}
