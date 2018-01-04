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

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.ResourceBundle;
import java.net.URI;
import java.net.URL;
import java.text.NumberFormat;
import java.security.PublicKey;
import java.security.KeyStore;
import java.security.Security;
import java.security.cert.Certificate;
import java.nio.charset.Charset;

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

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;

import org.beigesoft.afactory.IFactoryAppBeans;
import org.beigesoft.ajetty.crypto.CryptoService;

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
  implements ActionListener {

  /**
   * <p>Factory app-beans - only for WEB-app class loader.</p>
   **/
  private final IFactoryAppBeans factoryAppBeans;

  /**
   * <p>Bootstrap.</p>
   **/
  private final BootStrapEmbeddedHttps bootStrapEmbeddedHttps;

  //Interface:
  /**
   * <p>Label A-Jetty instance number.</p>
   **/
  private final JLabel lbAjettyIn;

  /**
   * <p>A-Jetty instance number.</p>
   **/
  private final JFormattedTextField ftfAjettyIn;

  /**
   * <p>Label keystore password.</p>
   **/
  private final JLabel lbKeystorePw;

  /**
   * <p>Keystore password.</p>
   **/
  private final JPasswordField pfKeystorePw;

  /**
   * <p>Label keystore password conformation.</p>
   **/
  private final JLabel lbKeystorePwc;

  /**
   * <p>A-Jetty instance password conformation.</p>
   **/
  private final JPasswordField pfKeystorePwc;

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
   * <p>A-Jetty instance number.</p>
   **/
  private Integer ajettyIn;

  /**
   * <p>Flag is keystore created.</p>
   **/
  private boolean isKeystoreCreated;

  /**
   * <p>Crypto service.</p>
   **/
  private CryptoService cryptoService;

  /**
   * <p>Only constructor.</p>
   * @throws Exception any
   **/
  public BootStrapEmbeddedHttpsSwing() throws Exception {
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    // Interface:
    URL iconURL = getClass().getResource("/favicon.png");
    ImageIcon icon = new ImageIcon(iconURL);
    setIconImage(icon.getImage());
    this.messages = ResourceBundle.getBundle("MessagesAjetty");
    setTitle(getMsg("title"));
    GridLayout layout = new GridLayout(12, 1);
    layout.setVgap(10);
    setLayout(layout);
    this.lbAjettyIn = new JLabel(getMsg("AJettyIn"));
    add(this.lbAjettyIn);
    this.ftfAjettyIn = new JFormattedTextField(NumberFormat
      .getIntegerInstance());
    add(this.ftfAjettyIn);
    this.lbKeystorePw = new JLabel(getMsg("KeystorePw"));
    add(this.lbKeystorePw);
    this.pfKeystorePw = new JPasswordField();
    add(this.pfKeystorePw);
    this.lbKeystorePwc = new JLabel(getMsg("KeystorePwc"));
    add(this.lbKeystorePwc);
    this.pfKeystorePwc = new JPasswordField();
    add(this.pfKeystorePwc);
    this.lbPort = new JLabel(getMsg("port"));
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
      .intValue(), 500);
    setPreferredSize(new Dimension(width, height));
    pack();
    setLocationRelativeTo(null);
    // A-Jetty:
    this.factoryAppBeans = new FactoryAppBeansEmbedded();
    String appDir = null;
    try {
      File jarBoot = new File(BootStrapEmbeddedHttpsSwing.class
        .getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
      appDir = jarBoot.getParentFile().getPath();
    } catch (Exception e) {
      e.printStackTrace();
    }
    if (appDir == null) {
      appDir = System.getProperty("user.dir");
    }
    String webAppPath = appDir + File.separator + "webapp";
    File webappdir = new File(webAppPath);
    if (!webappdir.exists() || !webappdir.isDirectory()) {
      throw new Exception("Web app directory not found: " + webAppPath);
    }
    // keystore placed into [webappdir-parent]/ks folder:
    File ksDir = new File(appDir + File.separator + "ks");
    if (!ksDir.exists() && !ksDir.mkdir()) {
      throw new Exception("Can't create ks directory: " + ksDir);
    }
    File[] lstFl = ksDir.listFiles();
    String nmpref = "ajettykeystore.";
    if (lstFl != null) {
      if (lstFl.length > 1
        || lstFl.length == 1 && !lstFl[0].isFile()) {
        throw new Exception("KS directory must contains only ks file!!!");
      } else if (lstFl.length == 1 && lstFl[0].isFile()
        && lstFl[0].getName().startsWith(nmpref)) {
        String ajettyInStr = lstFl[0].getName().replace(nmpref, "");
        this.ajettyIn = Integer.parseInt(ajettyInStr);
        this.isKeystoreCreated = true;
      }
    }
    this.bootStrapEmbeddedHttps = new BootStrapEmbeddedHttps();
    this.bootStrapEmbeddedHttps.setFactoryAppBeans(this.factoryAppBeans);
    this.bootStrapEmbeddedHttps.setWebAppPath(webAppPath);
    Security.addProvider(new BouncyCastleProvider());
    refreshUi();
  }

  @Override
  public final void actionPerformed(final ActionEvent pAe) {
    try {
      if (pAe.getSource() == this.btStart
        && this.bootStrapEmbeddedHttps.getServer() == null) {
        if (!this.isActionPerforming) {
          this.isActionPerforming = true;
          if (!this.isKeystoreCreated) {
            Long ajl = (Long) ftfAjettyIn.getValue();
            if (ajl != null) {
              try {
                this.ajettyIn = Integer.parseInt(ajl.toString());
              } catch (Exception e) {
                e.printStackTrace();
              }
            }
          }
          if (this.ajettyIn == null) {
            JOptionPane.showMessageDialog(this, getMsg("EnterAjettyNumber"),
              getMsg("error"), JOptionPane.ERROR_MESSAGE);
            this.isActionPerforming = false;
            return;
          }
          startAjetty();
        }
      } else if (pAe.getSource() == this.btStop
        && this.bootStrapEmbeddedHttps.getIsStarted()) {
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
   * <p>It starts A-Jetty.</p>
   * @throws Exception an Exception
   **/
  public final void startAjetty() throws Exception {
    char[] ksPassword = this.pfKeystorePw.getPassword();
    File webappdir = new File(this.bootStrapEmbeddedHttps
      .getWebAppPath());
    String ksPath = webappdir.getParentFile()
      .getAbsolutePath() + File.separator + "ks";
    File pks12File = new File(ksPath + File.separator
        + "ajettykeystore." + this.ajettyIn);
    KeyStore pkcs12Store = null;
    if (!pks12File.exists()) {
      char[] ksPasswordc = this.pfKeystorePwc.getPassword();
      boolean noMatch = false;
      if (ksPassword.length != ksPasswordc.length) {
        noMatch = true;
      } else {
        for (int i = 0; i < ksPassword.length; i++) {
          if (ksPassword[i] != ksPasswordc[i]) {
            noMatch = true;
            break;
          }
        }
      }
      if (noMatch) {
        JOptionPane.showMessageDialog(this, getMsg("PasswordRepeatNoMatch"),
          getMsg("error"), JOptionPane.ERROR_MESSAGE);
        this.isActionPerforming = false;
        return;
      }
      String isPswStrRez = this.cryptoService.isPasswordStrong(ksPassword);
      if (isPswStrRez != null) {
        JOptionPane.showMessageDialog(this, isPswStrRez,
          getMsg("error"), JOptionPane.ERROR_MESSAGE);
        this.isActionPerforming = false;
        return;
      }
      this.cryptoService.createKeyStoreWithCredentials(pks12File
        .getParent(), this.ajettyIn, ksPassword);
      FileInputStream fis = null;
      Certificate certCa = null;
      PublicKey fileExchPub = null;
      try {
        pkcs12Store = KeyStore.getInstance("PKCS12", "BC");
        fis = new FileInputStream(pks12File);
        pkcs12Store.load(fis, ksPassword);
        this.isKeystoreCreated = true;
        certCa = pkcs12Store.getCertificate("AJettyCa" + this.ajettyIn);
        fileExchPub = pkcs12Store
          .getCertificate("AJettyFileExch" + this.ajettyIn).getPublicKey();
      } finally {
        if (fis != null) {
         try {
           fis.close();
         } catch (Exception e2) {
           e2.printStackTrace();
         }
        }
      }
      if (certCa != null) {
        File pemFl = new File(pks12File.getParentFile().getParent()
          + File.separator + "ajetty-ca.pem");
        JcaPEMWriter pemWriter = null;
        try {
          OutputStreamWriter osw = new OutputStreamWriter(
            new FileOutputStream(pemFl), Charset.forName("ASCII").newEncoder());
          pemWriter = new JcaPEMWriter(osw);
          pemWriter.writeObject(certCa);
          pemWriter.flush();
        } finally {
          if (pemWriter != null) {
            try {
              pemWriter.close();
            } catch (Exception e2) {
              e2.printStackTrace();
            }
          }
        }
      }
      if (fileExchPub != null) {
        File pubFl = new File(pks12File.getParentFile().getParent()
          + File.separator + "ajetty-file-exch" + this.ajettyIn + ".kpub");
        FileOutputStream fos = null;
        try {
          fos = new FileOutputStream(pubFl);
          fos.write(fileExchPub.getEncoded());
          fos.flush();
        } finally {
          if (fos != null) {
            try {
              fos.close();
            } catch (Exception e2) {
              e2.printStackTrace();
            }
          }
        }
      }
    } else {
      FileInputStream fis = null;
      try {
        pkcs12Store = KeyStore.getInstance("PKCS12", "BC");
        fis = new FileInputStream(pks12File);
        pkcs12Store.load(fis, ksPassword);
      } catch (Exception e) {
        pkcs12Store = null;
        e.printStackTrace();
      } finally {
        if (fis != null) {
          try {
            fis.close();
          } catch (Exception e2) {
            e2.printStackTrace();
          }
        }
      }
      if (pkcs12Store == null) {
        JOptionPane.showMessageDialog(this, getMsg("passwordDoNotMatch"),
          getMsg("error"), JOptionPane.ERROR_MESSAGE);
        this.isActionPerforming = false;
        return;
      }
    }
    this.bootStrapEmbeddedHttps.setHttpsAlias("AJettyHttps" + this.ajettyIn);
    this.bootStrapEmbeddedHttps.setPkcs12File(pks12File);
    this.bootStrapEmbeddedHttps.setPassword(new String(ksPassword));
    this.bootStrapEmbeddedHttps.setPort(
      (Integer) this.cmbPort.getSelectedItem());
    this.lastActionStartDate = new Date().getTime();
    StartThread stThread = new StartThread();
    stThread.start();
    refreshUi();
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
      this.ftfAjettyIn.setEnabled(false);
      this.pfKeystorePwc.setEnabled(false);
    } else {
      if (this.bootStrapEmbeddedHttps.getIsStarted()) {
        this.btBrowse.setText("https://localhost:"
        + this.cmbPort.getSelectedItem());
        this.pfKeystorePw.setEnabled(false);
        this.ftfAjettyIn.setEnabled(false);
        this.pfKeystorePwc.setEnabled(false);
        this.cmbPort.setEnabled(false);
        this.btStart.setEnabled(false);
        this.btStop.setEnabled(true);
        this.btBrowse.setEnabled(true);
      } else {
        if (this.isKeystoreCreated) {
          this.ftfAjettyIn.setEnabled(false);
          this.pfKeystorePwc.setEnabled(false);
          this.ftfAjettyIn.setText(this.ajettyIn.toString());
        } else {
          this.ftfAjettyIn.setEnabled(true);
          this.pfKeystorePwc.setEnabled(true);
        }
        this.btBrowse.setText(getMsg("stopped"));
        this.pfKeystorePw.setEnabled(true);
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
        BootStrapEmbeddedHttpsSwing bses = null;
        try {
          bses = new BootStrapEmbeddedHttpsSwing();
          bses.setVisible(true);
          bses.setCryptoService(new CryptoService());
        } catch (Exception ex) {
          ex.printStackTrace();
          if (bses != null) {
            bses.dispose();
          }
          System.exit(0);
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
      if (BootStrapEmbeddedHttpsSwing.this.isActionPerforming) {
        long currDate = new Date().getTime();
        if (currDate - BootStrapEmbeddedHttpsSwing
          .this.lastActionStartDate > 120000) {
          BootStrapEmbeddedHttpsSwing.this.isActionPerforming = false;
        }
      }
      if (!BootStrapEmbeddedHttpsSwing.this.isActionPerforming) {
        if (BootStrapEmbeddedHttpsSwing.this
          .bootStrapEmbeddedHttps.getServer() != null) {
          try {
            BootStrapEmbeddedHttpsSwing.this
              .bootStrapEmbeddedHttps.stopServer();
            BootStrapEmbeddedHttpsSwing.this.refreshUi();
          } catch (Exception ex) {
            ex.printStackTrace();
          }
        }
        BootStrapEmbeddedHttpsSwing.this.setVisible(false);
        BootStrapEmbeddedHttpsSwing.this.dispose();
        System.exit(0); // do not exit without it
      }
    }
  };


  /**
   * <p>Thread to start A-Jetty.</p>
   */
  private class StartThread extends Thread {

    @Override
    public void run() {
      if (BootStrapEmbeddedHttpsSwing
        .this.bootStrapEmbeddedHttps.getServer() == null) {
        try {
          BootStrapEmbeddedHttpsSwing.this.bootStrapEmbeddedHttps.startServer();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
      BootStrapEmbeddedHttpsSwing.this.isActionPerforming = false;
      BootStrapEmbeddedHttpsSwing.this.refreshUi();
    }
  };

  /**
   * <p>Thread to stop A-Jetty.</p>
   */
  private class StopThread extends Thread {

    @Override
    public void run() {
      if (BootStrapEmbeddedHttpsSwing.this
        .bootStrapEmbeddedHttps.getServer() != null) {
        try {
          BootStrapEmbeddedHttpsSwing.this.bootStrapEmbeddedHttps.stopServer();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
      BootStrapEmbeddedHttpsSwing.this.isActionPerforming = false;
      BootStrapEmbeddedHttpsSwing.this.refreshUi();
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
   * <p>Getter for BootStrapEmbeddedHttps.</p>
   * @return final
   **/
  public final BootStrapEmbeddedHttps getBootStrapEmbeddedHttps() {
    return this.bootStrapEmbeddedHttps;
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
  /**
   * <p>Getter for ajettyIn.</p>
   * @return Integer
   **/
  public final Integer getAjettyIn() {
    return this.ajettyIn;
  }

  /**
   * <p>Setter for ajettyIn.</p>
   * @param pAjettyIn reference
   **/
  public final void setAjettyIn(final Integer pAjettyIn) {
    this.ajettyIn = pAjettyIn;
  }

  /**
   * <p>Getter for cryptoService.</p>
   * @return CryptoService
   **/
  public final CryptoService getCryptoService() {
    return this.cryptoService;
  }

  /**
   * <p>Setter for cryptoService.</p>
   * @param pCryptoService reference
   **/
  public final void setCryptoService(final CryptoService pCryptoService) {
    this.cryptoService = pCryptoService;
  }
}
