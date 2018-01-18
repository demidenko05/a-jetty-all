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

package org.eclipse.jetty.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.eclipse.jetty.server.UserIdentity;
import org.eclipse.jetty.util.Loader;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.security.Credential;

import org.beigesoft.ajetty.ISrvGetUserCredentials;
import org.beigesoft.ajetty.UserCredentials;

/* ------------------------------------------------------------ */
/**
 * 
 * It's based on JDBCLoginService.java
 * It never caches DB data.
 * ISrvGetUserCredentials is JDBC/Android database retriever.
 */

public class DataBaseLoginService extends MappedLoginService
{
  private static final Logger LOG = Log.getLogger(DataBaseLoginService.class);


  private ISrvGetUserCredentials srvGetUserCredentials;

  /* ------------------------------------------------------------ */
  public DataBaseLoginService()
      throws IOException
  {
  }
  
  /* ------------------------------------------------------------ */
  public DataBaseLoginService(String name)
      throws IOException
  {
      setName(name);
  }

  /* ------------------------------------------------------------ */
  public DataBaseLoginService(String name, IdentityService identityService)
      throws IOException
  {
      setName(name);
      setIdentityService(identityService);
  }

  /* ------------------------------------------------------------ */
  @Override
  protected void loadUsers()
  {
    if (this.srvGetUserCredentials != null) {
      try
      {
        UserCredentials[] uca = this.srvGetUserCredentials.retrieveUsersCredentials();
        if (uca != null) {
          for (UserCredentials uc : uca) {
            putUser(uc.getUserName(), uc.getUserPassword(), uc.getUserRoles());
          }
        }
      }
      catch (Exception e)
      {
          LOG.warn("UserRealm " + getName() + " could not load user information from database", e);
      }
    }
  }
  
  /* ------------------------------------------------------------ */
  @Override
  protected UserIdentity loadUser(String username)
  {
    if (this.srvGetUserCredentials != null) {
      try
      {
        UserCredentials uc = this.srvGetUserCredentials.retrieveUserCredentials(username);
        if (uc != null) {
          return putUser(uc.getUserName(), uc.getUserPassword(), uc.getUserRoles());
        }
      }
      catch (Exception e)
      {
          LOG.warn("UserRealm " + getName() + " could not load user information from database", e);
      }
    }
    return null;
  }
  
  /* ------------------------------------------------------------ */
  protected UserIdentity putUser (String username, String credentials, String[] roles)
  {
      return putUser(username, Credential.getCredential(credentials),roles);
  }

  /**
   * <p>Getter for srvGetUserCredentials.</p>
   * @return ISrvGetUserCredentials
   **/
  public final ISrvGetUserCredentials getSrvGetUserCredentials() {
    return this.srvGetUserCredentials;
  }

  /**
   * <p>Setter for srvGetUserCredentials.</p>
   * @param pSrvGetUserCredentials reference
   **/
  public final void setSrvGetUserCredentials(final ISrvGetUserCredentials pSrvGetUserCredentials) {
    this.srvGetUserCredentials = pSrvGetUserCredentials;
  }
}
