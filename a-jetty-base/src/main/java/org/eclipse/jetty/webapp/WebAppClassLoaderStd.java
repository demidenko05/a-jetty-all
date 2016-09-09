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
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.net.URL;
import java.net.URLClassLoader;
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
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.jetty.util.IO;
import org.eclipse.jetty.util.StringUtil;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.resource.ResourceCollection;

import org.beigesoft.lang.IUrlClassLoader;

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
public class WebAppClassLoaderStd extends URLClassLoader implements IUrlClassLoader
{
  private static final Logger LOG = Log.getLogger(WebAppClassLoaderStd.class);

  private final ClassLoader _parent;
  private final WebAppClassLoader.Context _context;
  private final List<ClassFileTransformer> _transformers = new CopyOnWriteArrayList<>();
  private String _name=String.valueOf(hashCode());

  public WebAppClassLoaderStd(
    WebAppClassLoader.Context context) throws IOException {
    this(null, context);
  }

  public WebAppClassLoaderStd(ClassLoader parent, WebAppClassLoader.Context context)
      throws IOException
  {
      super(new URL[]{},parent!=null?parent
              :(Thread.currentThread().getContextClassLoader()!=null?Thread.currentThread().getContextClassLoader()
                      :(WebAppClassLoaderStd.class.getClassLoader()!=null?WebAppClassLoaderStd.class.getClassLoader()
                              :ClassLoader.getSystemClassLoader())));
      _context = context;
      _parent=getParent();
      if (_parent==null)
          throw new IllegalArgumentException("no parent classloader!");
  }

  @Override
  public void addUrl(URL pUrl) {
    addURL(pUrl);
  }

  /* ------------------------------------------------------------ */
  @Override
  public PermissionCollection getPermissions(CodeSource cs)
  {
      PermissionCollection permissions=_context.getPermissions();
      PermissionCollection pc= (permissions == null) ? super.getPermissions(cs) : permissions;
      return pc;
  }

  /* ------------------------------------------------------------ */
  @Override
  public Enumeration<URL> getResources(String name) throws IOException
  {
      boolean system_class=_context.isSystemClass(name);
      boolean server_class=_context.isServerClass(name);
      
      List<URL> from_parent = toList(server_class?null:_parent.getResources(name));
      List<URL> from_webapp = toList((system_class&&!from_parent.isEmpty())?null:this.findResources(name));
          
      if (_context.isParentLoaderPriority())
      {
          from_parent.addAll(from_webapp);
          return Collections.enumeration(from_parent);
      }
      from_webapp.addAll(from_parent);
      return Collections.enumeration(from_webapp);
  }

  /* ------------------------------------------------------------ */
  private List<URL> toList(Enumeration<URL> e)
  {
      if (e==null)
          return new ArrayList<URL>();
      return Collections.list(e);
  }
  
  /* ------------------------------------------------------------ */
  /**
   * Get a resource from the classloader
   * 
   * NOTE: this method provides a convenience of hacking off a leading /
   * should one be present. This is non-standard and it is recommended 
   * to not rely on this behavior
   */
  @Override
  public URL getResource(String name)
  {
      URL url= null;
      boolean tried_parent= false;

      //If the resource is a class name with .class suffix, strip it off before comparison
      //as the server and system patterns are specified without a .class suffix
      String tmp = name;
      if (tmp != null && tmp.endsWith(".class"))
          tmp = tmp.substring(0, tmp.length()-6);
    
      boolean system_class=_context.isSystemClass(tmp);
      boolean server_class=_context.isServerClass(tmp);
      
      if (system_class && server_class)
          return null;
      
      if (_parent!=null &&(_context.isParentLoaderPriority() || system_class ) && !server_class)
      {
          tried_parent= true;
          
          if (_parent!=null)
              url= _parent.getResource(name);
      }

      if (url == null)
      {
          url= this.findResource(name);

          if (url == null && name.startsWith("/"))
          {
              if (LOG.isDebugEnabled())
                  LOG.debug("HACK leading / off " + name);
              url= this.findResource(name.substring(1));
          }
      }

      if (url == null && !tried_parent && !server_class )
      {
          if (_parent!=null)
              url= _parent.getResource(name);
      }

      if (url != null)
          if (LOG.isDebugEnabled())
              LOG.debug("getResource("+name+")=" + url);

      return url;
  }

  /* ------------------------------------------------------------ */
  @Override
  public Class<?> loadClass(String name) throws ClassNotFoundException
  {
      return loadClass(name, false);
  }

  /* ------------------------------------------------------------ */
  @Override
  public synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException
  {
      Class<?> c= findLoadedClass(name);
      ClassNotFoundException ex= null;
      boolean tried_parent= false;
      
      boolean system_class=_context.isSystemClass(name);
      boolean server_class=_context.isServerClass(name);
      
      if (system_class && server_class)
      {
          return null;
      }
      
      if (c == null && _parent!=null && (_context.isParentLoaderPriority() || system_class) && !server_class)
      {
          tried_parent= true;
          try
          {
              c= _parent.loadClass(name);
              if (LOG.isDebugEnabled())
                  LOG.debug("loaded " + c);
          }
          catch (ClassNotFoundException e)
          {
              ex= e;
          }
      }

      if (c == null)
      {
          try
          {
              c= this.findClass(name);
          }
          catch (ClassNotFoundException e)
          {
              ex= e;
          }
      }

      if (c == null && _parent!=null && !tried_parent && !server_class )
          c= _parent.loadClass(name);

      if (c == null && ex!=null)
          throw ex;

      if (resolve)
          resolveClass(c);

      if (LOG.isDebugEnabled())
          LOG.debug("loaded {} from {}",c,c==null?null:c.getClassLoader());
      
      return c;
  }

  /* ------------------------------------------------------------ */
  /**
   * @see addTransformer
   * @deprecated
   */
  public void addClassFileTransformer(ClassFileTransformer transformer)
  {
      _transformers.add(transformer);
  }
  
  /* ------------------------------------------------------------ */
  /**
   * @see removeTransformer
   * @deprecated
   */
  public boolean removeClassFileTransformer(ClassFileTransformer transformer)
  {
      return _transformers.remove(transformer);
  }

  /* ------------------------------------------------------------ */
  /**
   * @see addClassFileTransformer
   */
  public void addTransformer(ClassFileTransformer transformer)
  {
      _transformers.add(transformer);
  }
  
  /* ------------------------------------------------------------ */
  /**
   * @see removeClassFileTransformer
   */
  public boolean removeTransformer(ClassFileTransformer transformer)
  {
      return _transformers.remove(transformer);
  }
  
  
  /* ------------------------------------------------------------ */
  @Override
  public Class<?> findClass(final String name) throws ClassNotFoundException
  {
      Class<?> clazz=null;

      if (_transformers.isEmpty())
          clazz = super.findClass(name);
      else
      {
          String path = name.replace('.', '/').concat(".class");
          URL url = getResource(path);
          if (url==null)
              throw new ClassNotFoundException(name);

          InputStream content=null;
          try
          {
              content = url.openStream();
              byte[] bytes = IO.readBytes(content);
                  
              for (ClassFileTransformer transformer : _transformers)
              {
                  byte[] tmp = transformer.transform(this,name,null,null,bytes);
                  if (tmp != null)
                      bytes = tmp;
              }
              
              clazz=defineClass(name,bytes,0,bytes.length);
          }
          catch (IOException e)
          {
              throw new ClassNotFoundException(name,e);
          }
          catch (IllegalClassFormatException e)
          {
              throw new ClassNotFoundException(name,e);
          }
          finally
          {
              if (content!=null)
              {
                  try
                  {
                      content.close(); 
                  }
                  catch (IOException e)
                  {
                      throw new ClassNotFoundException(name,e);
                  }
              }
          }
      }

      return clazz;
  }
  
  /* ------------------------------------------------------------ */
  @Override
  public String toString()
  {
      return "WebAppClassLoader=" + _name+"@"+Long.toHexString(hashCode());
  }
}
