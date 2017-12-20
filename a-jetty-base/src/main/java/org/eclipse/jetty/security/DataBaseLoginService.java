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
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
 * HashMapped User Realm with DataBase as data source. 
 * The login() method checks the inherited Map for the user. If the user is not
 * found, it will fetch details from the database and populate the inherited
 * Map. It then calls the superclass login() method to perform the actual
 * authentication. Periodically (controlled by configuration parameter),
 * internal hashes are cleared. Caching can be disabled by setting cache refresh
 * interval to zero. Uses one database connection that is initialized at
 * startup. Reconnect on failures. authenticate() is 'synchronized'.
 * 
 * 
 * Based on JDBCLoginService.java
 * 
 * 
 * 
 * 
 */

public class DataBaseLoginService extends MappedLoginService
{
  private static final Logger LOG = Log.getLogger(DataBaseLoginService.class);

  protected int _cacheTime;
  protected long _lastHashPurge;

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
  /**
   * @see org.eclipse.jetty.security.MappedLoginService#doStart()
   */
  @Override
  protected void doStart() throws Exception
  {
      _cacheTime = 300000;
      _lastHashPurge = 0;
      super.doStart();
  }

  /* ------------------------------------------------------------ */
  @Override
  public UserIdentity login(String username, Object credentials)
  {
      long now = System.currentTimeMillis();
      if (now - _lastHashPurge > _cacheTime || _cacheTime == 0)
      {
          _users.clear();
          _lastHashPurge = now;
      }
      
      return super.login(username,credentials);
  }

  /* ------------------------------------------------------------ */
  @Override
  protected void loadUsers()
  {
    if (this.srvGetUserCredentials != null) {
      try
      {
        UserCredentials[] uca = this.srvGetUserCredentials.retrieveUsersCredentials();
        for (UserCredentials uc : uca) {
          putUser(uc.getUserName(), uc.getUserPassword(), uc.getUserRoles());
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
        return putUser(uc.getUserName(), uc.getUserPassword(), uc.getUserRoles());
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
