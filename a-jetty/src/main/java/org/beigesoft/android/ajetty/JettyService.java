/*
BSD 2-Clause License

Copyright (c) 2019, Beigesoft™
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

* Redistributions of source code must retain the above copyright notice, this
  list of conditions and the following disclaimer.

* Redistributions in binary form must reproduce the above copyright notice,
  this list of conditions and the following disclaimer in the documentation
  and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.beigesoft.android.ajetty;

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
