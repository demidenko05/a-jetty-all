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

import java.util.HashMap;
import java.util.Map;
import android.app.Application;

/**
 * <p>It extends Application to contains application beans map.</p>
 *
 * @author Yury Demidenko
 */
public class ApplicationPlus extends Application {

  /**
   * <p>Application beans map.</p>
   **/
  private final Map<String, Object> beansMap = new HashMap<String, Object>();

  /**
   * Shared services
   * (do not hold medium and big data in it!!!
   * Use a data storage(SQL, a file...) to hold that data!)
   * @return a service
   */
  public final Map<String, Object> getBeansMap() {
    return this.beansMap;
  }
}
