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

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.util.ssl.SslContextFactory;

import org.beigesoft.afactory.IFactoryAppBeans;

/**
 * <p>
 * BootStrapEmbedded for A-Jetty configured as minimum server
 * with precompiled together WebApp with WEB-INF/web.xml, index.html, all other
 * static files(css, js) that exists on given path, by default path is webapp.
 * This is means that all classes A-Jetty
 * and web application must be in same jar/dex file.
 * </p>
 *
 * @author Yury Demidenko
 */
public class BootStrapEmbeddedHttps {

  /**
   * <p>Factory app-beans - only for WEB-app class loader.</p>
   **/
  private IFactoryAppBeans factoryAppBeans;

  /**
   * <p>Port.</p>
   **/
  private Integer port = 8443;

  /**
   * <p>Web.</p>
   **/
  private String webAppPath = "webapp";

  /**
   * <p>Jetty.</p>
   **/
  private Server server;

  /**
   * <p>Host IP address.</p>
   **/
  private String hostAddress = "127.0.0.1";

  /**
   * <p>Webapp context.</p>
   **/
  private WebAppContext webAppContext;

  /**
   * <p>Is started.</p>
   **/
  private boolean isStarted = false;

  /**
   * <p>A-Jetty start (keystore) password.</p>
   **/
  private String password;

  /**
   * <p>Keystore file.</p>
   **/
  private File pkcs12File;

  /**
   * <p>A-Jetty start HTTPS alias.</p>
   **/
  private String httpsAlias;

  /**
   * <p>A-Jetty start HTTPS alias.</p>
   **/
  private String httpsPassword;

  /**
   * <p>Create and configure server.</p>
   * @throws Exception an Exception
   **/
  public final void createServer() throws Exception {
    try {
      File webappdir = new File(getWebAppPath());
      if (!webappdir.exists() || !webappdir.isDirectory()) {
        throw new Exception("Web app directory not found: " + getWebAppPath());
      }
      this.server = new Server();
      SslContextFactory sslContextFactory = new SslContextFactory();
      sslContextFactory.setKeyStorePath(this.pkcs12File.getAbsolutePath());
      sslContextFactory.setKeyStorePassword(this.password);
      sslContextFactory.setKeyStoreProvider("BC");
      sslContextFactory.setKeyStoreType("PKCS12");
      if (this.httpsPassword != null) {
        sslContextFactory.setKeyManagerPassword(this.httpsPassword);
      }
      sslContextFactory.setCertAlias(this.httpsAlias);
      HttpConfiguration httpsConf = new HttpConfiguration();
      httpsConf.setSecureScheme("https");
      httpsConf.setSecurePort(this.port);
      httpsConf.setOutputBufferSize(32768);
      httpsConf.addCustomizer(new SecureRequestCustomizer());
      ServerConnector connector = new ServerConnector(server,
        new SslConnectionFactory(sslContextFactory, "http/1.1"),
          new HttpConnectionFactory(httpsConf));
      connector.setHost(this.hostAddress);
      connector.setPort(this.port);
      connector.setIdleTimeout(500000);
      server.setConnectors(new Connector[] {connector});
      this.webAppContext = new WebAppContext(webappdir
        .getAbsolutePath(), "/");
      this.webAppContext.setFactoryAppBeans(getFactoryAppBeans());
      this.webAppContext.setDefaultsDescriptor(webappdir
        .getAbsolutePath() + File.separator + "webdefault.xml");
      this.server.setHandler(this.webAppContext);
    } catch (Exception e) {
      this.server = null;
      throw e;
    }
  }

  /**
   * <p>Start server.</p>
   * @throws Exception an Exception
   **/
  public final void startServer() throws Exception {
    createServer();
    this.server.start();
    this.isStarted = true;
  }

  /**
   * <p>Stop server.</p>
   * @throws Exception an Exception
   **/
  public final void stopServer() throws Exception {
    this.server.stop();
    this.server.destroy();
    this.server = null;
    this.isStarted = false;
  }

  //Simple getters and setters:
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
   * <p>Getter for server.</p>
   * @return Server
   **/
  public final Server getServer() {
    return this.server;
  }

  /**
   * <p>Getter for isStarted.</p>
   * @return boolean
   **/
  public final boolean getIsStarted() {
    return this.isStarted;
  }

  /**
   * <p>Getter for factoryAppBeans.</p>
   * @return IFactoryAppBeans
   **/
  public final IFactoryAppBeans getFactoryAppBeans() {
    return this.factoryAppBeans;
  }

  /**
   * <p>Setter for factoryAppBeans.</p>
   * @param pFactoryAppBeans reference
   **/
  public final void setFactoryAppBeans(
    final IFactoryAppBeans pFactoryAppBeans) {
    this.factoryAppBeans = pFactoryAppBeans;
  }

  /**
   * <p>Getter for webAppPath.</p>
   * @return String
   **/
  public final String getWebAppPath() {
    return this.webAppPath;
  }

  /**
   * <p>Setter for webAppPath.</p>
   * @param pWebAppPath reference
   **/
  public final void setWebAppPath(final String pWebAppPath) {
    this.webAppPath = pWebAppPath;
  }

  /**
   * <p>Getter for webAppContext.</p>
   * @return WebAppContext
   **/
  public final WebAppContext getWebAppContext() {
    return this.webAppContext;
  }

  /**
   * <p>Getter for hostAddress.</p>
   * @return String
   **/
  public final String getHostAddress() {
    return this.hostAddress;
  }

  /**
   * <p>Setter for hostAddress.</p>
   * @param pHostAddress reference
   **/
  public final void setHostAddress(final String pHostAddress) {
    this.hostAddress = pHostAddress;
  }

  /**
   * <p>Getter for password.</p>
   * @return String
   **/
  public final String getPassword() {
    return this.password;
  }

  /**
   * <p>Setter for password.</p>
   * @param pPassword reference
   **/
  public final void setPassword(final String pPassword) {
    this.password = pPassword;
  }


  /**
   * <p>Getter for pkcs12File.</p>
   * @return File
   **/
  public final File getPkcs12File() {
    return this.pkcs12File;
  }

  /**
   * <p>Setter for pkcs12File.</p>
   * @param pPkcs12File reference
   **/
  public final void setPkcs12File(final File pPkcs12File) {
    this.pkcs12File = pPkcs12File;
  }

  /**
   * <p>Getter for httpsAlias.</p>
   * @return String
   **/
  public final String getHttpsAlias() {
    return this.httpsAlias;
  }

  /**
   * <p>Setter for httpsAlias.</p>
   * @param pHttpsAlias reference
   **/
  public final void setHttpsAlias(final String pHttpsAlias) {
    this.httpsAlias = pHttpsAlias;
  }

  /**
   * <p>Getter for httpsPassword.</p>
   * @return String
   **/
  public final String getHttpsPassword() {
    return this.httpsPassword;
  }

  /**
   * <p>Setter for httpsPassword.</p>
   * @param pHttpsPassword reference
   **/
  public final void setHttpsPassword(final String pHttpsPassword) {
    this.httpsPassword = pHttpsPassword;
  }
}
