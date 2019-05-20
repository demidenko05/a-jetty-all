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

/**
 * <p>Generic exception for A-Jetty start.</p>
 *
 * @author Yury Demidenko
 */
public class ExceptionStart extends Exception {


  /**
   * <p>Constructor default.</p>
   **/
  public ExceptionStart() {
  }

  /**
   * <p>Constructor MSG.</p>
   * @param pMsg message
   **/
  public ExceptionStart(final String pMsg) {
    super(pMsg);
  }

  /**
   * <p>Constructor CAUSE.</p>
   * @param pCause parent exception
   **/
  public ExceptionStart(final Throwable pCause) {
    super(pCause);
  }

  /**
   * <p>Constructor MSG CAUSE.</p>
   * @param pMsg message
   * @param pCause parent exception
   **/
  public ExceptionStart(final String pMsg, final Throwable pCause) {
    super(pMsg, pCause);
  }
}
