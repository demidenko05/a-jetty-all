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

import org.beigesoft.log.LoggerFileAdp;

import android.os.Environment;
import android.util.Log;

/**
 * Logger into file only for all classes.
 * File with name "ajetty[0/1].log" is placed in directory
 * where A-Jetty jar file is invoked.
 * This is not suitable for Android.
 * Many software uses Slf4J, e.g. HikariCP, so this is not suitable
 * for that situation. But A-Jetty for Android doesn't uses HikariCP,
 * so it's best way for it.
 */
public class LoggerFileAndroid extends AILoggerLog {

  /**
   * <p>Create logger for all classes.</p>
   **/
  @Override
  protected final void createLogger() {
    LoggerFileAdp logger = new LoggerFileAdp();
    String currDir = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
    logger.setFilePath(currDir + "ajetty");
    Log.i("A-Jetty", "> Log file path: " + logger.getFilePath());
    setLogger(logger);
  }
}
