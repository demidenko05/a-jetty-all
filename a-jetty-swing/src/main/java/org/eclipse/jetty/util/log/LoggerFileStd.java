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

import org.beigesoft.log.LogFile;
import org.beigesoft.log.FilFlLogPrp;
import org.beigesoft.log.IPrnDbg;
import org.beigesoft.log.PrnDbgCon;

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

  private FilFlLogPrp filFlLogPrp;

  /**
   * <p>Debug printer.</p>
   **/
  private IPrnDbg prnDbg;

  /**
   * <p>Create logger for all classes.</p>
   **/
  @Override
  protected final void createLogger() {
    LogFile log = new LogFile();
    log.setClsImm(true);
    if (this.filFlLogPrp == null) {
      this.filFlLogPrp = new FilFlLogPrp();
    }
    if (this.prnDbg == null) {
      this.prnDbg = new PrnDbgCon();
    }
    this.filFlLogPrp.setPrnDbg(this.prnDbg);
    this.filFlLogPrp.fill(log, "ajetty");
    setLogger(log);
  }


  //Simple getters and setters:
  /**
   * <p>Getter for filFlLogPrp.</p>
   * @return FilFlLogPrp
   **/
  public final FilFlLogPrp getFilFlLogPrp() {
    return this.filFlLogPrp;
  }

  /**
   * <p>Setter for filFlLogPrp.</p>
   * @param pFilFlLogPrp reference
   **/
  public final void setFilFlLogPrp(final FilFlLogPrp pFilFlLogPrp) {
    this.filFlLogPrp = pFilFlLogPrp;
  }

  /**
   * <p>Getter for prnDbg.</p>
   * @return IPrnDbg
   **/
  public final IPrnDbg getPrnDbg() {
    return this.prnDbg;
  }

  /**
   * <p>Setter for prnDbg.</p>
   * @param pPrnDbg reference
   **/
  public final void setPrnDbg(final IPrnDbg pPrnDbg) {
    this.prnDbg = pPrnDbg;
  }
}
