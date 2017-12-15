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
import java.security.Security;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Key;
import java.security.KeyPairGenerator;
import java.security.cert.X509Certificate;
import java.security.KeyPair;
import java.security.Signature;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.CipherOutputStream;

import org.bouncycastle.asn1.ASN1Encoding;
import org.bouncycastle.asn1.DERBMPString;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.Attribute;
import org.bouncycastle.asn1.pkcs.ContentInfo;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.InputDecryptorProvider;
import org.bouncycastle.operator.OutputEncryptor;
import org.bouncycastle.operator.bc.BcDefaultDigestProvider;
import org.bouncycastle.pkcs.PKCS12PfxPdu;
import org.bouncycastle.pkcs.PKCS12PfxPduBuilder;
import org.bouncycastle.pkcs.PKCS12SafeBag;
import org.bouncycastle.pkcs.PKCS12SafeBagBuilder;
import org.bouncycastle.pkcs.PKCS12SafeBagFactory;
import org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfo;
import org.bouncycastle.pkcs.bc.BcPKCS12MacCalculatorBuilderProvider;
import org.bouncycastle.pkcs.jcajce.JcaPKCS12SafeBagBuilder;
import org.bouncycastle.pkcs.jcajce.JcePKCS12MacCalculatorBuilder;
import org.bouncycastle.pkcs.jcajce.JcePKCSPBEInputDecryptorProviderBuilder;
import org.bouncycastle.pkcs.jcajce.JcePKCSPBEOutputEncryptorBuilder;
import org.bouncycastle.util.io.Streams;

import org.beigesoft.ajetty.crypto.CryptoService;

import static org.junit.Assert.assertEquals;
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

  private CryptoService cryptoService = new CryptoService();

  public CryptoTest() throws Exception {
    Security.addProvider(new BouncyCastleProvider());
    File pks12File = new File("ajettykeystore.1");
    if (!pks12File.exists()) {
      this.cryptoService.createKeyStoreWithCredentials(null, 1, this.ksPassword);
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
    byte[] data = dataString.getBytes(this.charset);
    System.out.println("Text : " + this.dataString);
    System.out.println("Text data size: " + data.length);
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
      System.out.println("Encrypted data size: " + encryptedData.length);
      Signature sigMk = Signature.getInstance("SHA256withRSA");
      sigMk.initSign(keyPairBob.getPrivate(), new SecureRandom());
      sigMk.update(encryptedData);
      byte[] sigDt = sigMk.sign();
      System.out.println("Signature size: " + sigDt.length);
      // Alice has received data:
      sigMk.initVerify(keyPairBob.getPublic());
      sigMk.update(encryptedData);
      if (sigMk.verify(sigDt)) {
        cipherRsa.init(Cipher.DECRYPT_MODE, keyPairAlice.getPrivate());
        byte[] decryptedData = cipherRsa.doFinal(encryptedData);
        String received = new String(decryptedData, this.charset);
        assertEquals(this.dataString, received);
        Long takeMlSec = System.currentTimeMillis() - longStart;
        System.out.println("RSA takes to encrypt/decrypt/sign/verify milliseconds: " + takeMlSec);
      } else {
        System.out.println("Wrong signature data!!!");
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
    System.out.println("testRsaAesFly SSK size: " + sskAes.getEncoded().length);
    System.out.println("testRsaAesFly Encrypted SSK size: " + encryptedSsk.length);
    Signature sigMk = Signature.getInstance("SHA256withRSA");
    sigMk.initSign(keyPairBob.getPrivate(), new SecureRandom());
    sigMk.update(encryptedSsk);
    byte[] sigSsk = sigMk.sign();
    System.out.println("testRsaAesFly Signature SSK size: " + sigSsk.length);
      //DATA:
    Cipher cipherAes = Cipher.getInstance("AES");
    cipherAes.init(Cipher.ENCRYPT_MODE, sskAes);
    byte[] data = dataString.getBytes(this.charset);
    //cipherAes.update(data); don't work
    byte[] encryptedData = cipherAes.doFinal(data);
    System.out.println("testRsaAesFly Encrypted data size: " + encryptedData.length);
    sigMk.initSign(keyPairBob.getPrivate(), new SecureRandom());
    sigMk.update(encryptedData);
    byte[] sigDt = sigMk.sign();
    System.out.println("testRsaAesFly Signature data size: " + sigDt.length);
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
        System.out.println("testRsaAesFly Restore decrypted SSK with algorithm: " + sskAes.getAlgorithm());
        SecretKeySpec sskAesRec = new SecretKeySpec(decryptedSsk, sskAes.getAlgorithm());
        cipherAes.init(Cipher.DECRYPT_MODE, sskAesRec);
        byte[] decryptedData = cipherAes.doFinal(encryptedData);
        String received = new String(decryptedData, this.charset);
        assertEquals(this.dataString, received);
        Long takeMlSec = System.currentTimeMillis() - longStart;
        System.out.println("testRsaAesFly RSA/AES takes to encrypt/decrypt/sign/verify milliseconds: " + takeMlSec);
      } else {
        System.out.println("testRsaAesFly Wrong signature DATA!!!");
      }
    } else {
      System.out.println("testRsaAesFly Wrong signature SSK!!!");
    }
  }

  /**
   * <p>Test for working - with using BC, pkcs12 keystore.<p>
   **/
  @Test
  public void testRsaAesBc() throws Exception {
    // get Bob's keystore:
    File pks12File = new File("ajettykeystore.1");
    KeyStore pkcs12Store = KeyStore.getInstance("PKCS12", "BC");
    pkcs12Store.load(new FileInputStream(pks12File), this.ksPassword);
    System.out.println("########## KeyStore Dump");
    for (Enumeration en = pkcs12Store.aliases(); en.hasMoreElements();) {
      String alias = (String)en.nextElement();
      if (pkcs12Store.isCertificateEntry(alias)) {
          System.out.println("Certificate Entry: " + alias + ", Subject: " + (((X509Certificate)pkcs12Store.getCertificate(alias)).getSubjectDN()));
      } else if (pkcs12Store.isKeyEntry(alias)) {
          System.out.println("Key Entry: " + alias + ", Subject: " + (((X509Certificate)pkcs12Store.getCertificate(alias)).getSubjectDN()));
      }
    }
    KeyGenerator keyGenAes = KeyGenerator.getInstance("AES", "BC");
    keyGenAes.init(256, new SecureRandom());
    SecretKey sskAes = keyGenAes.generateKey();
    KeyPairGenerator kpGenRsa = KeyPairGenerator.getInstance("RSA", "BC");
    // 2048 is enough for encrypting SSK.
    kpGenRsa.initialize(2048, new SecureRandom());
    KeyPair keyPairAlice = kpGenRsa.generateKeyPair();
    long longStart = System.currentTimeMillis();
    // Bob makes and sent data:
      //SSK:
    Cipher cipherRsa = Cipher.getInstance("RSA", "BC");
    cipherRsa.init(Cipher.ENCRYPT_MODE, keyPairAlice.getPublic());
    cipherRsa.update(sskAes.getEncoded());
    byte[] encryptedSsk = cipherRsa.doFinal();
    System.out.println("testRsaAesBc SSK size: " + sskAes.getEncoded().length);
    System.out.println("testRsaAesBc Encrypted SSK size: " + encryptedSsk.length);
    Signature sigMk = Signature.getInstance("SHA256withRSA");
    KeyStore.ProtectionParameter protParam = new KeyStore.PasswordProtection(this.ksPassword);
    KeyStore.PrivateKeyEntry pkEntry = (KeyStore.PrivateKeyEntry) pkcs12Store.getEntry("AJettyFileExch1", protParam);
    PrivateKey bobSk = pkEntry.getPrivateKey();
    sigMk.initSign(bobSk, new SecureRandom());
    sigMk.update(encryptedSsk);
    byte[] sigSsk = sigMk.sign();
    System.out.println("testRsaAesBc Signature SSK size: " + sigSsk.length);
      //DATA:
    Cipher cipherAes = Cipher.getInstance("AES/ECB/PKCS7Padding", "BC");
    cipherAes.init(Cipher.ENCRYPT_MODE, sskAes);
    byte[] data = dataString.getBytes(this.charset);
    byte[] encryptedData = cipherAes.doFinal(data);
    System.out.println("testRsaAesBc Encrypted data size: " + encryptedData.length);
    sigMk.initSign(bobSk, new SecureRandom());
    sigMk.update(encryptedData);
    byte[] sigDt = sigMk.sign();
    System.out.println("testRsaAesBc Signature data size: " + sigDt.length);
    // Alice has received data:
    PublicKey bobPk = pkcs12Store.getCertificate("AJettyFileExch1").getPublicKey();
    sigMk.initVerify(bobPk);
    sigMk.update(encryptedSsk);
    if (sigMk.verify(sigSsk)) {
      sigMk.initVerify(bobPk);
      sigMk.update(encryptedData);
      if (sigMk.verify(sigDt)) {
        cipherRsa.init(Cipher.DECRYPT_MODE, keyPairAlice.getPrivate());
        cipherRsa.update(encryptedSsk);
        byte[] decryptedSsk = cipherRsa.doFinal();
        System.out.println("testRsaAesBc Restore decrypted SSK with algorithm: " + sskAes.getAlgorithm());
        SecretKeySpec sskAesRec = new SecretKeySpec(decryptedSsk, sskAes.getAlgorithm());
        cipherAes.init(Cipher.DECRYPT_MODE, sskAesRec);
        byte[] decryptedData = cipherAes.doFinal(encryptedData);
        String received = new String(decryptedData, this.charset);
        assertEquals(this.dataString, received);
        Long takeMlSec = System.currentTimeMillis() - longStart;
        System.out.println("testRsaAesBc RSA/AES takes to encrypt/decrypt/sign/verify milliseconds: " + takeMlSec);
      } else {
        System.out.println("testRsaAesBc Wrong signature DATA!!!");
      }
    } else {
      System.out.println("testRsaAesBc Wrong signature SSK!!!");
    }
  }

  /**
   * <p>Test for working - with using BC, pkcs12 keystore and real SQlite database.<p>
   **/
  @Test
  public void testRsaAesBcRealData() throws Exception {
    // get Bob's keystore:
    File pks12File = new File("ajettykeystore.1");
    KeyStore pkcs12Store = KeyStore.getInstance("PKCS12", "BC");
    pkcs12Store.load(new FileInputStream(pks12File), this.ksPassword);
    KeyGenerator keyGenAes = KeyGenerator.getInstance("AES", "BC");
    keyGenAes.init(256, new SecureRandom());
    SecretKey sskAes = keyGenAes.generateKey();
    KeyPairGenerator kpGenRsa = KeyPairGenerator.getInstance("RSA", "BC");
    // 2048 is enough for encrypting SSK.
    kpGenRsa.initialize(2048, new SecureRandom());
    KeyPair keyPairAlice = kpGenRsa.generateKeyPair();
    long longStart = System.currentTimeMillis();
    // Bob makes and sent data:
      //SSK:
    Cipher cipherRsa = Cipher.getInstance("RSA", "BC");
    cipherRsa.init(Cipher.ENCRYPT_MODE, keyPairAlice.getPublic());
    cipherRsa.update(sskAes.getEncoded());
    byte[] encryptedSsk = cipherRsa.doFinal();
    System.out.println("testRsaAesBcRealData SSK size: " + sskAes.getEncoded().length);
    System.out.println("testRsaAesBcRealData Encrypted SSK size: " + encryptedSsk.length);
    Signature sigMk = Signature.getInstance("SHA256withRSA");
    KeyStore.ProtectionParameter protParam = new KeyStore.PasswordProtection(this.ksPassword);
    KeyStore.PrivateKeyEntry pkEntry = (KeyStore.PrivateKeyEntry) pkcs12Store.getEntry("AJettyFileExch1", protParam);
    PrivateKey bobSk = pkEntry.getPrivateKey();
    sigMk.initSign(bobSk, new SecureRandom());
    sigMk.update(encryptedSsk);
    byte[] sigSsk = sigMk.sign();
    System.out.println("testRsaAesBcRealData Signature SSK size: " + sigSsk.length);
      //DATA:
    Cipher cipherAes = Cipher.getInstance("AES/ECB/PKCS7Padding", "BC");
    cipherAes.init(Cipher.ENCRYPT_MODE, sskAes);
    File sqliteFl = new File("src" + File.separator + "test" + File.separator + "bobs-pizza-nfs.sqlite");
    FileInputStream fisData = new FileInputStream(sqliteFl);
    BufferedInputStream bis = new BufferedInputStream(fisData);
    FileOutputStream fosEncryptedData = new FileOutputStream("bobs-pizza-nfs.encr");
    CipherOutputStream cous = new CipherOutputStream(fosEncryptedData, cipherAes);
    byte[] buffer = new byte[1024];
    int len;
    while ((len = bis.read(buffer)) >= 0) {
      cous.write(buffer, 0, len);
    };
    bis.close();
    cous.flush();
    cous.close();
    sigMk.initSign(bobSk, new SecureRandom());
    FileInputStream fisDataEncr = new FileInputStream("bobs-pizza-nfs.encr");
    bis = new BufferedInputStream(fisDataEncr);
    while ((len = bis.read(buffer)) >= 0) {
      sigMk.update(buffer, 0, len);
    };
    bis.close();
    byte[] sigDt = sigMk.sign();
    System.out.println("testRsaAesBcRealData Signature data size: " + sigDt.length);
    // Alice has received data:
    PublicKey bobPk = pkcs12Store.getCertificate("AJettyFileExch1").getPublicKey();
    sigMk.initVerify(bobPk);
    sigMk.update(encryptedSsk);
    if (sigMk.verify(sigSsk)) {
      sigMk.initVerify(bobPk);
      fisDataEncr = new FileInputStream("bobs-pizza-nfs.encr");
      bis = new BufferedInputStream(fisDataEncr);
      while ((len = bis.read(buffer)) >= 0) {
        sigMk.update(buffer, 0, len);
      };
      bis.close();
      if (sigMk.verify(sigDt)) {
        cipherRsa.init(Cipher.DECRYPT_MODE, keyPairAlice.getPrivate());
        cipherRsa.update(encryptedSsk);
        byte[] decryptedSsk = cipherRsa.doFinal();
        System.out.println("testRsaAesBcRealData Restore decrypted SSK with algorithm: " + sskAes.getAlgorithm());
        SecretKeySpec sskAesRec = new SecretKeySpec(decryptedSsk, sskAes.getAlgorithm());
        cipherAes.init(Cipher.DECRYPT_MODE, sskAesRec);
        fisDataEncr = new FileInputStream("bobs-pizza-nfs.encr");
        bis = new BufferedInputStream(fisDataEncr);
        FileOutputStream fosDecryptedData = new FileOutputStream("bobs-pizza-nfs-r.sqlite");
        cous = new CipherOutputStream(fosDecryptedData, cipherAes);
        while ((len = bis.read(buffer)) >= 0) {
          cous.write(buffer, 0, len);
        };
        bis.close();
        cous.flush();
        cous.close();
        Long takeMlSec = System.currentTimeMillis() - longStart;
        System.out.println("testRsaAesBcRealData RSA/AES takes to encrypt/decrypt/sign/verify milliseconds: " + takeMlSec);
      } else {
        System.out.println("testRsaAesBcRealData Wrong signature DATA!!!");
      }
    } else {
      System.out.println("testRsaAesBcRealData Wrong signature SSK!!!");
    }
  }
}
