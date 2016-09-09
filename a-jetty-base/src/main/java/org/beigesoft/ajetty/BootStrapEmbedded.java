package org.beigesoft.ajetty;

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

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.ShutdownHandlerSimple;
import org.eclipse.jetty.server.handler.HandlerCollection;

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
   * <p>Factory app-beans.</p>
   **/
  private IFactoryAppBeans factoryAppBeans;

  /**
   * <p>Port.</p>
   **/
  private int port = 8080;

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
   * <p>Create and configure server.</p>
   * @param pIsCreateShutdownHandler - Is Create Shutdown Handler
   * @throws Exception an Exception
   **/
  public final void createServer(
    final boolean pIsCreateShutdownHandler) throws Exception {
    this.server = new Server();
    ServerConnector connector = new ServerConnector(server);
    connector.setPort(this.port);
    connector.setHost("127.0.0.1");
    server.setConnectors(new Connector[]{connector});
    File webappdir = new File(getWebAppPath());
    if (!webappdir.exists() || !webappdir.isDirectory()) {
      throw new Exception("Web app directory not found: " + getWebAppPath());
    }
    this.webAppContext = new WebAppContext(webappdir
      .getAbsolutePath(), "/");
    this.webAppContext.setFactoryAppBeans(getFactoryAppBeans());
    this.webAppContext.setDefaultsDescriptor(webappdir
      .getAbsolutePath() + File.separator + "webdefault.xml");
    if (pIsCreateShutdownHandler) {
      HandlerCollection handlers = new HandlerCollection();
      handlers.setHandlers(new Handler[] {this.webAppContext,
        new ShutdownHandlerSimple(this.server)});
      this.server.setHandler(handlers);
    } else {
      this.server.setHandler(this.webAppContext);
    }
  }


  /**
   * <p>Start server.</p>
   * @throws Exception an Exception
   **/
  public final void startServer() throws Exception {
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
      bootStrap.createServer(true);
      bootStrap.startServer(); //server started in current thread
      // stop it by send GET or POST /shutdown
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  //Simple getters and setters:
  /**
   * <p>Getter for port.</p>
   * @return int
   **/
  public final int getPort() {
    return this.port;
  }

  /**
   * <p>Setter for port.</p>
   * @param pPort reference
   **/
  public final void setPort(final int pPort) {
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
   * <p>Setter for server.</p>
   * @param pServer reference
   **/
  public final void setServer(final Server pServer) {
    this.server = pServer;
  }

  /**
   * <p>Getter for isStarted.</p>
   * @return boolean
   **/
  public final boolean getIsStarted() {
    return this.isStarted;
  }

  /**
   * <p>Setter for isStarted.</p>
   * @param pIsStarted reference
   **/
  public final void setIsStarted(final boolean pIsStarted) {
    this.isStarted = pIsStarted;
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
   * <p>Setter for webAppContext.</p>
   * @param pWebAppContext reference
   **/
  public final void setWebAppContext(final WebAppContext pWebAppContext) {
    this.webAppContext = pWebAppContext;
  }
}
