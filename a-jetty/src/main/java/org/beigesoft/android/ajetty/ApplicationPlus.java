package org.beigesoft.android.ajetty;

/*
 * Beigesoft ™
 *
 * Licensed under the Apache License, Version 2.0
 *
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
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
