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

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import org.beigesoft.android.app.ApplicationPlus;
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
  public static final String ACTION_STOP = "org.beigesoft.android.ajetty.STOP";

  /**
   * <p>onBind handler.</p>
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
      processStartRequest();
    } else if (action.equals(ACTION_STOP)) {
      processStopRequest();
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
    stopServer();
  }

  /**
   * <p>Start server for request.</p>
   */
  private void processStartRequest() {
    BootStrap bootStrap = getBootStrap();
    if (bootStrap != null && !bootStrap.getIsStarted()) {
      try {
        bootStrap.startServer();
        Toast.makeText(getApplicationContext(),
          "Server has been started!", Toast.LENGTH_SHORT).show();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * <p>Stop server for request.</p>
   */
  private void processStopRequest() {
    stopServer();
    stopSelf();
  }

  /**
   * <p>Stop server.</p>
   */
  private void stopServer() {
    BootStrap bootStrap = getBootStrap();
    if (bootStrap != null && bootStrap.getIsStarted()) {
      try {
        bootStrap.stopServer();
        Toast.makeText(getApplicationContext(),
          "Server has been stopped!", Toast.LENGTH_SHORT).show();
        bootStrap = null;
        ApplicationPlus appPlus = (ApplicationPlus) getApplicationContext();
        appPlus.getBeansMap()
          .remove(BootStrap.class.getCanonicalName());
        Thread.sleep(1000);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * <p>Get BootStrap from app-context.</p>
   * @return BootStrap BootStrap
   */
  private BootStrap getBootStrap() {
    BootStrap bootStrap = null;
    ApplicationPlus appPlus = (ApplicationPlus) getApplicationContext();
    Object bootStrapO = appPlus.getBeansMap()
      .get(BootStrap.class.getCanonicalName());
    if (bootStrapO != null) {
      bootStrap = (BootStrap) bootStrapO;
    } else {
      Toast.makeText(getApplicationContext(),
        "BootStap not initialized!", Toast.LENGTH_SHORT).show();
      stopSelf();
    }
    return bootStrap;
  }
}
