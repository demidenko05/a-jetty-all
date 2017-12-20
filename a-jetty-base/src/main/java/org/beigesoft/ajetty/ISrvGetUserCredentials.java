package org.beigesoft.ajetty;

/*
 * Copyright (c) 2017 Beigesoft ™
 *
 * Licensed under the GNU General Public License (GPL), Version 2.0
 * (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.en.html
 */

/**
 * <p>Abstraction of service that get user credentials (from database).</p>
 * @author Yury Demidenko
 */
public interface ISrvGetUserCredentials {

  /**
   * <p>Retrieve User Credentials.</p>
   * @param pUserName User Name
   * @return User Credentials
   * @throws Exception - an exception
   **/
   UserCredentials retrieveUserCredentials(String pUserName) throws Exception;

  /**
   * <p>Retrieve all Users Credentials.</p>
   * @return Users Credentials
   * @throws Exception - an exception
   **/
   UserCredentials[] retrieveUsersCredentials() throws Exception;
}
