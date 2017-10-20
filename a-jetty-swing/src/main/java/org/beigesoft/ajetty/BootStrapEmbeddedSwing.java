package org.beigesoft.ajetty;

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

import java.io.File;
import java.util.Date;
import java.util.ResourceBundle;
import java.net.URI;
import java.net.URL;

import java.awt.GridLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.ImageIcon;

import org.beigesoft.afactory.IFactoryAppBeans;

/**
 * <p>
 * BootStrapEmbedded that launch A-Jetty as host 127.0.0.1 on chosen port,
 * for only WEB-application in <b>webapp</b> folder
 * For internationalization it expects overridden MessagesAjetty.properties
 * file.
 * </p>
 *
 * @author Yury Demidenko
 */
public class BootStrapEmbeddedSwing extends JFrame implements ActionListener {

  /**
   * <p>Factory app-beans - only for WEB-app class loader.</p>
   **/
  private final IFactoryAppBeans factoryAppBeans;

  /**
   * <p>Bootstrap.</p>
   **/
  private final BootStrapEmbedded bootStrapEmbedded;

  //Interface:
  /**
   * <p>Label port.</p>
   **/
  private final JLabel lbPort;

  /**
   * <p>Combo-box port.</p>
   **/
  private final JComboBox<Integer> cmbPort;

  /**
   * <p>Button start.</p>
   **/
  private final JButton btStart;

  /**
   * <p>Button refresh.</p>
   **/
  private final JButton btRefresh;

  /**
   * <p>Button stop.</p>
   **/
  private final JButton btStop;

  /**
   * <p>Button launch browser.</p>
   **/
  private final JButton btBrowse;

  /**
   * <p>I18N.</p>
   **/
  private final ResourceBundle messages;

  /**
   * <p>Flag to avoid double invoke and custom synchronization.</p>
   **/
  private boolean isActionPerforming = false;

  /**
   * <p>Time of last action start.</p>
   **/
  private long lastActionStartDate;

  /**
   * <p>Only constructor.</p>
   * @throws Exception any
   **/
  public BootStrapEmbeddedSwing() throws Exception {
    // Interface:
    URL iconURL = getClass().getResource("/favicon.png");
    ImageIcon icon = new ImageIcon(iconURL);
    setIconImage(icon.getImage());
    this.messages = ResourceBundle.getBundle("MessagesAjetty");
    setTitle(getMsg("title"));
    GridLayout layout = new GridLayout(6, 1);
    layout.setVgap(10);
    setLayout(layout);
    this.lbPort = new JLabel(getMsg("port"));
    add(this.lbPort);
    this.cmbPort = new JComboBox<Integer>();
    Integer p8080 = 8080;
    this.cmbPort.addItem(p8080);
    this.cmbPort.addItem(new Integer(8081));
    this.cmbPort.addItem(new Integer(8082));
    this.cmbPort.addItem(new Integer(8083));
    this.cmbPort.addItem(new Integer(8084));
    this.cmbPort.setSelectedItem(p8080);
    add(this.cmbPort);
    this.btStart = new JButton(getMsg("start"));
    this.btStart.addActionListener(this);
    add(this.btStart);
    this.btStop = new JButton(getMsg("stop"));
    this.btStop.addActionListener(this);
    add(this.btStop);
    this.btRefresh = new JButton(getMsg("refresh"));
    this.btRefresh.addActionListener(this);
    add(this.btRefresh);
    this.btBrowse = new JButton();
    this.btBrowse.addActionListener(this);
    add(this.btBrowse);
    setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    addWindowListener(windowListener);
    Dimension screenDimension = java.awt.Toolkit
      .getDefaultToolkit().getScreenSize();
    int width = Math.min(Double.valueOf(screenDimension.getWidth())
      .intValue(), 400);
    int height = Math.min(Double.valueOf(screenDimension.getHeight())
      .intValue(), 350);
    setPreferredSize(new Dimension(width, height));
    pack();
    setLocationRelativeTo(null);
    // A-Jetty:
    this.factoryAppBeans = new FactoryAppBeansEmbedded();
    String currDir = BootStrapEmbeddedSwing.class.getProtectionDomain()
      .getCodeSource().getLocation().toURI().getPath();
    String webAppPath = currDir.substring(0, currDir
      .lastIndexOf(File.separator) + 1) + "webapp";
    this.bootStrapEmbedded = new BootStrapEmbedded();
    this.bootStrapEmbedded.setFactoryAppBeans(this.factoryAppBeans);
    this.bootStrapEmbedded.setWebAppPath(webAppPath);
    this.bootStrapEmbedded.createServer();
    refreshUi();
  }

  @Override
  public final void actionPerformed(final ActionEvent pAe) {
    try {
      if (pAe.getSource() == this.btStart
        && !this.bootStrapEmbedded.getIsStarted()) {
        if (!this.isActionPerforming) {
          this.isActionPerforming = true;
          this.lastActionStartDate = new Date().getTime();
          this.bootStrapEmbedded
            .setPort((Integer) this.cmbPort.getSelectedItem());
          StartThread stThread = new StartThread();
          stThread.start();
          refreshUi();
        }
      } else if (pAe.getSource() == this.btStop
        && this.bootStrapEmbedded.getIsStarted()) {
        if (!this.isActionPerforming) {
          this.isActionPerforming = true;
          this.lastActionStartDate = new Date().getTime();
          StopThread stThread = new StopThread();
          stThread.start();
          refreshUi();
        }
      } else if (pAe.getSource() == this.btRefresh) {
        if (this.isActionPerforming) {
          // it was only time when server has started but
          // interface not respond (Class was synchronized)
          // The reason was cause either A-Jetty - did not exit from "start"
          // (so class was locked by start thread)
          // or Swing Interface itself get frozen
          long currDate = new Date().getTime();
          if (currDate - this.lastActionStartDate > 120000) {
            this.isActionPerforming = false;
          }
        }
        refreshUi();
      } else if (pAe.getSource() == this.btBrowse) {
        Desktop.getDesktop()
          .browse(new URI(this.btBrowse.getText()));
      }
    } catch (Exception ex) {
      ex.printStackTrace();
      setTitle("Error!");
    }
  }

  /**
   * <p>Refresh user interface.</p>
   **/
  public final void refreshUi() {
    if (this.isActionPerforming) {
      this.btBrowse.setText(getMsg("wait"));
      this.cmbPort.setEnabled(false);
      this.btStart.setEnabled(false);
      this.btStop.setEnabled(false);
      this.btBrowse.setEnabled(false);
    } else {
      if (this.bootStrapEmbedded.getIsStarted()) {
        this.btBrowse.setText("http://localhost:"
        + this.cmbPort.getSelectedItem());
        this.cmbPort.setEnabled(false);
        this.btStart.setEnabled(false);
        this.btStop.setEnabled(true);
        this.btBrowse.setEnabled(true);
      } else {
        this.btBrowse.setText(getMsg("stopped"));
        this.cmbPort.setEnabled(true);
        this.btStart.setEnabled(true);
        this.btStop.setEnabled(false);
        this.btBrowse.setEnabled(false);
      }
    }
  }

  /**
   * <p>This start Jetty with SWING interface.</p>
   * @param pArgs arguments
   **/
  public static final void main(final String[] pArgs) {
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          new BootStrapEmbeddedSwing().setVisible(true);
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
    });
  }

  /**
   * <p>To override odd behavior standard I18N.</p>
   * @param pKey key
   * @return i18n message
   **/
  public final String getMsg(final String pKey) {
    try {
      return this.messages.getString(pKey);
    } catch (Exception e) {
      return "[" + pKey + "]";
    }
  }

  //Inner classes:
  /**
   * <p>Stop A-Jetty on close.</p>
   **/
  private final WindowListener windowListener = new WindowAdapter() {

    @Override
    public final void windowClosing(final WindowEvent e) {
      if (BootStrapEmbeddedSwing.this.isActionPerforming) {
        long currDate = new Date().getTime();
        if (currDate - BootStrapEmbeddedSwing
          .this.lastActionStartDate > 120000) {
          BootStrapEmbeddedSwing.this.isActionPerforming = false;
        }
      }
      if (!BootStrapEmbeddedSwing.this.isActionPerforming) {
        if (BootStrapEmbeddedSwing.this.bootStrapEmbedded.getIsStarted()) {
          try {
            BootStrapEmbeddedSwing.this.bootStrapEmbedded.stopServer();
            BootStrapEmbeddedSwing.this.refreshUi();
          } catch (Exception ex) {
            ex.printStackTrace();
          }
        }
        BootStrapEmbeddedSwing.this.setVisible(false);
        BootStrapEmbeddedSwing.this.dispose();
        System.exit(0);
      }
    }
  };


  /**
   * <p>Thread to start A-Jetty.</p>
   */
  private class StartThread extends Thread {

    @Override
    public void run() {
      if (!BootStrapEmbeddedSwing.this.bootStrapEmbedded.getIsStarted()) {
        try {
          BootStrapEmbeddedSwing.this.bootStrapEmbedded.startServer();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
      BootStrapEmbeddedSwing.this.isActionPerforming = false;
      BootStrapEmbeddedSwing.this.refreshUi();
    }
  };

  /**
   * <p>Thread to stop A-Jetty.</p>
   */
  private class StopThread extends Thread {

    @Override
    public void run() {
      if (BootStrapEmbeddedSwing.this.bootStrapEmbedded.getIsStarted()) {
        try {
          BootStrapEmbeddedSwing.this.bootStrapEmbedded.stopServer();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
      BootStrapEmbeddedSwing.this.isActionPerforming = false;
      BootStrapEmbeddedSwing.this.refreshUi();
    }
  };

  //Simple getters and setters:
  /**
   * <p>Getter for isActionPerforming.</p>
   * @return boolean
   **/
  public final boolean getIsActionPerforming() {
    return this.isActionPerforming;
  }

  /**
   * <p>Getter for BootStrapEmbedded.</p>
   * @return final
   **/
  public final BootStrapEmbedded getBootStrapEmbedded() {
    return this.bootStrapEmbedded;
  }

  /**
   * <p>Getter for factoryAppBeans.</p>
   * @return IFactoryAppBeans
   **/
  public final IFactoryAppBeans getFactoryAppBeans() {
    return this.factoryAppBeans;
  }

  /**
   * <p>Getter for lbPort.</p>
   * @return JLabel
   **/
  public final JLabel getLbPort() {
    return this.lbPort;
  }

  /**
   * <p>Getter for cmbPort.</p>
   * @return JComboBox<Integer>
   **/
  public final JComboBox<Integer> getCmbPort() {
    return this.cmbPort;
  }

  /**
   * <p>Getter for btStart.</p>
   * @return JButton
   **/
  public final JButton getBtStart() {
    return this.btStart;
  }

  /**
   * <p>Getter for btStop.</p>
   * @return JButton
   **/
  public final JButton getBtStop() {
    return this.btStop;
  }

  /**
   * <p>Getter for btBrowse.</p>
   * @return JButton
   **/
  public final JButton getBtBrowse() {
    return this.btBrowse;
  }

  /**
   * <p>Getter for btRefresh.</p>
   * @return JButton
   **/
  public final JButton getBtRefresh() {
    return this.btRefresh;
  }

  /**
   * <p>Getter for messages.</p>
   * @return ResourceBundle
   **/
  public final ResourceBundle getMessages() {
    return this.messages;
  }

  /**
   * <p>Getter for windowListener.</p>
   * @return WindowListener
   **/
  public final WindowListener getWindowListener() {
    return this.windowListener;
  }

  /**
   * <p>Getter for lastActionStartDate.</p>
   * @return long
   **/
  public final long getLastActionStartDate() {
    return this.lastActionStartDate;
  }
}
