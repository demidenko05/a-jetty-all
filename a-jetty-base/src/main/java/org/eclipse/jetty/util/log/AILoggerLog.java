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

import org.beigesoft.log.ILog;

/**
 * <p>ILogger Logger only for all classes.</p>
 */
public abstract class AILoggerLog implements Logger {

  private ILog logger;
  
  public AILoggerLog() {
    createLogger();
  }

  /**
   * <p>Create logger for all classes.</p>
   **/
  protected abstract void createLogger();

  @Override
  public final String getName() {
    return "org.eclipse.jetty.util.log";
  }

  /**
   * <p>Only logger for all classes.</p>
   * @param name the name of the logger
   * @return a logger with the given name
   */
  @Override
  public Logger getLogger(String name) {
    return this;
  }

  @Override
  public final void warn(final String msg, final Object... args) {
    this.logger.warn(null, this.getClass(), msg);
  }

  @Override
  public final void warn(final Throwable thrown) {
    this.logger.warn(null, this.getClass(), null, thrown);
  }

  @Override
  public final void warn(final String msg, final Throwable thrown) {
    this.logger.warn(null, this.getClass(), msg, thrown);
  }

  @Override
  public final void info(final String msg, final Object... args) {
    this.logger.info(null, this.getClass(), msg);
  }

  @Override
  public final void info(final Throwable thrown) {
    this.logger.warn(null, this.getClass(), null, thrown);
  }

  @Override
  public void info(final String msg, final Throwable thrown) {
    this.logger.warn(null, this.getClass(), msg, thrown);
  }

  @Override
  public final void debug(final String msg, final Object... args) {
    if (this.logger.getDbgSh()) {
      this.logger.debug(null, this.getClass(), msg);
    }
  }

  @Override
  public final void debug(final String msg, final long arg) {
    if (this.logger.getDbgSh()) {
      this.logger.debug(null, this.getClass(), msg + new Object[]{new Long(arg)});
    }
  }

  @Override
  public final void debug(final Throwable thrown) {
    if (this.logger.getDbgSh()) {
      this.logger.debug(null, this.getClass(), null, thrown);
    }
  }

  @Override
  public final void debug(final String msg, final Throwable thrown) {
    if (this.logger.getDbgSh()) {
      this.logger.debug(null, this.getClass(), msg, thrown);
    }
  }

  @Override
  public final void ignore(final Throwable thrown) {
    this.logger.warn(null, this.getClass(), "IGNORE!!!", thrown);
  }

  @Override
  public final boolean isDebugEnabled() {
    return this.logger.getDbgSh();
  }

  @Override
  public final void setDebugEnabled(final boolean enabled) {
    this.logger.setDbgSh(enabled);
  }

  //Simple getters and setters:
  /**
   * <p>Getter for logger.</p>
   * @return ILogger
   **/
  public final ILog getLogger() {
    return this.logger;
  }

  /**
   * <p>Setter for logger.</p>
   * @param pLogger reference
   **/
  public final void setLogger(final ILog pLogger) {
    this.logger = pLogger;
  }
}
