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

import java.util.Date;
import java.net.URI;
import java.net.URL;
import java.text.NumberFormat;

import java.awt.GridLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFormattedTextField;
import javax.swing.JPasswordField;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.ImageIcon;

/**
 * <p>
 * BootStrapEmbeddedHttpsSwing launches A-Jetty as host 127.0.0.1 on opted port
 * and uses HTTPS connector,
 * for only WEB-application in <b>webapp</b> folder
 * For internationalization it expects overridden MessagesAjetty.properties
 * file.
 * </p>
 *
 * @author Yury Demidenko
 */
public class BootStrapEmbeddedHttpsSwing extends JFrame
  implements ActionListener, IBootStrapIFace {

  //Interface:
  /**
   * <p>Label A-Jetty instance number.</p>
   **/
  private JLabel lbAjettyIn;

  /**
   * <p>A-Jetty instance number.</p>
   **/
  private JFormattedTextField ftfAjettyIn;

  /**
   * <p>Label keystore password.</p>
   **/
  private JLabel lbKeystorePw;

  /**
   * <p>Keystore password.</p>
   **/
  private JPasswordField pfKeystorePw;

  /**
   * <p>Label keystore password conformation.</p>
   **/
  private JLabel lbKeystorePwc;

  /**
   * <p>A-Jetty instance password conformation.</p>
   **/
  private JPasswordField pfKeystorePwc;

  /**
   * <p>Label port.</p>
   **/
  private JLabel lbPort;

  /**
   * <p>Combo-box port.</p>
   **/
  private JComboBox<Integer> cmbPort;

  /**
   * <p>Button start.</p>
   **/
  private JButton btStart;

  /**
   * <p>Button refresh.</p>
   **/
  private JButton btRefresh;

  /**
   * <p>Button stop.</p>
   **/
  private JButton btStop;

  /**
   * <p>Button launch browser.</p>
   **/
  private JButton btBrowse;

  /**
   * <p>Main boot strap.</p>
   **/
  private BootStrapEmbeddedMain mainBootStrap;

  /**
   * <p>Starts interface.</p>
   * @throws Exception any
   **/
  public final void startInterface() throws Exception {
    try {
      setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      // Interface:
      URL iconURL = getClass().getResource("/favicon.png");
      ImageIcon icon = new ImageIcon(iconURL);
      setIconImage(icon.getImage());
      if (this.mainBootStrap.getIsDebug()) {
        setTitle("DEBUGGING IS ON!!!");
      } else {
        setTitle(this.mainBootStrap.getMsg("title"));
      }
      GridLayout layout = new GridLayout(12, 1);
      layout.setVgap(10);
      setLayout(layout);
      this.lbAjettyIn = new JLabel(this.mainBootStrap.getMsg("AJettyIn"));
      add(this.lbAjettyIn);
      this.ftfAjettyIn = new JFormattedTextField(NumberFormat
        .getIntegerInstance());
      add(this.ftfAjettyIn);
      this.lbKeystorePw = new JLabel(this.mainBootStrap.getMsg("KeystorePw"));
      add(this.lbKeystorePw);
      this.pfKeystorePw = new JPasswordField();
      add(this.pfKeystorePw);
      this.lbKeystorePwc = new JLabel(this.mainBootStrap.getMsg("KeystorePwc"));
      add(this.lbKeystorePwc);
      this.pfKeystorePwc = new JPasswordField();
      add(this.pfKeystorePwc);
      this.lbPort = new JLabel(this.mainBootStrap.getMsg("port"));
      add(this.lbPort);
      this.cmbPort = new JComboBox<Integer>();
      Integer p8443 = 8443;
      this.cmbPort.addItem(p8443);
      this.cmbPort.addItem(new Integer(8444));
      this.cmbPort.addItem(new Integer(8445));
      this.cmbPort.addItem(new Integer(8446));
      this.cmbPort.addItem(new Integer(8447));
      this.cmbPort.setSelectedItem(p8443);
      add(this.cmbPort);
      this.btStart = new JButton(this.mainBootStrap.getMsg("start"));
      this.btStart.addActionListener(this);
      add(this.btStart);
      this.btStop = new JButton(this.mainBootStrap.getMsg("stop"));
      this.btStop.addActionListener(this);
      add(this.btStop);
      this.btRefresh = new JButton(this.mainBootStrap.getMsg("refresh"));
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
        .intValue(), 500);
      setPreferredSize(new Dimension(width, height));
      pack();
      setLocationRelativeTo(null);
      setVisible(true);
      refreshUi();
    } catch (Exception e) {
      this.mainBootStrap.getLogger()
        .error(null, BootStrapEmbeddedHttpsSwing.class, null, e);
      throw e;
    }
  }

  @Override
  public final void actionPerformed(final ActionEvent pAe) {
    try {
      if (pAe.getSource() == this.btStart
        && !this.mainBootStrap.getBootStrapEmbeddedHttps().getIsStarted()) {
        if (!this.mainBootStrap.getIsActionPerforming()) {
          if (this.mainBootStrap.getIsDebug()) {
            setTitle("DEBUGGING IS ON!!!");
          } else {
            setTitle(this.mainBootStrap.getMsg("title"));
          }
          this.mainBootStrap.setIsActionPerforming(true);
          if (!this.mainBootStrap.getIsKeystoreCreated()) {
            Long ajl = (Long) ftfAjettyIn.getValue();
            if (ajl != null) {
              try {
                this.mainBootStrap.setAjettyIn(Integer.parseInt(ajl
                  .toString()));
              } catch (Exception e) {
                e.printStackTrace();
              }
            }
          }
          if (this.mainBootStrap.getAjettyIn() == null) {
            JOptionPane.showMessageDialog(this, this.mainBootStrap
              .getMsg("EnterAjettyNumber"), this.mainBootStrap.getMsg("error"),
                JOptionPane.ERROR_MESSAGE);
            this.mainBootStrap.setIsActionPerforming(false);
            return;
          }
          this.mainBootStrap.setKsPassword(this.pfKeystorePw.getPassword());
          this.mainBootStrap.setKsPasswordConf(this
            .pfKeystorePwc.getPassword());
          this.mainBootStrap.setPort((Integer) this.cmbPort.getSelectedItem());
          this.mainBootStrap.startAjetty();
          refreshUi();
        }
      } else if (pAe.getSource() == this.btStop
        && this.mainBootStrap.getBootStrapEmbeddedHttps().getIsStarted()) {
        if (!this.mainBootStrap.getIsActionPerforming()) {
          if (this.mainBootStrap.getIsDebug()) {
            setTitle("DEBUGGING IS ON!!!");
          } else {
            setTitle(this.mainBootStrap.getMsg("title"));
          }
          this.mainBootStrap.setIsActionPerforming(true);
          this.mainBootStrap.setLastActionStartDate(new Date().getTime());
          this.mainBootStrap.stopAjetty();
          refreshUi();
        }
      } else if (pAe.getSource() == this.btRefresh) {
        if (this.mainBootStrap.getIsDebug()) {
          setTitle("DEBUGGING IS ON!!!");
        } else {
          setTitle(this.mainBootStrap.getMsg("title"));
        }
        if (this.mainBootStrap.getIsActionPerforming()) {
          // it was only time when server has started but
          // interface not respond (Class was synchronized)
          // The reason was cause either A-Jetty - did not exit from "start"
          // (so class was locked by start thread)
          // or Swing Interface itself get frozen
          long currDate = new Date().getTime();
          if (currDate - this.mainBootStrap.getLastActionStartDate() > 120000) {
            this.mainBootStrap.setIsActionPerforming(false);
            this.mainBootStrap.getLogger()
              .error(null, BootStrapEmbeddedHttpsSwing.class, "Frozen!!!");
          }
        }
        refreshUi();
      } else if (pAe.getSource() == this.btBrowse) {
        Desktop.getDesktop()
          .browse(new URI(this.btBrowse.getText()));
      }
    } catch (Exception ex) {
      this.mainBootStrap.setIsActionPerforming(false);
      setTitle("Error! See starter.log!");
      this.mainBootStrap.getLogger()
        .error(null, BootStrapEmbeddedHttpsSwing.class, null, ex);
    }
  }

  /**
   * <p>Show error message.</p>
   * @param pError message
   **/
  @Override
  public final void showError(final String pError) {
    JOptionPane.showMessageDialog(this, pError,
      this.mainBootStrap.getMsg("error"), JOptionPane.ERROR_MESSAGE);
  }

  /**
   * <p>Refresh user interface.</p>
   **/
  @Override
  public final void refreshUi() {
    if (this.mainBootStrap.getIsActionPerforming()) {
      this.btBrowse.setText(this.mainBootStrap.getMsg("wait"));
      this.cmbPort.setEnabled(false);
      this.btStart.setEnabled(false);
      this.btStop.setEnabled(false);
      this.btBrowse.setEnabled(false);
      this.ftfAjettyIn.setEnabled(false);
      this.pfKeystorePwc.setEnabled(false);
    } else {
      if (this.mainBootStrap.getBootStrapEmbeddedHttps().getIsStarted()) {
        this.btBrowse.setText("https://localhost:"
          + this.cmbPort.getSelectedItem() + "/bsa"
            + this.cmbPort.getSelectedItem());
        this.pfKeystorePw.setEnabled(false);
        this.ftfAjettyIn.setEnabled(false);
        this.pfKeystorePwc.setEnabled(false);
        this.cmbPort.setEnabled(false);
        this.btStart.setEnabled(false);
        this.btStop.setEnabled(true);
        this.btBrowse.setEnabled(true);
      } else {
        if (this.mainBootStrap.getIsKeystoreCreated()) {
          this.ftfAjettyIn.setEnabled(false);
          this.pfKeystorePwc.setEnabled(false);
          this.ftfAjettyIn.setText(this.mainBootStrap.getAjettyIn().toString());
        } else {
          this.ftfAjettyIn.setEnabled(true);
          this.pfKeystorePwc.setEnabled(true);
        }
        this.btBrowse.setText(this.mainBootStrap.getMsg("stopped"));
        this.pfKeystorePw.setEnabled(true);
        this.cmbPort.setEnabled(true);
        this.btStart.setEnabled(true);
        this.btStop.setEnabled(false);
        this.btBrowse.setEnabled(false);
      }
    }
  }

  //Inner classes:
  /**
   * <p>Stop A-Jetty on close.</p>
   **/
  private WindowListener windowListener = new WindowAdapter() {

    @Override
    public final void windowClosing(final WindowEvent e) {
      if (BootStrapEmbeddedHttpsSwing.this.mainBootStrap
        .getIsActionPerforming()) {
        long currDate = new Date().getTime();
        if (currDate - BootStrapEmbeddedHttpsSwing
          .this.mainBootStrap.getLastActionStartDate() > 120000) {
          BootStrapEmbeddedHttpsSwing.this.mainBootStrap
            .setIsActionPerforming(false);
          BootStrapEmbeddedHttpsSwing.this.mainBootStrap.getLogger()
            .error(null, BootStrapCli.class, "Frozen!!!");
        }
      }
      if (!BootStrapEmbeddedHttpsSwing.this.mainBootStrap
        .getIsActionPerforming()) {
        if (BootStrapEmbeddedHttpsSwing.this
          .mainBootStrap.getBootStrapEmbeddedHttps().getServer() != null) {
          try {
            BootStrapEmbeddedHttpsSwing.this
              .mainBootStrap.getBootStrapEmbeddedHttps().stopServer();
            BootStrapEmbeddedHttpsSwing.this.refreshUi();
          } catch (Exception ex) {
            BootStrapEmbeddedHttpsSwing.this.mainBootStrap.getLogger()
              .error(null, BootStrapEmbeddedHttpsSwing.class, null, ex);
          }
        }
        BootStrapEmbeddedHttpsSwing.this.setVisible(false);
        BootStrapEmbeddedHttpsSwing.this.dispose();
      }
    }
  };

  //Simple getters and setters:
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
   * <p>Getter for windowListener.</p>
   * @return WindowListener
   **/
  public final WindowListener getWindowListener() {
    return this.windowListener;
  }

  /**
   * <p>Getter for mainBootStrap.</p>
   * @return BootStrapEmbeddedMain
   **/
  public final BootStrapEmbeddedMain getMainBootStrap() {
    return this.mainBootStrap;
  }

  /**
   * <p>Setter for mainBootStrap.</p>
   * @param pMainBootStrap reference
   **/
  public final void setMainBootStrap(
    final BootStrapEmbeddedMain pMainBootStrap) {
    this.mainBootStrap = pMainBootStrap;
  }
}
