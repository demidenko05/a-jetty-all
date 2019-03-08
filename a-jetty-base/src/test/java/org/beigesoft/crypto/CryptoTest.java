package org.beigesoft.crypto;

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

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ByteArrayInputStream;
import java.io.BufferedInputStream;
import java.io.File;
import java.util.Enumeration;
import java.nio.charset.Charset;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Key;
import java.security.KeyPairGenerator;
import java.security.cert.X509Certificate;
import java.security.KeyPair;
import java.security.KeyFactory;
import java.security.Signature;
import java.security.SecureRandom;
import java.security.spec.X509EncodedKeySpec;
import java.security.cert.Certificate;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.CipherOutputStream;

import org.beigesoft.ajetty.crypto.ICryptoService;
import org.beigesoft.ajetty.crypto.CryptoService;
import org.beigesoft.log.ILog;
import org.beigesoft.log.LogSmp;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;
import org.junit.Test;

/**
 * <p>Crypto tests.</p>
 *
 * @author Yury Demidenko
 */
public class CryptoTest {
  
  private final Charset charset = Charset.forName("UTF-8");

  private final String dataString = "Hi Alice! \n It's  cloudy and worm today. Birds are singing. \n Some code: クルマ カーラインアップ본명조는 Adobe Type에서 최근 출시한 두번째北京市景点玩乐 \n ™ jljsdl898лржыфрважы879732лододыв8989879823бодол979798798798одододшо. \n ijhaoisjdoijoihjoi729173987yohds87u9dfwhekdhsa8798uijdow89ee8u98uoihid98u89u98u23oi89787tuyguyguyt87w7t687 \n Truly yours, Bob.";

  private final char[] ksPassword = "Hhhl98Kl2983hkjhkj".toCharArray();

  private ICryptoService cryptoService;
  
  private ILog log;

  private String ksPath;

  private boolean isInit = false;

  public void initIfNeed() throws Exception {
    if (!isInit) {
      if (this.cryptoService == null) {
        this.cryptoService = new CryptoService();
      }
      this.cryptoService.init();
      File pks12File;
      if (this.ksPath == null) {
        pks12File = new File("ajettykeystore.1");
      } else {
        pks12File = new File(this.ksPath + File.separator + "ajettykeystore.1");
      }
      if (!pks12File.exists()) {
        this.cryptoService.createKeyStoreWithCredentials(this.ksPath, 1, this.ksPassword);
      }
      if (this.log == null) {
        this.log = new LogSmp();
      }
      isInit = true;
    }
  }

  /**
   * <p>The slowest and simplest method.
   * AKP is asymmetric key pair (SK+PK).
   * Bob generated his AKP pair and sent PK to Alice.
   * Alice generated her AKP pair and send PK to Bob.
   * They used phone to verify their PK.
   * Bob encrypted DATA with her PK then signed it with his SK.
   * Then Bob send DATA_ENCRYPTED and its SIGNATURE to Alice.
   * Alice check SIGNATURE for DATA_ENCRYPTED with Bob's PK.
   * Then Alice decrypted DATA_ENCRYPTED with her SK.
   * <p>
   * <p>But it's NOT POSSIBLE for large data!!!</p>
   **/
  @Test
  public void testRsaFly() throws Exception {
    initIfNeed();
    byte[] data = dataString.getBytes(this.charset);
    this.log.info(null, CryptoTest.class,"Text : " + this.dataString);
    this.log.info(null, CryptoTest.class,"Text data size: " + data.length);
    KeyPairGenerator kpGenRsa = KeyPairGenerator.getInstance("RSA");
    //They suggest to use pure new SecureRandom()
    //They - Bounce Castle last examples, https://android-developers.googleblog.com/2013/02/using-cryptography-to-store-credentials.html
    kpGenRsa.initialize(4096, new SecureRandom());
    KeyPair keyPairBob = kpGenRsa.generateKeyPair();
    KeyPair keyPairAlice = kpGenRsa.generateKeyPair();
    try {
      long longStart = System.currentTimeMillis();
      // Bob makes and sent data:
      Cipher cipherRsa = Cipher.getInstance("RSA");
      cipherRsa.init(Cipher.ENCRYPT_MODE, keyPairAlice.getPublic());
      byte[] encryptedData = cipherRsa.doFinal(data);
      this.log.info(null, CryptoTest.class,"Encrypted data size: " + encryptedData.length);
      Signature sigMk = Signature.getInstance("SHA256withRSA");
      sigMk.initSign(keyPairBob.getPrivate(), new SecureRandom());
      sigMk.update(encryptedData);
      byte[] sigDt = sigMk.sign();
      this.log.info(null, CryptoTest.class,"Signature size: " + sigDt.length);
      // Alice has received data:
      sigMk.initVerify(keyPairBob.getPublic());
      sigMk.update(encryptedData);
      if (sigMk.verify(sigDt)) {
        cipherRsa.init(Cipher.DECRYPT_MODE, keyPairAlice.getPrivate());
        byte[] decryptedData = cipherRsa.doFinal(encryptedData);
        String received = new String(decryptedData, this.charset);
        assertEquals(this.dataString, received);
        Long takeMlSec = System.currentTimeMillis() - longStart;
        this.log.info(null, CryptoTest.class,"RSA takes to encrypt/decrypt/sign/verify milliseconds: " + takeMlSec);
      } else {
        this.log.info(null, CryptoTest.class,"Wrong signature data!!!");
      }
    } catch (Exception e) {
      // for large data and key 2048 size it will be:
      // javax.crypto.IllegalBlockSizeException: Data must not be longer than 245 bytes
      // at at com.sun.crypto.provider.RSACipher.doFinal(RSACipher.java:344)
      // using javax.crypto.SealedObject resolve this problem, but why this problem arises?
      // they say that data can not be grater than key, i.e. 2048 bit key = 2048/8 = 245 bytes max data?
      // Is key size 2048 not enough strong for data more than 245 bytes?
      e.printStackTrace();
    }
  }

  /**
   * <p>The fastest widely used method.
   * AKP is asymmetric key pair (SK+PK).
   * Bob generated his AKP pair and sent PK to Alice.
   * Alice generated her AKP pair and send PK to Bob.
   * They used phone to verify their PK.
   * Bob generated SSK key (symmetric secret key) then encrypted it with her PK and signed it with his SK,
   * then he encrypted DATA with SSK and signed with his SK.
   * Then Bob send AES_ENCRYPTED and AES_SIGNATURE and DATA_ENCRYPTED and DATA_SIGNATURE to Alice.
   * Alice check signatures of both AES_ENCRYPTED and DATA_ENCRYPTED with Bob's PK.
   * Then Alice decrypted SSK with Bob's PK then she decrepted DATA_ENCRYPTED with SSK.
   * Bob and Alice destroyed SSK key.
   * They generate SSK key for every data sending transaction.<p>
   **/
  @Test
  public void testRsaAesFly() throws Exception {
    initIfNeed();
    KeyGenerator keyGenAes = KeyGenerator.getInstance("AES");
    keyGenAes.init(256, new SecureRandom());
    SecretKey sskAes = keyGenAes.generateKey();
    KeyPairGenerator kpGenRsa = KeyPairGenerator.getInstance("RSA");
    // 2048 is enough for encrypting SSK.
    kpGenRsa.initialize(2048, new SecureRandom());
    KeyPair keyPairBob = kpGenRsa.generateKeyPair();
    KeyPair keyPairAlice = kpGenRsa.generateKeyPair();
    long longStart = System.currentTimeMillis();
    // Bob makes and sent data:
      //SSK:
    Cipher cipherRsa = Cipher.getInstance("RSA");
    cipherRsa.init(Cipher.ENCRYPT_MODE, keyPairAlice.getPublic());
    cipherRsa.update(sskAes.getEncoded());
    byte[] encryptedSsk = cipherRsa.doFinal();
    this.log.info(null, CryptoTest.class,"testRsaAesFly SSK size: " + sskAes.getEncoded().length);
    this.log.info(null, CryptoTest.class,"testRsaAesFly Encrypted SSK size: " + encryptedSsk.length);
    Signature sigMk = Signature.getInstance("SHA256withRSA");
    sigMk.initSign(keyPairBob.getPrivate(), new SecureRandom());
    sigMk.update(encryptedSsk);
    byte[] sigSsk = sigMk.sign();
    this.log.info(null, CryptoTest.class,"testRsaAesFly Signature SSK size: " + sigSsk.length);
      //DATA:
    Cipher cipherAes = Cipher.getInstance("AES");
    cipherAes.init(Cipher.ENCRYPT_MODE, sskAes);
    byte[] data = dataString.getBytes(this.charset);
    //cipherAes.update(data); don't work
    byte[] encryptedData = cipherAes.doFinal(data);
    this.log.info(null, CryptoTest.class,"testRsaAesFly Encrypted data size: " + encryptedData.length);
    sigMk.initSign(keyPairBob.getPrivate(), new SecureRandom());
    sigMk.update(encryptedData);
    byte[] sigDt = sigMk.sign();
    this.log.info(null, CryptoTest.class,"testRsaAesFly Signature data size: " + sigDt.length);
    // Alice has received data:
    sigMk.initVerify(keyPairBob.getPublic());
    sigMk.update(encryptedSsk);
    if (sigMk.verify(sigSsk)) {
      sigMk.initVerify(keyPairBob.getPublic());
      sigMk.update(encryptedData);
      if (sigMk.verify(sigDt)) {
        cipherRsa.init(Cipher.DECRYPT_MODE, keyPairAlice.getPrivate());
        cipherRsa.update(encryptedSsk);
        byte[] decryptedSsk = cipherRsa.doFinal();
        this.log.info(null, CryptoTest.class,"testRsaAesFly Restore decrypted SSK with algorithm: " + sskAes.getAlgorithm());
        SecretKeySpec sskAesRec = new SecretKeySpec(decryptedSsk, sskAes.getAlgorithm());
        cipherAes.init(Cipher.DECRYPT_MODE, sskAesRec);
        byte[] decryptedData = cipherAes.doFinal(encryptedData);
        String received = new String(decryptedData, this.charset);
        assertEquals(this.dataString, received);
        Long takeMlSec = System.currentTimeMillis() - longStart;
        this.log.info(null, CryptoTest.class,"testRsaAesFly RSA/AES takes to encrypt/decrypt/sign/verify milliseconds: " + takeMlSec);
      } else {
        this.log.info(null, CryptoTest.class,"testRsaAesFly Wrong signature DATA!!!");
      }
    } else {
      this.log.info(null, CryptoTest.class,"testRsaAesFly Wrong signature SSK!!!");
    }
  }

  /**
   * <p>Test for working - with using BC, pkcs12 keystore.<p>
   **/
  @Test
  public void testRsaAesBc() throws Exception {
    initIfNeed();
    // get Bob's keystore:
    File pks12File;
    if (this.ksPath == null) {
      pks12File = new File("ajettykeystore.1");
    } else {
      pks12File = new File(this.ksPath + File.separator + "ajettykeystore.1");
    }
    KeyStore pkcs12Store = KeyStore.getInstance("PKCS12", this.cryptoService.getProviderName());
    pkcs12Store.load(new FileInputStream(pks12File), this.ksPassword);
    this.log.info(null, CryptoTest.class,"########## KeyStore Dump");
    for (Enumeration en = pkcs12Store.aliases(); en.hasMoreElements();) {
      String alias = (String)en.nextElement();
      if (pkcs12Store.isCertificateEntry(alias)) {
          this.log.info(null, CryptoTest.class,"Certificate Entry: " + alias + ", Subject: " + (((X509Certificate)pkcs12Store.getCertificate(alias)).getSubjectDN()));
      } else if (pkcs12Store.isKeyEntry(alias)) {
          this.log.info(null, CryptoTest.class,"Key Entry: " + alias + ", Subject: " + (((X509Certificate)pkcs12Store.getCertificate(alias)).getSubjectDN()));
      }
    }
    KeyGenerator keyGenAes = KeyGenerator.getInstance("AES", this.cryptoService.getProviderName());
    keyGenAes.init(256, new SecureRandom());
    SecretKey sskAes = keyGenAes.generateKey();
    KeyPairGenerator kpGenRsa = KeyPairGenerator.getInstance("RSA", this.cryptoService.getProviderName());
    // 2048 is enough for encrypting SSK.
    kpGenRsa.initialize(2048, new SecureRandom());
    KeyPair keyPairAlice = kpGenRsa.generateKeyPair();
    long longStart = System.currentTimeMillis();
    // Bob makes and sent data:
      //SSK:
    Cipher cipherRsa = Cipher.getInstance("RSA", this.cryptoService.getProviderName());
    cipherRsa.init(Cipher.ENCRYPT_MODE, keyPairAlice.getPublic());
    cipherRsa.update(sskAes.getEncoded());
    byte[] encryptedSsk = cipherRsa.doFinal();
    this.log.info(null, CryptoTest.class,"testRsaAesBc SSK size: " + sskAes.getEncoded().length);
    this.log.info(null, CryptoTest.class,"testRsaAesBc Encrypted SSK size: " + encryptedSsk.length);
    Signature sigMk = Signature.getInstance("SHA256withRSA");
    KeyStore.ProtectionParameter protParam = new KeyStore.PasswordProtection(this.ksPassword);
    KeyStore.PrivateKeyEntry pkEntry = (KeyStore.PrivateKeyEntry) pkcs12Store.getEntry("AJettyFileExch1", protParam);
    PrivateKey bobSk = pkEntry.getPrivateKey();
    sigMk.initSign(bobSk, new SecureRandom());
    sigMk.update(encryptedSsk);
    byte[] sigSsk = sigMk.sign();
    this.log.info(null, CryptoTest.class,"testRsaAesBc Signature SSK size: " + sigSsk.length);
      //DATA:
    Cipher cipherAes = Cipher.getInstance("AES/ECB/PKCS7Padding", this.cryptoService.getProviderName());
    cipherAes.init(Cipher.ENCRYPT_MODE, sskAes);
    byte[] data = dataString.getBytes(this.charset);
    byte[] encryptedData = cipherAes.doFinal(data);
    this.log.info(null, CryptoTest.class,"testRsaAesBc Encrypted data size: " + encryptedData.length);
    sigMk.initSign(bobSk, new SecureRandom());
    sigMk.update(encryptedData);
    byte[] sigDt = sigMk.sign();
    this.log.info(null, CryptoTest.class,"testRsaAesBc Signature data size: " + sigDt.length);
    // Alice has received data:
    byte[] bobPkFromFile = pkcs12Store.getCertificate("AJettyFileExch1").getPublicKey().getEncoded();
    X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(bobPkFromFile);
    KeyFactory keyFactory = KeyFactory.getInstance("RSA", this.cryptoService.getProviderName());
    PublicKey bobPk = keyFactory.generatePublic(pubKeySpec);
    sigMk.initVerify(bobPk);
    sigMk.update(encryptedSsk);
    if (sigMk.verify(sigSsk)) {
      sigMk.initVerify(bobPk);
      sigMk.update(encryptedData);
      if (sigMk.verify(sigDt)) {
        cipherRsa.init(Cipher.DECRYPT_MODE, keyPairAlice.getPrivate());
        cipherRsa.update(encryptedSsk);
        byte[] decryptedSsk = cipherRsa.doFinal();
        this.log.info(null, CryptoTest.class,"testRsaAesBc Restore decrypted SSK with algorithm: " + sskAes.getAlgorithm());
        SecretKeySpec sskAesRec = new SecretKeySpec(decryptedSsk, sskAes.getAlgorithm());
        cipherAes.init(Cipher.DECRYPT_MODE, sskAesRec);
        byte[] decryptedData = cipherAes.doFinal(encryptedData);
        String received = new String(decryptedData, this.charset);
        assertEquals(this.dataString, received);
        Long takeMlSec = System.currentTimeMillis() - longStart;
        this.log.info(null, CryptoTest.class,"testRsaAesBc RSA/AES takes to encrypt/decrypt/sign/verify milliseconds: " + takeMlSec);
      } else {
        this.log.info(null, CryptoTest.class,"testRsaAesBc Wrong signature DATA!!!");
      }
    } else {
      this.log.info(null, CryptoTest.class,"testRsaAesBc Wrong signature SSK!!!");
    }
  }

  /**
   * <p>Test for working - with using BC, pkcs12 keystore and real SQlite database.<p>
   **/
  @Test
  public void testRsaAesBcRealData() throws Exception {
    initIfNeed();
    // get Bob's keystore:
    File pks12File;
    if (this.ksPath == null) {
      pks12File = new File("ajettykeystore.1");
    } else {
      pks12File = new File(this.ksPath + File.separator + "ajettykeystore.1");
    }
    KeyStore pkcs12Store = KeyStore.getInstance("PKCS12", this.cryptoService.getProviderName());
    pkcs12Store.load(new FileInputStream(pks12File), this.ksPassword);
    KeyGenerator keyGenAes = KeyGenerator.getInstance("AES", this.cryptoService.getProviderName());
    keyGenAes.init(256, new SecureRandom());
    SecretKey sskAes = keyGenAes.generateKey();
    KeyPairGenerator kpGenRsa = KeyPairGenerator.getInstance("RSA", this.cryptoService.getProviderName());
    // 2048 is enough for encrypting SSK.
    kpGenRsa.initialize(2048, new SecureRandom());
    KeyPair keyPairAlice = kpGenRsa.generateKeyPair();
    long longStart = System.currentTimeMillis();
    // Bob makes and sent data:
      //SSK:
    Cipher cipherRsa = Cipher.getInstance("RSA", this.cryptoService.getProviderName());
    cipherRsa.init(Cipher.ENCRYPT_MODE, keyPairAlice.getPublic());
    cipherRsa.update(sskAes.getEncoded());
    byte[] encryptedSsk = cipherRsa.doFinal();
    this.log.info(null, CryptoTest.class,"testRsaAesBcRealData SSK size: " + sskAes.getEncoded().length);
    this.log.info(null, CryptoTest.class,"testRsaAesBcRealData Encrypted SSK size: " + encryptedSsk.length);
    Signature sigMk = Signature.getInstance("SHA256withRSA");
    KeyStore.ProtectionParameter protParam = new KeyStore.PasswordProtection(this.ksPassword);
    KeyStore.PrivateKeyEntry pkEntry = (KeyStore.PrivateKeyEntry) pkcs12Store.getEntry("AJettyFileExch1", protParam);
    PrivateKey bobSk = pkEntry.getPrivateKey();
    sigMk.initSign(bobSk, new SecureRandom());
    sigMk.update(encryptedSsk);
    byte[] sigSsk = sigMk.sign();
    this.log.info(null, CryptoTest.class,"testRsaAesBcRealData Signature SSK size: " + sigSsk.length);
      //DATA:
    Cipher cipherAes = Cipher.getInstance("AES/ECB/PKCS7Padding", this.cryptoService.getProviderName());
    cipherAes.init(Cipher.ENCRYPT_MODE, sskAes);
    File sqliteFl;
    if (this.ksPath == null) {
      sqliteFl = new File("src" + File.separator + "test" + File.separator + "bobs-pizza-nfs.sqlite");
    } else {
      sqliteFl = new File(this.ksPath + File.separator + "bobs-pizza-nfs.sqlite");
    }
    FileInputStream fisData = new FileInputStream(sqliteFl);
    BufferedInputStream bis = new BufferedInputStream(fisData);
    File encrFl;
    if (this.ksPath == null) {
      encrFl = new File("bobs-pizza-nfs.sqlten");
    } else {
      encrFl = new File(this.ksPath + File.separator + "bobs-pizza-nfs.sqlten");
    }
    FileOutputStream fosEncryptedData = new FileOutputStream(encrFl);
    CipherOutputStream cous = new CipherOutputStream(fosEncryptedData, cipherAes);
    byte[] buffer = new byte[1024];
    int len;
    while ((len = bis.read(buffer)) > 0) {
      cous.write(buffer, 0, len);
    }
    bis.close();
    cous.flush();
    cous.close();
    byte[] digestOri = this.cryptoService.calculateSha1(sqliteFl);
    sigMk.initSign(bobSk, new SecureRandom());
    FileInputStream fisDataEncr = new FileInputStream(encrFl);
    bis = new BufferedInputStream(fisDataEncr);
    while ((len = bis.read(buffer)) >= 0) {
      sigMk.update(buffer, 0, len);
    }
    bis.close();
    byte[] sigDt = sigMk.sign();
    this.log.info(null, CryptoTest.class,"testRsaAesBcRealData Signature data size: " + sigDt.length);
    // Alice has received data:
    byte[] bobPkFromFile = pkcs12Store.getCertificate("AJettyFileExch1").getPublicKey().getEncoded();
    X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(bobPkFromFile);
    KeyFactory keyFactory = KeyFactory.getInstance("RSA", this.cryptoService.getProviderName());
    PublicKey bobPk = keyFactory.generatePublic(pubKeySpec);
    sigMk.initVerify(bobPk);
    sigMk.update(encryptedSsk);
    if (sigMk.verify(sigSsk)) {
      sigMk.initVerify(bobPk);
      fisDataEncr = new FileInputStream(encrFl);
      bis = new BufferedInputStream(fisDataEncr);
      while ((len = bis.read(buffer)) > 0) {
        sigMk.update(buffer, 0, len);
      }
      bis.close();
      if (sigMk.verify(sigDt)) {
        cipherRsa.init(Cipher.DECRYPT_MODE, keyPairAlice.getPrivate());
        byte[] decryptedSsk = cipherRsa.doFinal(encryptedSsk);
        this.log.info(null, CryptoTest.class,"testRsaAesBcRealData Restore decrypted SSK size/algorithm: "  + decryptedSsk + "/" + sskAes.getAlgorithm());
        SecretKeySpec sskAesRec = new SecretKeySpec(decryptedSsk, sskAes.getAlgorithm());
        cipherAes.init(Cipher.DECRYPT_MODE, sskAesRec);
        fisDataEncr = new FileInputStream(encrFl);
        bis = new BufferedInputStream(fisDataEncr);
        File recFl;
        if (this.ksPath == null) {
          recFl = new File("bobs-pizza-nfs-r.sqlite");
        } else {
          recFl = new File(this.ksPath + File.separator + "bobs-pizza-nfs-r.sqlite");
        }
        FileOutputStream fosDecryptedData = new FileOutputStream(recFl);
        cous = new CipherOutputStream(fosDecryptedData, cipherAes);
        while ((len = bis.read(buffer)) > 0) {
          cous.write(buffer, 0, len);
        }
        bis.close();
        cous.flush();
        cous.close();
        byte[] digestRec = this.cryptoService.calculateSha1(recFl);
        assertArrayEquals(digestOri, digestRec);
        Long takeMlSec = System.currentTimeMillis() - longStart;
        this.log.info(null, CryptoTest.class,"testRsaAesBcRealData RSA/AES takes to encrypt/decrypt/sign/verify milliseconds: " + takeMlSec);
      } else {
        this.log.info(null, CryptoTest.class,"testRsaAesBcRealData Wrong signature DATA!!!");
      }
    } else {
      this.log.info(null, CryptoTest.class,"testRsaAesBcRealData Wrong signature SSK!!!");
    }
  }

  /**
   * <p>Test password strong.<p>
   **/
  @Test
  public void testPasswordStrong() throws Exception {
    initIfNeed();
    char[] password = null;
    String rez = this.cryptoService.isPasswordStrong(password);
    assertNotNull(rez);
    password = "".toCharArray();
    rez = this.cryptoService.isPasswordStrong(password);
    assertNotNull(rez);
    password = "hhkjhkauwsy".toCharArray();
    rez = this.cryptoService.isPasswordStrong(password);
    assertNotNull(rez);
    password = "gracailikiki213".toCharArray();
    rez = this.cryptoService.isPasswordStrong(password);
    assertNull(rez);
    password = "gracioliFIkw213".toCharArray();
    rez = this.cryptoService.isPasswordStrong(password);
    assertNull(rez);
    //human friendly strong (to try by hand) passwords:
    password = "deviLWoodgrovE155".toCharArray();
    rez = this.cryptoService.isPasswordStrong(password);
    assertNull(rez);
  }

  /**
   * <p>Getter for log.</p>
   * @return ILog
   **/
  public final ILog getLog() {
    return this.log;
  }

  /**
   * <p>Setter for log.</p>
   * @param pLog reference
   **/
  public final void setLog(final ILog pLog) {
    this.log = pLog;
  }

  /**
   * <p>Getter for ksPath.</p>
   * @return String
   **/
  public final String getKsPath() {
    return this.ksPath;
  }

  /**
   * <p>Setter for ksPath.</p>
   * @param pKsPath reference
   **/
  public final void setKsPath(final String pKsPath) {
    this.ksPath = pKsPath;
  }

  /**
   * <p>Getter for cryptoService.</p>
   * @return CryptoService
   **/
  public final ICryptoService getCryptoService() {
    return this.cryptoService;
  }

  /**
   * <p>Setter for cryptoService.</p>
   * @param pCryptoService reference
   **/
  public final void setCryptoService(final ICryptoService pCryptoService) {
    this.cryptoService = pCryptoService;
  }
}
