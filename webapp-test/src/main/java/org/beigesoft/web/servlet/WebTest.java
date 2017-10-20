package org.beigesoft.web.servlet;

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

import java.io.IOException;
import java.nio.charset.Charset;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 * Simple servlet to test UTF-8 I18N..
 * </p>
 *
 * @author Yury Demidenko
 */
@SuppressWarnings("serial")
public class WebTest extends HttpServlet {

  @Override
  public final void doGet(final HttpServletRequest pReq,
    final HttpServletResponse pResp) throws ServletException, IOException {
    pReq.setCharacterEncoding("UTF-8");
    pResp.setCharacterEncoding("UTF-8");
    String hello = pReq.getParameter("hello");
    System.out.println("Default charset = " + Charset.defaultCharset());
    System.out.println("Request encoding : " + pReq.getCharacterEncoding());
    System.out.println("Request parameter hello = " + hello);
    System.out.println("Response encoding : " + pResp.getCharacterEncoding());
    //byte[] bytes = hello.getBytes(StandardCharsets.ISO_8859_1);
    //String hello1 = new String(bytes, StandardCharsets.UTF_8);
    pResp.setContentType("text/html;charset=UTF-8");
    pResp.setStatus(HttpServletResponse.SC_OK);
    pResp.getWriter().println(
            "<h1>You say:" + hello + "</h1>");
  }
}
