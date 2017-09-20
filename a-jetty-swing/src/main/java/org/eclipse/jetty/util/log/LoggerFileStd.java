//
//  ========================================================================
//  Copyright (c) 1995-2016 Mort Bay Consulting Pty. Ltd.
//  ------------------------------------------------------------------------
//  All rights reserved. This program and the accompanying materials
//  are made available under the terms of the Eclipse Public License v1.0
//  and Apache License v2.0 which accompanies this distribution.
//
//      The Eclipse Public License is available at
//      http://www.eclipse.org/legal/epl-v10.html
//
//      The Apache License v2.0 is available at
//      http://www.opensource.org/licenses/apache2.0.php
//
//  You may elect to redistribute this code under either of these licenses.
//  ========================================================================
//

package org.eclipse.jetty.util.log;

import java.io.File;

import org.beigesoft.log.LoggerFile;
import org.beigesoft.log.FillerFileLogProperties;
import org.beigesoft.log.IDebugPrinter;
import org.beigesoft.log.DebugPrinterConsole;

/**
 * Logger into file only for all classes.
 * File with name "ajetty[0/1].log" is placed in directory
 * where A-Jetty jar file is invoked.
 * This is not suitable for Android.
 * Many software uses Slf4J, e.g. HikariCP, so this is not suitable
 * for that situation. But A-Jetty for Android not uses HikariCP,
 * so it's best way for it.
 */
public class LoggerFileStd extends AILoggerLog {

  private FillerFileLogProperties fillerFileLogProperties;

  /**
   * <p>Debug printer.</p>
   **/
  private IDebugPrinter debugPrinter;

  /**
   * <p>Create logger for all classes.</p>
   **/
  @Override
  protected final void createLogger() {
    LoggerFile log = new LoggerFile();
    if (this.fillerFileLogProperties == null) {
      this.fillerFileLogProperties = new FillerFileLogProperties();
    }
    if (this.debugPrinter == null) {
      this.debugPrinter = new DebugPrinterConsole();
    }
    this.fillerFileLogProperties.setDebugPrinter(this.debugPrinter);
    this.fillerFileLogProperties.fillProperties(log, "ajetty");
    setLogger(log);
  }


  //Simple getters and setters:
  /**
   * <p>Getter for fillerFileLogProperties.</p>
   * @return FillerFileLogProperties
   **/
  public final FillerFileLogProperties
    getFillerFileLogProperties() {
    return this.fillerFileLogProperties;
  }

  /**
   * <p>Setter for fillerFileLogProperties.</p>
   * @param pFillerFileLogProperties reference
   **/
  public final void setFillerFileLogProperties(
    final FillerFileLogProperties pFillerFileLogProperties) {
    this.fillerFileLogProperties = pFillerFileLogProperties;
  }

  /**
   * <p>Getter for debugPrinter.</p>
   * @return IDebugPrinter
   **/
  public final IDebugPrinter getDebugPrinter() {
    return this.debugPrinter;
  }

  /**
   * <p>Setter for debugPrinter.</p>
   * @param pDebugPrinter reference
   **/
  public final void setDebugPrinter(
    final IDebugPrinter pDebugPrinter) {
    this.debugPrinter = pDebugPrinter;
  }
}
