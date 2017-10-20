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

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.webapp.WebAppContext;

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
public class BootStrapEmbedded {

  /**
   * <p>Factory app-beans - only for WEB-app class loader.</p>
   **/
  private IFactoryAppBeans factoryAppBeans;

  /**
   * <p>Port.</p>
   **/
  private Integer port = 8080;

  /**
   * <p>Web.</p>
   **/
  private String webAppPath = "webapp";

  /**
   * <p>Jetty.</p>
   **/
  private Server server;

  /**
   * <p>Jetty connector.</p>
   **/
  private ServerConnector connector;

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
   * <p>Create and configure server.</p>
   * @throws Exception an Exception
   **/
  public final void createServer() throws Exception {
    this.server = new Server();
    this.connector = new ServerConnector(server);
    this.connector.setHost(this.hostAddress);
    this.server.setConnectors(new Connector[] {this.connector});
    File webappdir = new File(getWebAppPath());
    if (!webappdir.exists() || !webappdir.isDirectory()) {
      throw new Exception("Web app directory not found: " + getWebAppPath());
    }
    this.webAppContext = new WebAppContext(webappdir
      .getAbsolutePath(), "/");
    this.webAppContext.setFactoryAppBeans(getFactoryAppBeans());
    this.webAppContext.setDefaultsDescriptor(webappdir
      .getAbsolutePath() + File.separator + "webdefault.xml");
    this.server.setHandler(this.webAppContext);
  }


  /**
   * <p>Start server.</p>
   * @throws Exception an Exception
   **/
  public final void startServer() throws Exception {
    this.connector.setPort(this.port);
    this.server.start();
    this.isStarted = true;
  }

  /**
   * <p>Stop server.</p>
   * @throws Exception an Exception
   **/
  public final void stopServer() throws Exception {
    this.server.stop();
    this.isStarted = false;
  }

  /**
   * <p>This start preconfigured Jetty on non-Android OS.
   * It may takes up to tho parameters: port and webAppPath.
   * Example:
   * <pre>
   * java -jar a-jetty-base.jar
   * or
   * java -jar a-jetty-base.jar webAppPath=../my/webcrm
   * or
   * java -jar a-jetty-base.jar webAppPath=../my/webcrm port=8080
   * </pre>
   * </p>
   * @param pArgs arguments
   **/
  public static final void main(final String[] pArgs) {
    try {
      BootStrapEmbedded bootStrap = new BootStrapEmbedded();
      for (String arg : pArgs) {
        if (arg.contains("port=")) {
          String strPort = arg.replace("port=", "").trim();
          bootStrap.setPort(Integer.parseInt(strPort));
        } else if (arg.contains("webAppPath=")) {
          bootStrap.setWebAppPath(arg.replace("webAppPath=", "").trim());
        }
      }
      bootStrap.setFactoryAppBeans(new FactoryAppBeansEmbedded());
      //Only for standard JAVA:
      bootStrap.createServer();
      bootStrap.startServer(); //server started in current thread
    } catch (Exception e) {
      e.printStackTrace();
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
   * <p>Getter for connector.</p>
   * @return ServerConnector
   **/
  public final ServerConnector getConnector() {
    return this.connector;
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
}
