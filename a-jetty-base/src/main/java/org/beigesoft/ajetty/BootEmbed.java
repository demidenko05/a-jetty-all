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

package org.beigesoft.ajetty;

import java.io.File;
import java.security.KeyStore;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.security.DataBaseLoginService;
import org.eclipse.jetty.util.ssl.SslContextFactory;

import org.beigesoft.afactory.IFactoryAppBeans;

/**
 * <p>
 * BootEmbed for A-Jetty configured as minimum server
 * with precompiled together WebApp with WEB-INF/web.xml, index.html, all other
 * static files(css, js) that exists on given path, by default path is webapp.
 * This is means that all classes A-Jetty
 * and web application must be in same jar/dex file.
 * </p>
 *
 * @author Yury Demidenko
 */
public class BootEmbed {

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
   * <p>Crypto provider name.</p>
   **/
  private String cryptoProviderName = "BC";

  /**
   * <p>Key-store.</p>
   **/
  private KeyStore keyStore;

  /**
   * <p>A-Jetty instance number.</p>
   **/
  private Integer ajettyIn;

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
      sslContextFactory.setKeyStoreProvider(this.cryptoProviderName);
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
      connector.setHost("127.0.0.1");
      connector.setPort(this.port);
      connector.setIdleTimeout(500000);
      server.setConnectors(new Connector[] {connector});
      // without different application context path
      // authentication works badly in case of two A-Jetty instance
      // with the same web-app context path:
      this.webAppContext = new WebAppContext(webappdir
        .getAbsolutePath(), "/bsa" + this.port + "/");
      DataBaseLoginService dataBaseLoginService =
        new DataBaseLoginService("JDBCRealm");
      this.webAppContext.getSecurityHandler()
        .setLoginService(dataBaseLoginService);
      this.webAppContext.setAttribute("JDBCRealm", dataBaseLoginService);
      this.webAppContext.setFactoryAppBeans(getFactoryAppBeans());
      this.webAppContext.setDefaultsDescriptor(webappdir
        .getAbsolutePath() + File.separator + "webdefault.xml");
      this.webAppContext.setAttribute("ajettyKeystore", this.keyStore);
      this.webAppContext.setAttribute("ajettyIn", this.ajettyIn);
      this.webAppContext.setAttribute("ksPassword", this.password);
      this.server.setHandler(this.webAppContext);
    } catch (Exception e) {
      this.server = null;
      this.webAppContext = null;
      throw e;
    }
  }

  /**
   * <p>Start server.</p>
   * @throws Exception an Exception
   **/
  public final void startServer() throws Exception {
    try {
      if (this.server == null) {
        createServer();
      }
      this.server.start();
      this.isStarted = true;
    } catch (Exception e) {
      this.server = null;
      this.webAppContext = null;
    }
  }

  /**
   * <p>Stop server.</p>
   * @throws Exception an Exception
   **/
  public final void stopServer() throws Exception {
    try {
      this.server.stop();
      this.server.destroy();
    } finally {
      this.server = null;
      this.webAppContext = null;
      this.isStarted = false;
    }
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

  /**
   * <p>Getter for cryptoProviderName.</p>
   * @return String
   **/
  public final String getCryptoProviderName() {
    return this.cryptoProviderName;
  }

  /**
   * <p>Setter for cryptoProviderName.</p>
   * @param pCryptoProviderName reference
   **/
  public final void setCryptoProviderName(final String pCryptoProviderName) {
    this.cryptoProviderName = pCryptoProviderName;
  }

  /**
   * <p>Getter for keyStore.</p>
   * @return KeyStore
   **/
  public final KeyStore getKeyStore() {
    return this.keyStore;
  }

  /**
   * <p>Setter for keyStore.</p>
   * @param pKeyStore reference
   **/
  public final void setKeyStore(final KeyStore pKeyStore) {
    this.keyStore = pKeyStore;
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
}
