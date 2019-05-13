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

package org.beigesoft.ajetty.crypto;

import java.io.File;

/**
 * <p>It serves A-Jetty with encryption features.</p>
 */
public interface ICryptoService {

  /**
   * <p>Check if password strong.</p>
   * @param pPassword Password
   * @return NULL if strong, otherwise message.
   **/
  String isPasswordStrong(char[] pPassword);

  /**
   * <p>Generates RSA pair for HTTPS and file exchange,
   * then makes certificates for them,
   * then creates Key Store and save them into it.
   * Keystore name is ajettykeystore.[pAjettyIn]
   * Validity period is 10 years since now.</p>
   * <p>It uses standard aliases prefixes:
   * <ul>
   * <li>AJettyRoot[pAjettyIn] - root certificate alias</li>
   * <li>AJettyCA[pAjettyIn] - intermediate CA certificate alias</li>
   * <li>AJettyHttps[pAjettyIn] - HTTPS certificate/private key alias</li>
   * <li>AJettyFileExch[pAjettyIn] - File exchanger certificate/private
   * key alias</li>
   * </ul>
   * </p>
   * @param pFilePath path, if null - use current
   * @param pAjettyIn A-Jetty installation number
   * @param pPassw password
   * @throws Exception an Exception
   */
  void createKeyStoreWithCredentials(String pFilePath, int pAjettyIn,
    char[] pPassw) throws Exception;

  /**
   * <p>Calculate SHA1 for given file.</p>
   * return SHA1 bytes array
   * @param pFile file
   * @return SHA1 sum
   * @throws Exception an Exception
   */
  byte[] calculateSha1(File pFile) throws Exception;

  /**
   * <p>Initialize (cryptop-rovider).</p>
   * @throws Exception an Exception
   */
  void init() throws Exception;

  /**
   * <p>Get crypto-provider name.</p>
   * @return crypto-provider name
   **/
  String getProviderName();
}
