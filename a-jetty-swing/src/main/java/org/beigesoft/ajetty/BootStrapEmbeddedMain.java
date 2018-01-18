package org.beigesoft.ajetty;

/*
 * Copyright (c) 2018 Beigesoft ™
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
import java.security.PublicKey;
import java.security.KeyStore;
import java.security.Security;
import java.security.cert.Certificate;
import java.nio.charset.Charset;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;

import org.beigesoft.afactory.IFactoryAppBeans;
import org.beigesoft.ajetty.crypto.CryptoService;
import org.beigesoft.log.ILogger;
import org.beigesoft.log.LoggerFile;

/**
 * <p>
 * BootStrapEmbeddedHttpsIFacelaunches A-Jetty as host 127.0.0.1 on opted port
 * and uses HTTPS connector,
 * for only WEB-application in <b>webapp</b> folder
 * For internationalization it expects overridden MessagesAjetty.properties
 * file. There are two interfaces - SWING and command line (CLI).
 * Add "cli" argument to start CLI,
 * i.e. "java -jar BootStrapEmbeddedMain.jar cli"
 * </p>
 *
 * @author Yury Demidenko
 */
public class BootStrapEmbeddedMain {

  /**
   * <p>Factory app-beans - only for WEB-app class loader.</p>
   **/
  private final IFactoryAppBeans factoryAppBeans;

  /**
   * <p>Bootstrap.</p>
   **/
  private final BootStrapEmbeddedHttps bootStrapEmbeddedHttps;

  /**
   * <p>I18N.</p>
   **/
  private ResourceBundle messages;

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
   * <p>A-Jetty port.</p>
   **/
  private Integer port = 8443;

  /**
   * <p>Flag is keystore created.</p>
   **/
  private boolean isKeystoreCreated;

  /**
   * <p>Crypto service.</p>
   **/
  private CryptoService cryptoService;

  /**
   * <p>Logger.</p>
   **/
  private ILogger logger;

  /**
   * <p>Is debug.</p>
   **/
  private boolean isDebug = false;

  /**
   * <p>Key-store.</p>
   **/
  private KeyStore keyStore;

  /**
   * <p>Key-store password.</p>
   **/
  private char[] ksPassword;

  /**
   * <p>Key-store password confirm.</p>
   **/
  private char[] ksPasswordConf;

  /**
   * <p>Swing interface.</p>
   **/
  private IBootStrapIFace bootStrapIFace;

  /**
   * <p>Is last start fail.</p>
   **/
  private boolean isLastStartFail = false;

  /**
   * <p>Only constructor.</p>
   * @throws ExceptionStart ExceptionStart
   **/
  public BootStrapEmbeddedMain() throws ExceptionStart {
    String appDir = null;
    try {
      File jarBoot = new File(BootStrapEmbeddedMain.class
        .getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
      appDir = jarBoot.getParent();
      this.messages = ResourceBundle.getBundle("MessagesAjetty");
    } catch (Exception e) {
      e.printStackTrace();
    }
    if (appDir == null) {
      appDir = System.getProperty("user.dir");
    }
    if (this.logger == null) {
      LoggerFile log = new LoggerFile();
      log.setFilePath(appDir + File.separator + "starter");
      log.setIsCloseFileAfterRecord(true);
      this.logger = log;
    }
    try {
      java.util.List<String> arguments = java.lang.management.ManagementFactory
        .getRuntimeMXBean().getInputArguments();
      for (String str : arguments) {
        if (str.contains("jdwp") || str.contains("debug")) {
          this.isDebug = true;
        }
      }
    } catch (Exception e) {
      this.logger.warn(null, BootStrapEmbeddedMain.class,
        "IS DEBUG CHECKING: ", e);
    }
    try {
      if (this.cryptoService == null) {
        setCryptoService(new CryptoService());
      }
      // A-Jetty:
      this.factoryAppBeans = new FactoryAppBeansEmbedded();
      String webAppPath = appDir + File.separator + "webapp";
      File webappdir = new File(webAppPath);
      if (!webappdir.exists() || !webappdir.isDirectory()) {
        throw new ExceptionStart("Web app directory not found: " + webAppPath);
      }
      // keystore placed into [webappdir-parent]/ks folder:
      File ksDir = new File(appDir + File.separator + "ks");
      if (!ksDir.exists() && !ksDir.mkdir()) {
        throw new ExceptionStart("Can't create ks directory: " + ksDir);
      }
      File[] lstFl = ksDir.listFiles();
      String nmpref = "ajettykeystore.";
      if (lstFl != null) {
        if (lstFl.length > 1
          || lstFl.length == 1 && !lstFl[0].isFile()) {
          throw new ExceptionStart(
            "KS directory must contains only ks file!!!");
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
    } catch (ExceptionStart e) {
      this.logger.error(null, BootStrapEmbeddedMain.class, null, e);
      throw e;
    } catch (Exception e) {
      this.logger.error(null, BootStrapEmbeddedMain.class, null, e);
      throw new ExceptionStart(e);
    }
  }

  /**
   * <p>It stops A-Jetty.</p>
   * @throws ExceptionStart ExceptionStart
   **/
  public final void stopAjetty() throws ExceptionStart {
    try {
      StopThread stThread = new StopThread();
      stThread.start();
    } catch (Exception e) {
      this.logger.error(null, BootStrapEmbeddedMain.class, null, e);
      throw new ExceptionStart(e);
    }
  }

  /**
   * <p>It starts A-Jetty.</p>
   * @throws Exception an Exception
   **/
  public final void startAjetty() throws Exception {
    File webappdir = new File(this.bootStrapEmbeddedHttps
      .getWebAppPath());
    String ksPath = webappdir.getParent() + File.separator + "ks";
    File pks12File = new File(ksPath + File.separator
      + "ajettykeystore." + this.ajettyIn);
    if (!pks12File.exists()) {
      boolean noMatch = false;
      if (this.ksPassword.length != this.ksPasswordConf.length) {
        noMatch = true;
      } else {
        for (int i = 0; i < this.ksPassword.length; i++) {
          if (this.ksPassword[i] != this.ksPasswordConf[i]) {
            noMatch = true;
            break;
          }
        }
      }
      if (noMatch) {
        this.bootStrapIFace.showError(getMsg("PasswordRepeatNoMatch"));
        this.isActionPerforming = false;
        return;
      }
      String isPswStrRez = this.cryptoService
        .isPasswordStrong(this.ksPassword);
      if (isPswStrRez != null) {
        this.bootStrapIFace.showError(isPswStrRez);
        this.isActionPerforming = false;
        return;
      }
      this.cryptoService.createKeyStoreWithCredentials(pks12File
        .getParent(), this.ajettyIn, this.ksPassword);
      FileInputStream fis = null;
      Certificate certCa = null;
      PublicKey fileExchPub = null;
      try {
        this.keyStore = KeyStore.getInstance("PKCS12", "BC");
        fis = new FileInputStream(pks12File);
        this.keyStore.load(fis, this.ksPassword);
        this.isKeystoreCreated = true;
        certCa = this.keyStore.getCertificate("AJettyCa" + this.ajettyIn);
        fileExchPub = this.keyStore
          .getCertificate("AJettyFileExch" + this.ajettyIn).getPublicKey();
      } finally {
        if (fis != null) {
          try {
            fis.close();
          } catch (Exception e2) {
            this.logger
              .error(null, BootStrapEmbeddedMain.class, null, e2);
          }
        }
      }
      if (certCa != null) {
        File pemFl = new File(pks12File.getParentFile().getParent()
          + File.separator + "ajetty-ca.pem");
        JcaPEMWriter pemWriter = null;
        try {
          OutputStreamWriter osw = new OutputStreamWriter(
            new FileOutputStream(pemFl),
              Charset.forName("ASCII").newEncoder());
          pemWriter = new JcaPEMWriter(osw);
          pemWriter.writeObject(certCa);
          pemWriter.flush();
        } finally {
          if (pemWriter != null) {
            try {
              pemWriter.close();
            } catch (Exception e2) {
              this.logger
                .error(null, BootStrapEmbeddedMain.class, null, e2);
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
              this.logger
                .error(null, BootStrapEmbeddedMain.class, null, e2);
            }
          }
        }
      }
    } else {
      FileInputStream fis = null;
      try {
        this.keyStore = KeyStore.getInstance("PKCS12", "BC");
        fis = new FileInputStream(pks12File);
        this.keyStore.load(fis, this.ksPassword);
      } catch (Exception e) {
        this.keyStore = null;
        this.logger.error(null, BootStrapEmbeddedMain.class, null, e);
      } finally {
        if (fis != null) {
          try {
            fis.close();
          } catch (Exception e2) {
            this.logger
              .error(null, BootStrapEmbeddedMain.class, null, e2);
          }
        }
      }
      if (this.keyStore == null) {
        this.bootStrapIFace.showError(getMsg("passwordDoNotMatch"));
        this.isActionPerforming = false;
        this.isLastStartFail = true;
        return;
      }
    }
    this.bootStrapEmbeddedHttps.setHttpsAlias("AJettyHttps" + this.ajettyIn);
    this.bootStrapEmbeddedHttps.setPkcs12File(pks12File);
    this.bootStrapEmbeddedHttps.setPassword(new String(this.ksPassword));
    this.bootStrapEmbeddedHttps.setKeyStore(this.keyStore);
    this.bootStrapEmbeddedHttps.setAjettyIn(this.ajettyIn);
    this.bootStrapEmbeddedHttps.setPort(this.port);
    this.lastActionStartDate = new Date().getTime();
    StartThread stThread = new StartThread();
    stThread.start();
    this.isLastStartFail = false;
  }

  /**
   * <p>Refresh user interface.</p>
   **/
  public final void refreshUi() {
    this.bootStrapIFace.refreshUi();
  }

  /**
   * <p>This start A-Jetty with either SWING interface or CLI.</p>
   * @param pArgs arguments - use "cli" to start CLI
   **/
  public static final void main(final String[] pArgs) {
    try {
      final BootStrapEmbeddedMain bsem = new BootStrapEmbeddedMain();
      if (pArgs != null && pArgs.length > 0 && "cli".equals(pArgs[0])) {
        //command line interface:
        BootStrapCli bsc = new BootStrapCli();
        bsc.setMainBootStrap(bsem);
        bsem.setBootStrapIFace(bsc);
        bsc.startInterface();
      } else {
        java.awt.EventQueue.invokeLater(new Runnable() {
          public void run() {
            BootStrapEmbeddedHttpsSwing bses = null;
            try {
              bses = new BootStrapEmbeddedHttpsSwing();
              bses.setMainBootStrap(bsem);
              bsem.setBootStrapIFace(bses);
              bses.startInterface();
            } catch (Exception e) {
              if (bses != null) {
                bses.dispose();
              }
            }
          }
        });
      }
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
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
   * <p>Command line interface.</p>
   */
  private class CliThread extends Thread {

    @Override
    public void run() {
      if (!BootStrapEmbeddedMain
        .this.bootStrapEmbeddedHttps.getIsStarted()) {
        try {
          BootStrapEmbeddedMain.this.bootStrapEmbeddedHttps.startServer();
        } catch (Exception e) {
          BootStrapEmbeddedMain.this.logger
            .error(null, BootStrapEmbeddedMain.class, null, e);
        }
      }
    }
  };

  /**
   * <p>Thread to start A-Jetty.</p>
   */
  private class StartThread extends Thread {

    @Override
    public void run() {
      if (!BootStrapEmbeddedMain
        .this.bootStrapEmbeddedHttps.getIsStarted()) {
        try {
          BootStrapEmbeddedMain.this.bootStrapEmbeddedHttps.startServer();
        } catch (Exception e) {
          BootStrapEmbeddedMain.this.logger
            .error(null, BootStrapEmbeddedMain.class, null, e);
        }
      }
      BootStrapEmbeddedMain.this.isActionPerforming = false;
      BootStrapEmbeddedMain.this.refreshUi();
    }
  };

  /**
   * <p>Thread to stop A-Jetty.</p>
   */
  private class StopThread extends Thread {

    @Override
    public void run() {
      if (BootStrapEmbeddedMain.this
        .bootStrapEmbeddedHttps.getIsStarted()) {
        try {
          BootStrapEmbeddedMain.this.bootStrapEmbeddedHttps.stopServer();
        } catch (Exception e) {
          BootStrapEmbeddedMain.this.logger
            .error(null, BootStrapEmbeddedMain.class, null, e);
        }
      }
      BootStrapEmbeddedMain.this.isActionPerforming = false;
      BootStrapEmbeddedMain.this.refreshUi();
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
   * <p>Setter for isActionPerforming.</p>
   * @param pIsActionPerforming reference
   **/
  public final void setIsActionPerforming(final boolean pIsActionPerforming) {
    this.isActionPerforming = pIsActionPerforming;
  }

  /**
   * <p>Getter for lastActionStartDate.</p>
   * @return long
   **/
  public final long getLastActionStartDate() {
    return this.lastActionStartDate;
  }

  /**
   * <p>Setter for lastActionStartDate.</p>
   * @param pLastActionStartDate reference
   **/
  public final void setLastActionStartDate(final long pLastActionStartDate) {
    this.lastActionStartDate = pLastActionStartDate;
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
   * <p>Getter for messages.</p>
   * @return ResourceBundle
   **/
  public final ResourceBundle getMessages() {
    return this.messages;
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

  /**
   * <p>Getter for logger.</p>
   * @return ILogger
   **/
  public final ILogger getLogger() {
    return this.logger;
  }

  /**
   * <p>Setter for logger.</p>
   * @param pLogger reference
   **/
  public final void setLogger(final ILogger pLogger) {
    this.logger = pLogger;
  }

  /**
   * <p>Getter for isDebug.</p>
   * @return boolean
   **/
  public final boolean getIsDebug() {
    return this.isDebug;
  }

  /**
   * <p>Getter for isKeystoreCreated.</p>
   * @return boolean
   **/
  public final boolean getIsKeystoreCreated() {
    return this.isKeystoreCreated;
  }

  /**
   * <p>Getter for port.</p>
   * @return Integer
   **/
  public final Integer getPort() {
    return this.port;
  }

  /**
   * <p>Setter for port.</p>
   * @param pPort reference
   **/
  public final void setPort(final Integer pPort) {
    this.port = pPort;
  }


  /**
   * <p>Getter for ksPassword.</p>
   * @return char[]
   **/
  public final char[] getKsPassword() {
    return this.ksPassword;
  }

  /**
   * <p>Setter for ksPassword.</p>
   * @param pKsPassword reference
   **/
  public final void setKsPassword(final char[] pKsPassword) {
    this.ksPassword = pKsPassword;
  }

  /**
   * <p>Getter for ksPasswordConf.</p>
   * @return char[]
   **/
  public final char[] getKsPasswordConf() {
    return this.ksPasswordConf;
  }

  /**
   * <p>Setter for ksPasswordConf.</p>
   * @param pKsPasswordConf reference
   **/
  public final void setKsPasswordConf(final char[] pKsPasswordConf) {
    this.ksPasswordConf = pKsPasswordConf;
  }

  /**
   * <p>Getter for bootStrapIFace.</p>
   * @return IBootStrapIFace
   **/
  public final IBootStrapIFace getBootStrapIFace() {
    return this.bootStrapIFace;
  }

  /**
   * <p>Setter for bootStrapIFace.</p>
   * @param pBootStrapIFace reference
   **/
  public final void setBootStrapIFace(final IBootStrapIFace pBootStrapIFace) {
    this.bootStrapIFace = pBootStrapIFace;
  }

  /**
   * <p>Getter for isLastStartFail.</p>
   * @return boolean
   **/
  public final boolean getIsLastStartFail() {
    return this.isLastStartFail;
  }

  /**
   * <p>Setter for isLastStartFail.</p>
   * @param pIsLastStartFail reference
   **/
  public final void setIsLastStartFail(final boolean pIsLastStartFail) {
    this.isLastStartFail = pIsLastStartFail;
  }
}
