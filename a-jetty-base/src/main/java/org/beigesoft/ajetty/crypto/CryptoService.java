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

import java.math.BigInteger;
import java.io.File;
import java.io.FileOutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.security.SecureRandom;
import java.util.Date;
import javax.security.auth.x500.X500Principal;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
import org.bouncycastle.cert.X509v1CertificateBuilder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.cert.jcajce.JcaX509v1CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS12PfxPdu;
import org.bouncycastle.pkcs.PKCS12PfxPduBuilder;
import org.bouncycastle.pkcs.PKCS12SafeBag;
import org.bouncycastle.pkcs.PKCS12SafeBagBuilder;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.pkcs.jcajce.JcaPKCS12SafeBagBuilder;
import org.bouncycastle.pkcs.jcajce.JcePKCS12MacCalculatorBuilder;
import org.bouncycastle.pkcs.jcajce.JcePKCSPBEOutputEncryptorBuilder;
import org.bouncycastle.asn1.ASN1Encoding;
import org.bouncycastle.asn1.DERBMPString;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.operator.OutputEncryptor;

/**
 * <p>It serves A-Jetty with encryption features.
 * It's based on Bounce Castle example.</p>
 */
public class CryptoService implements ICryptoService {

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
   * @param pAjettyIn A-Jetty instance number.
   * @param pPassw password
   * @throws Exception an Exception
   */
  @Override
  public final void createKeyStoreWithCredentials(final String pFilePath,
    final int pAjettyIn, final char[] pPassw) throws Exception {
    File pks12File;
    if (pFilePath == null) {
      pks12File = new File("ajettykeystore." + pAjettyIn);
    } else {
      pks12File = new File(pFilePath + File.separator
        + "ajettykeystore." + pAjettyIn);
    }
    if (pks12File.exists()) {
      throw new Exception("File already exist - " + pks12File.getPath());
    }
    // generate key pairs:
    KeyPairGenerator kpGenRsa = KeyPairGenerator.getInstance("RSA", "BC");
    kpGenRsa.initialize(2048, new SecureRandom());
    KeyPair kpHttps = kpGenRsa.generateKeyPair();
    kpGenRsa.initialize(2048, new SecureRandom());
    KeyPair kpFileExch = kpGenRsa.generateKeyPair();
    // generate certificates:
      // root:
    kpGenRsa.initialize(2048, new SecureRandom());
    KeyPair kpRoot = kpGenRsa.generateKeyPair();
    X509Certificate rootCert = buildRootCert(kpRoot, pAjettyIn);
      // CA:
    kpGenRsa.initialize(2048, new SecureRandom());
    KeyPair kpCa = kpGenRsa.generateKeyPair();
    X509Certificate caCert = buildCaCert(kpCa.getPublic(),
      kpRoot.getPrivate(), rootCert, pAjettyIn);
      // HTTPS:
    X509Certificate httpsCert = buildEndEntityCert(kpHttps.getPublic(),
      kpCa.getPrivate(), caCert, 3, "A-Jetty" + pAjettyIn
        + " HTTPS certificate");
      // File exchanger:
    X509Certificate fileExchCert = buildEndEntityCert(kpFileExch.getPublic(),
      kpCa.getPrivate(), caCert, 4, "A-Jetty" + pAjettyIn
        + " File Exchanger certificate");
    // save to keystore:
    OutputEncryptor encOut = new JcePKCSPBEOutputEncryptorBuilder(
      NISTObjectIdentifiers.id_aes256_CBC).setProvider("BC").build(pPassw);
    PKCS12SafeBagBuilder rtCrtBagBld = new JcaPKCS12SafeBagBuilder(rootCert);
    rtCrtBagBld.addBagAttribute(PKCS12SafeBag.friendlyNameAttribute,
      new DERBMPString("AJettyRoot" + pAjettyIn));
    PKCS12SafeBagBuilder caCrtBagBld = new JcaPKCS12SafeBagBuilder(caCert);
    caCrtBagBld.addBagAttribute(PKCS12SafeBag.friendlyNameAttribute,
      new DERBMPString("AJettyCa" + pAjettyIn));
    JcaX509ExtensionUtils extUtils = new JcaX509ExtensionUtils();
    PKCS12SafeBagBuilder httpsCrBgBr = new JcaPKCS12SafeBagBuilder(httpsCert);
    httpsCrBgBr.addBagAttribute(PKCS12SafeBag.friendlyNameAttribute,
      new DERBMPString("AJettyHttps" + pAjettyIn));
    SubjectKeyIdentifier skiHttps = extUtils
      .createSubjectKeyIdentifier(httpsCert.getPublicKey());
    httpsCrBgBr.addBagAttribute(PKCS12SafeBag.localKeyIdAttribute, skiHttps);
    PKCS12SafeBagBuilder httpsKbb =
      new JcaPKCS12SafeBagBuilder(kpHttps.getPrivate(), encOut);
    httpsKbb.addBagAttribute(PKCS12SafeBag.friendlyNameAttribute,
      new DERBMPString("AJettyHttps" + pAjettyIn));
    httpsKbb.addBagAttribute(PKCS12SafeBag.localKeyIdAttribute, skiHttps);
    PKCS12SafeBagBuilder fileExchCrBgBr =
      new JcaPKCS12SafeBagBuilder(fileExchCert);
    fileExchCrBgBr.addBagAttribute(PKCS12SafeBag.friendlyNameAttribute,
      new DERBMPString("AJettyFileExch" + pAjettyIn));
    SubjectKeyIdentifier skiFileExch = extUtils
      .createSubjectKeyIdentifier(fileExchCert.getPublicKey());
    fileExchCrBgBr
      .addBagAttribute(PKCS12SafeBag.localKeyIdAttribute, skiFileExch);
    PKCS12SafeBagBuilder fileExchKbb =
      new JcaPKCS12SafeBagBuilder(kpFileExch.getPrivate(), encOut);
    fileExchKbb.addBagAttribute(PKCS12SafeBag.friendlyNameAttribute,
      new DERBMPString("AJettyFileExch" + pAjettyIn));
    fileExchKbb.addBagAttribute(PKCS12SafeBag.localKeyIdAttribute, skiFileExch);
    PKCS12PfxPduBuilder builder = new PKCS12PfxPduBuilder();
    builder.addData(httpsKbb.build());
    builder.addData(fileExchKbb.build());
    builder.addEncryptedData(new JcePKCSPBEOutputEncryptorBuilder(
      PKCSObjectIdentifiers.pbeWithSHAAnd128BitRC2_CBC).setProvider("BC")
        .build(pPassw), new PKCS12SafeBag[] {httpsCrBgBr.build(),
          fileExchCrBgBr.build(), caCrtBagBld.build(), rtCrtBagBld.build()});
    PKCS12PfxPdu pfx = builder.build(new JcePKCS12MacCalculatorBuilder(
      NISTObjectIdentifiers.id_sha256), pPassw);
    FileOutputStream pfxOut = null;
    try {
      pfxOut = new FileOutputStream(pks12File);
      // make sure we don't include indefinite length encoding
      pfxOut.write(pfx.getEncoded(ASN1Encoding.DL));
      pfxOut.flush();
    } finally {
      if (pfxOut != null) {
        pfxOut.close();
      }
    }
  }

  /**
   * <p>Build A-Jetty Root V1 certificate for 20 years.</p>
   * @param pKpRoot Root key pair
   * @param pAjettyIn A-Jetty instance number.
   * @throws Exception an Exception
   * @return root certificate
   */
  public final X509Certificate buildRootCert(
    final KeyPair pKpRoot, final int pAjettyIn) throws Exception {
    long msIn20years = 20 * 365 * 24 * 60 * 60 * 1000;
    X509v1CertificateBuilder certBldr = new JcaX509v1CertificateBuilder(
      new X500Name("CN=A-Jetty" + pAjettyIn + " Root Certificate"),
        BigInteger.valueOf(1), //#1
          new Date(System.currentTimeMillis()),
            new Date(System.currentTimeMillis() + msIn20years),
              new X500Name("CN=A-Jetty" + pAjettyIn + " Root Certificate"),
                pKpRoot.getPublic());
    ContentSigner signer = new JcaContentSignerBuilder("SHA256withRSA")
      .setProvider("BC").build(pKpRoot.getPrivate());
    return new JcaX509CertificateConverter().setProvider("BC")
      .getCertificate(certBldr.build(signer));
  }

  /**
   * <p>Build A-Jetty CA intermediate V3 certificate
   * to use for creating (signing) end entities certificates.</p>
   * @param pCaPk CA PK
   * @param pRootSk root private key
   * @param pRootCert root certificate
   * @param pAjettyIn A-Jetty instance number.
   * @throws Exception an Exception
   * @return CA certificate
   */
  public final X509Certificate buildCaCert(final PublicKey pCaPk,
    final PrivateKey pRootSk, final X509Certificate pRootCert,
      final int pAjettyIn) throws Exception {
    long msIn20years = 20 * 365 * 24 * 60 * 60 * 1000;
    X509v3CertificateBuilder certBldr = new JcaX509v3CertificateBuilder(
      pRootCert.getSubjectX500Principal(), BigInteger.valueOf(2), //#2
        new Date(System.currentTimeMillis()),
          new Date(System.currentTimeMillis() + msIn20years),
            new X500Principal("CN=A-Jetty" + pAjettyIn + " CA Certificate"),
              pCaPk);
    JcaX509ExtensionUtils extUtils = new JcaX509ExtensionUtils();
    certBldr.addExtension(Extension.authorityKeyIdentifier, false, extUtils
      .createAuthorityKeyIdentifier(pRootCert)).addExtension(Extension
      .subjectKeyIdentifier, false, extUtils.createSubjectKeyIdentifier(pCaPk))
      .addExtension(Extension.basicConstraints, true, new BasicConstraints(0))
      .addExtension(Extension.keyUsage, true, new KeyUsage(KeyUsage
      .digitalSignature | KeyUsage.keyCertSign | KeyUsage.cRLSign));
    ContentSigner signer = new JcaContentSignerBuilder("SHA256withRSA")
      .setProvider("BC").build(pRootSk);
    return new JcaX509CertificateConverter().setProvider("BC")
      .getCertificate(certBldr.build(signer));
  }

  /**
   * <p>Build end entity V3 certificate.</p>
   * @param pEntityPk entity PK
   * @param pCaSk CA private key
   * @param pCaCert CA certificate
   * @param pSn serial number
   * @param pName entity name
   * @throws Exception an Exception
   * @return end user certificate
   */
  public final X509Certificate buildEndEntityCert(final PublicKey pEntityPk,
    final PrivateKey pCaSk, final X509Certificate pCaCert,
      final int pSn, final String pName) throws Exception {
    long msIn20years = 20 * 365 * 24 * 60 * 60 * 1000;
    X509v3CertificateBuilder certBldr = new JcaX509v3CertificateBuilder(
      pCaCert.getSubjectX500Principal(), BigInteger.valueOf(pSn),
        new Date(System.currentTimeMillis()),
          new Date(System.currentTimeMillis() + msIn20years),
            new X500Principal("CN=" + pName), pEntityPk);
    JcaX509ExtensionUtils extUtils = new JcaX509ExtensionUtils();
    certBldr.addExtension(Extension.authorityKeyIdentifier, false, extUtils
      .createAuthorityKeyIdentifier(pCaCert)).addExtension(Extension
      .subjectKeyIdentifier, false, extUtils.createSubjectKeyIdentifier(
      pEntityPk)).addExtension(Extension.basicConstraints, true,
      new BasicConstraints(false)).addExtension(Extension.keyUsage, true,
      new KeyUsage(KeyUsage.digitalSignature | KeyUsage.keyEncipherment));
    ContentSigner signer = new JcaContentSignerBuilder("SHA256withRSA")
      .setProvider("BC").build(pCaSk);
    return new JcaX509CertificateConverter().setProvider("BC")
      .getCertificate(certBldr.build(signer));
  }
}
