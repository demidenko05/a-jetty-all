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
import java.io.InputStream;
import java.net.URL;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.jetty.util.IO;
import org.eclipse.jetty.util.StringUtil;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.resource.ResourceCollection;

import org.beigesoft.lang.IUrlClassLoader;
import org.beigesoft.afactory.IFactoryAppBeans;
import org.beigesoft.afactory.IFactoryParam;


/* ------------------------------------------------------------ */
/** ClassLoader for HttpContext.
 * Specializes URLClassLoader with some utility and file mapping
 * methods.
 *
 * This loader defaults to the 2.3 servlet spec behavior where non
 * system classes are loaded from the classpath in preference to the
 * parent loader.  Java2 compliant loading, where the parent loader
 * always has priority, can be selected with the 
 * {@link org.eclipse.jetty.webapp.WebAppContext#setParentLoaderPriority(boolean)} 
 * method and influenced with {@link WebAppContext#isServerClass(String)} and 
 * {@link WebAppContext#isSystemClass(String)}.
 *
 * If no parent class loader is provided, then the current thread 
 * context classloader will be used.  If that is null then the 
 * classloader that loaded this class is used as the parent.
 * 
 */
public class WebAppClassLoader extends ClassLoader
{
  /**
   * <p>Cross platform class loader (Standard/Android)</p>
   **/
  private final IUrlClassLoader delegate;
  
  private final IFactoryAppBeans factoryAppBeans;
  
  private static final Logger LOG = Log.getLogger(WebAppClassLoader.class);

  private final Context _context;
  private final Set<String> _extensions=new HashSet<String>();
  private String _name=String.valueOf(hashCode());
    
  /* ------------------------------------------------------------ */
  /** The Context in which the classloader operates.
   */
  public interface Context
  {
    /* ------------------------------------------------------------ */
    /** Convert a URL or path to a Resource.
     * The default implementation
     * is a wrapper for {@link Resource#newResource(String)}.
     * @param urlOrPath The URL or path to convert
     * @return The Resource for the URL/path
     * @throws IOException The Resource could not be created.
     */
    Resource newResource(String urlOrPath) throws IOException;

    /* ------------------------------------------------------------ */
    /**
     * @return Returns the permissions.
     */
    PermissionCollection getPermissions();

    /* ------------------------------------------------------------ */
    /** Is the class a System Class.
     * A System class is a class that is visible to a webapplication,
     * but that cannot be overridden by the contents of WEB-INF/lib or
     * WEB-INF/classes 
     * @param clazz The fully qualified name of the class.
     * @return True if the class is a system class.
     */
    boolean isSystemClass(String clazz);

    /* ------------------------------------------------------------ */
    /** Is the class a Server Class.
     * A Server class is a class that is part of the implementation of 
     * the server and is NIT visible to a webapplication. The web
     * application may provide it's own implementation of the class,
     * to be loaded from WEB-INF/lib or WEB-INF/classes 
     * @param clazz The fully qualified name of the class.
     * @return True if the class is a server class.
     */
    boolean isServerClass(String clazz);

    /* ------------------------------------------------------------ */
    /**
     * @return True if the classloader should delegate first to the parent 
     * classloader (standard java behaviour) or false if the classloader 
     * should first try to load from WEB-INF/lib or WEB-INF/classes (servlet 
     * spec recommendation).
     */
    boolean isParentLoaderPriority();
    
    /* ------------------------------------------------------------ */
    String getExtraClasspath();
  }

  /** Constructor.
   */
  public WebAppClassLoader(Context context, IFactoryAppBeans pFactoryAppBeans)
      throws Exception
  {
    this(null, context, pFactoryAppBeans);
  }

  /** Constructor.
   */
  public WebAppClassLoader(ClassLoader parent, Context context, IFactoryAppBeans pFactoryAppBeans)
      throws Exception
  {
    this.factoryAppBeans = pFactoryAppBeans;
    IFactoryParam<IUrlClassLoader, WebAppClassLoader.Context> factoryUrlClassLoader =
      (IFactoryParam<IUrlClassLoader, WebAppClassLoader.Context>) this.factoryAppBeans
        .lazyGet("IFactoryParam<IUrlClassLoader, WebAppClassLoader.Context>");
    this.delegate = factoryUrlClassLoader.create(context);
    _context=context;
    _extensions.add(".jar");
    if (context.getExtraClasspath() != null) {
      addClassPath(context.getExtraClasspath());
    }
  }
  
  /* ------------------------------------------------------------ */
  /**
   * @return the name of the classloader
   */
  public String getName()
  {
      return _name;
  }

  /* ------------------------------------------------------------ */
  /**
   * @param name the name of the classloader
   */
  public void setName(String name)
  {
      _name=name;
  }

  /* ------------------------------------------------------------ */
  public Context getContext() {
      return _context;
  }

  /* ------------------------------------------------------------ */
  /**
   * @param resource Comma or semicolon separated path of filenames or URLs
   * pointing to directories or jar files. Directories should end
   * with '/'.
   */
  public void addClassPath(Resource resource)
      throws IOException {
    if (resource instanceof ResourceCollection) {
      for (Resource r : ((ResourceCollection)resource).getResources()) {
        addClassPath(r);
      }
    } else {
      addClassPath(resource.toString());
    }
  }
    
  /* ------------------------------------------------------------ */
  /**
   * @param classPath Comma or semicolon separated path of filenames or URLs
   * pointing to directories or jar files. Directories should end
   * with '/'.
   */
  public void addClassPath(String classPath)
      throws IOException
  {
    if (classPath == null)
        return;
        
    StringTokenizer tokenizer= new StringTokenizer(classPath, ",;");
    while (tokenizer.hasMoreTokens())
    {
      Resource resource= _context.newResource(tokenizer.nextToken().trim());
      if (LOG.isDebugEnabled())
          LOG.debug("Path resource=" + resource);

      // Add the resource
      if (resource.isDirectory() && resource instanceof ResourceCollection) {
        addClassPath(resource);
      } else {
        // Resolve file path if possible
        File file= resource.getFile();
        if (file != null) {
          URL url= resource.getURL();
          this.delegate.addUrl(url);
        } else if (resource.isDirectory()) {
          this.delegate.addUrl(resource.getURL());
        } else {
          if (LOG.isDebugEnabled())
              LOG.debug("Check file exists and is not nested jar: "+resource);
          throw new IllegalArgumentException("File not resolvable or incompatible with URLClassloader: "+resource);
        }
      }
    }
  }

  /* ------------------------------------------------------------ */
  /**
   * @param file Checks if this file type can be added to the classpath.
   */
  private boolean isFileSupported(String file)
  {
      int dot = file.lastIndexOf('.');
      return dot!=-1 && _extensions.contains(file.substring(dot));
  }

  /* ------------------------------------------------------------ */
  /** Add elements to the class path for the context from the jar and zip files found
   *  in the specified resource.
   * @param lib the resource that contains the jar and/or zip files.
   */
  public void addJars(Resource lib)
  {
      if (lib.exists() && lib.isDirectory())
      {
          String[] files=lib.list();
          for (int f=0;files!=null && f<files.length;f++)
          {
              try 
              {
                  Resource fn=lib.addPath(files[f]);
                  String fnlc=fn.getName().toLowerCase(Locale.ENGLISH);
                  // don't check if this is a directory, see Bug 353165
                  if (isFileSupported(fnlc))
                  {
                      String jar=fn.toString();
                      jar=StringUtil.replace(jar, ",", "%2C");
                      jar=StringUtil.replace(jar, ";", "%3B");
                      addClassPath(jar);
                  }
              }
              catch (Exception ex)
              {
                  LOG.warn(Log.EXCEPTION,ex);
              }
          }
      }
  }

  @Override
  public String toString()
  {
      return "WebAppClassLoader=" + _name+"@"+Long.toHexString(hashCode());
  }

  //Delagators to delegate:
  @Override
  public Class<?> loadClass(String name) throws ClassNotFoundException {
    return this.delegate.loadClass(name);
  }    
  
  @Override
  public Class<?> findClass(final String name) throws ClassNotFoundException {
    return this.delegate.findClass(name);
  }
}
