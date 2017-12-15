package org.beigesoft.ajetty.crypto;

/*
 * Copyright (c) 2017 Beigesoft ™
 *
 * Licensed under the GNU General Public License (GPL), Version 2.0
 * (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.en.html
 */

/**
 * <p>It serves A-Jetty with encryption features.</p>
 */
public interface ICryptoService {

  /**
   * <p>Generates RSA pair for HTTPS and file exchange,
   * then makes certificates for them,
   * then creates Key Store and save them into it.
   * Keystore name is ajettykeystore.[pAjettyIn]
   * Validity period is 20 years since now.</p>
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
}
