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
 * <p>User credentials container.</p>
 * @author Yury Demidenko
 */
public class UserCredentials {

  /**
   * <p>User name.</p>
   **/
   private String userName;

  /**
   * <p>User password.</p>
   **/
   private String userPassword;

  /**
   * <p>User roles.</p>
   **/
   private String[] userRoles;

  //Simple getters and setters:

  /**
   * <p>Getter for userName.</p>
   * @return String
   **/
  public final String getUserName() {
    return this.userName;
  }

  /**
   * <p>Setter for userName.</p>
   * @param pUserName reference
   **/
  public final void setUserName(final String pUserName) {
    this.userName = pUserName;
  }

  /**
   * <p>Getter for userPassword.</p>
   * @return String
   **/
  public final String getUserPassword() {
    return this.userPassword;
  }

  /**
   * <p>Setter for userPassword.</p>
   * @param pUserPassword reference
   **/
  public final void setUserPassword(final String pUserPassword) {
    this.userPassword = pUserPassword;
  }

  /**
   * <p>Getter for userRoles.</p>
   * @return String[]
   **/
  public final String[] getUserRoles() {
    return this.userRoles;
  }

  /**
   * <p>Setter for userRoles.</p>
   * @param pUserRoles reference
   **/
  public final void setUserRoles(final String[] pUserRoles) {
    this.userRoles = pUserRoles;
  }
}
