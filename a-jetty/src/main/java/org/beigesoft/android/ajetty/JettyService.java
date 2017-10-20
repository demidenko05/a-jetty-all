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

import java.util.Map;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import org.beigesoft.ajetty.BootStrap;

/**
 * <p>A-Jetty Android service.</p>
 *
 * @author Yury Demidenko
 */
public class JettyService extends Service {

  /**
   * <p>Action start.</p>
   **/
  public static final String ACTION_START =
    "org.beigesoft.android.ajetty.START";

  /**
   * <p>Action stop.</p>
   **/
  public static final String ACTION_STOP
    = "org.beigesoft.android.ajetty.STOP";

  /**
   * <p>Application beans map reference to lock.</p>
   **/
  private Map<String, Object> beansMap;

  /**
   * <p>Flag to avoid double invoke.</p>
   **/
  private boolean isActionPerforming = false;

  /**
   * <p>on create.</p>
   **/
  @Override
  public final void onCreate() {
    ApplicationPlus appPlus = (ApplicationPlus) getApplicationContext();
    this.beansMap = appPlus.getBeansMap();
  }

  /**
   * <p>onBind handler. No bind provide.</p>
   * @param pIntent Intent
   * @return IBinder IBinder
   **/
  @Override
  public final IBinder onBind(final Intent pIntent) {
    return null;
  }

  /**
   * <p>Called when we receive an Intent. When we receive an intent sent
   * to us via startService(), this is the method that gets called.
   * So here we react appropriately depending on the
   * Intent's action, which specifies what is being requested of us.</p>
   * @param pIntent Intent
   * @param pFlags flags
   * @param pStartId startId
   * @return int status
   */
  @Override
  public final int onStartCommand(final Intent pIntent,
    final int pFlags, final int pStartId) {
    String action = pIntent.getAction();
    if (action.equals(ACTION_START)) {
      synchronized (this) {
        if (!this.isActionPerforming) {
          this.isActionPerforming = true;
          StartThread stThread = new StartThread();
          stThread.start();
        }
      }
    } else if (action.equals(ACTION_STOP)) {
      synchronized (this) {
        if (!this.isActionPerforming) {
          this.isActionPerforming = true;
          StopThread stThread = new StopThread();
          stThread.start();
        }
      }
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      stopSelf();
    }
    return START_NOT_STICKY; // Means we started the service, but don't want
                             //it to restart in case it's killed.
  }

  /**
   * <p>Android Service destroy.
   * @see android.app.Service#onDestroy().</p>
   */
  @Override
  public final void onDestroy() {
    //nothing;
  }

  /**
   * <p>Get BootStrapEmbedded from app-context.
   * It invoked by start/stop threads.</p>
   * @return BootStrap BootStrap
   */
  private BootStrap getBootStrap() {
    BootStrap bootStrap = null;
    // this.beansMap already synchronized
    Object bootStrapO = this.beansMap
      .get(BootStrap.class.getCanonicalName());
    if (bootStrapO != null) {
      bootStrap = (BootStrap) bootStrapO;
    } else {
      //already stopped
      stopSelf();
    }
    return bootStrap;
  }

  /**
   * <p>Thread to start A-Jetty.</p>
   */
  private class StartThread extends Thread {

    @Override
    public void run() {
      synchronized (JettyService.this.beansMap) {
        BootStrap bootStrap = getBootStrap();
        if (bootStrap != null && !bootStrap.getIsStarted()) {
          try {
            if (bootStrap.getServer() == null) {
              bootStrap.createServer();
              bootStrap.getDeploymentManager().getContextAttributes()
                .setAttribute("android.content.Context", JettyService.this);
            }
            bootStrap.startServer();
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      }
      synchronized (JettyService.this) {
        JettyService.this.isActionPerforming = false;
      }
    }
  };

  /**
   * <p>Thread to stop A-Jetty.</p>
   */
  private class StopThread extends Thread {

    @Override
    public void run() {
      synchronized (JettyService.this.beansMap) {
        BootStrap bootStrap = JettyService.this
          .getBootStrap();
        if (bootStrap != null && bootStrap.getIsStarted()) {
          try {
            bootStrap.stopServer();
            JettyService.this.beansMap
              .remove(BootStrap.class.getCanonicalName());
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      }
      synchronized (JettyService.this) {
        JettyService.this.isActionPerforming = false;
      }
    }
  };

  //Simple getters and setters:
  /**
   * <p>Getter for beansMap.</p>
   * @return Map<String, Object>
   **/
  public final Map<String, Object> getBeansMap() {
    return this.beansMap;
  }

  /**
   * <p>Setter for beansMap.</p>
   * @param pBeansMap reference
   **/
  public final void setBeansMap(final Map<String, Object> pBeansMap) {
    this.beansMap = pBeansMap;
  }

  /**
   * <p>Getter for isActionPerforming.</p>
   * @return boolean
   **/
  public final boolean getIsActionPerforming() {
    return this.isActionPerforming;
  }
}
