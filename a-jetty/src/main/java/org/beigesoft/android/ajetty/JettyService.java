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
   * <p>Start Thread.</p>
   **/
  private StartThread startThread;

  /**
   * <p>Stop Thread.</p>
   **/
  private StopThread stopThread;


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
        if (getStartThread() == null) {
          StartThread stThread = new StartThread();
          setStartThread(stThread);
          stThread.start();
        }
      }
    } else if (action.equals(ACTION_STOP)) {
      synchronized (this) {
        if (getStopThread() == null) {
          StopThread stThread = new StopThread();
          setStopThread(stThread);
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
   * <p>Get BootStrap from app-context.</p>
   * @return BootStrap BootStrap
   */
  private BootStrap getBootStrap() {
    BootStrap bootStrap = null;
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
            bootStrap.startServer();
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      }
      synchronized (JettyService.this) {
        JettyService.this.setStartThread(null);
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
            bootStrap = null;
            JettyService.this.beansMap
              .remove(BootStrap.class.getCanonicalName());
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      }
      synchronized (JettyService.this) {
        JettyService.this.setStopThread(null);
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
   * <p>Getter for startThread.</p>
   * @return StartThread
   **/
  public final StartThread getStartThread() {
    return this.startThread;
  }

  /**
   * <p>Setter for startThread.</p>
   * @param pStartThread reference
   **/
  public final void setStartThread(final StartThread pStartThread) {
    this.startThread = pStartThread;
  }

  /**
   * <p>Getter for stopThread.</p>
   * @return StopThread
   **/
  public final StopThread getStopThread() {
    return this.stopThread;
  }

  /**
   * <p>Setter for stopThread.</p>
   * @param pStopThread reference
   **/
  public final void setStopThread(final StopThread pStopThread) {
    this.stopThread = pStopThread;
  }
}
