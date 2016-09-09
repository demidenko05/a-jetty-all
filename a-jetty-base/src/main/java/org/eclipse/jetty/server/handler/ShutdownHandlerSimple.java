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

package org.eclipse.jetty.server.handler;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.NetworkConnector;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

/**
 *
 * Usage:
 *
 * <pre>
    just send post /shutdown
  </pre>
 */
public class ShutdownHandlerSimple extends HandlerWrapper
{
    private static final Logger LOG = Log.getLogger(ShutdownHandlerSimple.class);

    /**
     * <p>Jetty.</p>
     **/
    private Server server;

    public ShutdownHandlerSimple(Server pServer)
    {
        this.server = pServer;
    }
    
    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
        if (!target.equals("/shutdown"))
        {
            super.handle(target,baseRequest,request,response);
            return;
        }
        new Thread() {

          @Override
          public void run() {
            try {
              ShutdownHandlerSimple.this.server.stop();
              Thread.currentThread().sleep(2000);
            } catch (Exception e) {
            }
            System.exit(0);
          }
        }.start();
    }
}
