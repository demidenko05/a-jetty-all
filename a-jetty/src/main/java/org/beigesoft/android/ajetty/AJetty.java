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

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Build;
import android.os.Environment;
import android.os.AsyncTask;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.beigesoft.android.app.ApplicationPlus;
import org.beigesoft.ajetty.BootStrap;

/**
 * <p>A-Jetty activity.</p>
 *
 * @author Yury Demidenko
 */
public class AJetty extends Activity implements OnClickListener {

  /**
   * <p>JETTY BASE dir.</p>
   **/
  public static final String JETTY_BASE = "A-Jetty";

  /**
   * <p>Flag to refresh UI.</p>
   **/
  private boolean isNeedsToRefresh;

  /**
   * <p>Button start.</p>
   **/
  private Button btnStart;

  /**
   * <p>Button stop.</p>
   **/
  private Button btnStop;

  /**
   * <p>EditText Port.</p>
   **/
  private EditText etPort;

  /**
   * <p>TextView Status.</p>
   **/
  private TextView tvStatus;

  /**
   * <p>TextView link.</p>
   **/
  private TextView tvLink;

  /**
   * <p>Called when the activity is first created or recreated.</p>
   * @param pSavedInstanceState Saved Instance State
   */
  @Override
  public final void onCreate(final Bundle pSavedInstanceState) {
    super.onCreate(pSavedInstanceState);
    setContentView(R.layout.ajetty);
    this.tvStatus = (TextView) findViewById(R.id.tvStatus);
    this.tvLink = (TextView) findViewById(R.id.tvLink);
    this.etPort = (EditText) findViewById(R.id.etPort);
    this.btnStart = (Button) findViewById(R.id.btnStart);
    this.btnStop = (Button) findViewById(R.id.btnStop);
    this.btnStart.setOnClickListener(this);
    this.btnStop.setOnClickListener(this);
    File jettyBase = new File(Environment.getExternalStorageDirectory()
      .getAbsolutePath() + File.separator + JETTY_BASE);
    if (!jettyBase.exists()) {
      boolean wasMistake = false;
      try {
        Toast.makeText(getApplicationContext(),
            "Try to create A-Jetty directory with webapps...",
              Toast.LENGTH_SHORT).show();
        if (!jettyBase.mkdirs()) {
          wasMistake = true;
        }
        copyAssets(JETTY_BASE);
      } catch (Exception e) {
        wasMistake = true;
        e.printStackTrace();
      }
      if (wasMistake) {
        Toast.makeText(getApplicationContext(),
          "There was errors!",
            Toast.LENGTH_SHORT).show();
      } else {
        Toast.makeText(getApplicationContext(),
          "A-Jetty directory with webapps was successfully created!",
            Toast.LENGTH_SHORT).show();
      }
    }
  }

  /**
   * <p>onClick handler.</p>
   * @param pTarget button
   */
  @Override
  public final void onClick(final View pTarget) {
    if (pTarget == this.btnStart) {
      startService(new Intent(JettyService.ACTION_START));
    } else if (pTarget == this.btnStop) {
      startService(new Intent(JettyService.ACTION_STOP));
    }
  }

  /**
   * <p>onResume handler.</p>
   */
  @Override
  public final void onResume() {
    isNeedsToRefresh = true;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
      new Refresher().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
        (Void[]) null);
    } else {
      new Refresher().execute((Void[]) null);
    }
    super.onResume();
  }

  /**
   * <p>onPause handler.</p>
   */
  @Override
  public final void onPause() {
    this.isNeedsToRefresh = false;
    try {
      Thread.sleep(100);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    super.onPause();
  }

  /**
   * <p>Refresh view.</p>
   */
  private void refreshView() {
    BootStrap bootStrap = getOrInitBootStrap();
    if (bootStrap.getIsStarted()) {
      this.btnStart.setEnabled(false);
      this.btnStop.setEnabled(true);
      this.tvStatus.setText(getResources().getString(R.string.started));
      String text = "You can type in browser: http://localhost:"
        + String.valueOf(bootStrap.getPort());
      this.tvLink.setText(text);
    } else {
      this.btnStart.setEnabled(true);
      this.btnStop.setEnabled(false);
      this.tvStatus.setText(getResources().getString(R.string.stopped));
      this.tvLink.setText("");
    }
    this.etPort.setText(String.valueOf(bootStrap.getPort()));
  }

  /**
   * <p>Get Or Initialize BootStrap.</p>
   * @return BootStrap BootStrap
   */
  private BootStrap getOrInitBootStrap() {
    BootStrap bootStrap = null;
    ApplicationPlus appPlus = (ApplicationPlus) getApplicationContext();
    Object bootStrapO = appPlus.getBeansMap()
      .get(BootStrap.class.getCanonicalName());
    if (bootStrapO != null) {
      bootStrap = (BootStrap) bootStrapO;
    } else { // initialize:
      bootStrap = new BootStrap();
      bootStrap.setJettyBase(Environment
        .getExternalStorageDirectory().getAbsolutePath() + File.separator
          + JETTY_BASE);
      try {
        bootStrap.setFactoryAppBeans(new FactoryAppBeansAndroid(this));
        bootStrap.createServer();
        bootStrap.getDeploymentManager().getContextAttributes()
          .setAttribute("android.content.Context", this);
      } catch (Exception e) {
        e.printStackTrace();
      }
      appPlus.getBeansMap()
        .put(BootStrap.class.getCanonicalName(), bootStrap);
    }
    return bootStrap;
  }

  /**
   * <p>Recursively copy assets.</p>
   * @param pCurrDir current directory assets
   * @throws Exception an Exception
   */
  private void copyAssets(final String pCurrDir) throws Exception {
    AssetManager assetManager = getAssets();
    String[] files = assetManager.list(pCurrDir);
    for (String fileName : files) {
      String createdPath = Environment.getExternalStorageDirectory()
          .getAbsolutePath() + File.separator + pCurrDir + File.separator
            + fileName;
      if (!fileName.contains(".")) {
        File subdir = new File(createdPath);
        if (subdir.mkdirs()) {
          copyAssets(pCurrDir + File.separator + fileName);
        }
      } else {
        InputStream ins = null;
        OutputStream outs = null;
        try {
          ins = getAssets().open(pCurrDir + File.separator + fileName);
          outs = new BufferedOutputStream(
            new FileOutputStream(createdPath));
          byte[] data = new byte[1024];
          int count;
          while ((count = ins.read(data)) != -1) {
              outs.write(data, 0, count);
          }
          outs.flush();
        } finally {
          if (ins != null) {
            try {
              ins.close();
            } catch (Exception e2) {
              e2.printStackTrace();
            }
          }
          if (outs != null) {
            try {
              outs.close();
            } catch (Exception e3) {
              e3.printStackTrace();
            }
          }
        }
      }
    }
  }


  /**
   * <p>Refresher thread.</p>
   */
  private class Refresher extends AsyncTask<Void, Void, Void> {

    /**
     * <p>doInBackground check is need refresh.</p>
     */
    @Override
    protected final Void doInBackground(final Void... params) {
      while (AJetty.this.isNeedsToRefresh) {
        publishProgress((Void[]) null);
        try {
          Thread.sleep(2000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
      return null;
    }

    /**
     * <p>onProgressUpdate call refresh.</p>
     */
    @Override
    protected final void onProgressUpdate(final Void... values) {
      AJetty.this.refreshView();
      super.onProgressUpdate(values);
    }
  }
}
