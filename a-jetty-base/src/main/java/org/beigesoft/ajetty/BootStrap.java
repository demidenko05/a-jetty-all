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

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.deploy.DeploymentManager;
import org.eclipse.jetty.deploy.providers.WebAppProvider;
import org.eclipse.jetty.deploy.PropertiesConfigurationManager;

import org.beigesoft.afactory.IFactoryAppBeans;

/**
 * <p>
 * BootStrap for Jetty for Android configured as minimum server
 * with WebAppDeployer that deploy ordinal non-JSP web-app
 * (JSP/JSTL must be precompiled into servlets) that must be unpacked
 * into [jetty-base]/webapps.
 * It must be follow working directory(jetty:base) containing:
 * <pre>
 * webdefault.xml
 * webapps
 * </pre>
 * </p>
 *
 * @author Yury Demidenko
 */
public class BootStrap {

  /**
   * <p>Factory app-beans.</p>
   **/
  private IFactoryAppBeans factoryAppBeans;

  /**
   * <p>Port.</p>
   **/
  private Integer port = 8080;

  /**
   * <p>Jetty base.</p>
   **/
  private String jettyBase = "";

  /**
   * <p>Jetty.</p>
   **/
  private Server server;

  /**
   * <p>Is started.</p>
   **/
  private boolean isStarted = false;

  /**
   * <p>Deployment Manager.</p>
   **/
  private DeploymentManager deploymentManager;

  /**
   * <p>Jetty connector.</p>
   **/
  private ServerConnector connector;

  /**
   * <p>Host IP address.</p>
   **/
  private String hostAddress = "127.0.0.1";

  /**
   * <p>Create and configure server.</p>
   * @throws Exception an Exception
   **/
  public final void createServer() throws Exception {
    // Create a basic jetty server object that will listen on port 8080.
    // Note that if you set this to port 0 then a randomly available port
    // will be assigned that you can either look in the logs for the port,
    // or programmatically obtain it for use in test cases.
    this.server = new Server();
    this.connector = new ServerConnector(server);
    this.connector.setPort(this.port);
    this.connector.setHost(this.hostAddress);
    this.server.setConnectors(new Connector[] {this.connector});
    // Handlers:
    HandlerCollection handlers = new HandlerCollection();
    DefaultHandler defaultHandler = new DefaultHandler();
    ContextHandlerCollection contextHandlerCollection =
      new ContextHandlerCollection();
    handlers.setHandlers(new Handler[] {contextHandlerCollection,
      defaultHandler }); // !!! defaultHandler must be second
    this.server.setHandler(handlers);
    // Create the deployment manager:
    this.deploymentManager = new DeploymentManager();
    this.deploymentManager.setContexts(contextHandlerCollection);
    WebAppProvider webAppProvider = new WebAppProvider();
    webAppProvider.setFactoryAppBeans(this.factoryAppBeans);
    webAppProvider.setMonitoredDirName(jettyBase + File.separator + "webapps");
    webAppProvider.setDefaultsDescriptor(jettyBase + File.separator
      + "webdefault.xml");
    webAppProvider.setExtractWars(false);
    PropertiesConfigurationManager confManager =
      new PropertiesConfigurationManager();
    webAppProvider.setConfigurationManager(confManager);
    this.deploymentManager.addAppProvider(webAppProvider);
    this.server.addBean(deploymentManager);
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
   * It may takes up to tho parameters: port and jetty:base.
   * Example:
   * <pre>
   * java -jar a-jetty-base.jar jetty:base=/home/my/a-jetty
   * or
   * java -jar a-jetty-base.jar jetty:base=/home/my/a-jetty port=8080
   * </pre>
   * </p>
   * @param pArgs arguments
   **/
  public static final void main(final String[] pArgs) {
    try {
      BootStrap bootStrap = new BootStrap();
      for (String arg : pArgs) {
        if (arg.contains("port=")) {
          String strPort = arg.replace("port=", "").trim();
          bootStrap.setPort(Integer.parseInt(strPort));
        } else if (arg.contains("jetty:base=")) {
          bootStrap.setJettyBase(arg.replace("jetty:base=", "").trim());
        }
      }
      bootStrap.setFactoryAppBeans(new FctApp());
      bootStrap.createServer();
      bootStrap.startServer();
      // The use of server.join() the will make the current thread join and
      // wait until the server is done executing.
      // See http://docs.oracle.com/javase/7/docs/api/java/lang/Thread.html#join
      bootStrap.getServer().join();
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
   * <p>Getter for jettyBase.</p>
   * @return String
   **/
  public final String getJettyBase() {
    return this.jettyBase;
  }

  /**
   * <p>Setter for jettyBase.</p>
   * @param pJettyBase reference
   **/
  public final void setJettyBase(final String pJettyBase) {
    this.jettyBase = pJettyBase;
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
   * <p>Getter for deploymentManager.</p>
   * @return DeploymentManager
   **/
  public final DeploymentManager getDeploymentManager() {
    return this.deploymentManager;
  }
}
