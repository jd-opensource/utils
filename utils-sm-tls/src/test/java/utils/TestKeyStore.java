package utils;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.junit.Test;
import utils.certs.CertsHelper;
import utils.certs.SM2Util;
import utils.certs.SmCertMarker;

import java.io.*;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;

public class TestKeyStore {

    @Test
    public void testBuildCertZipFile() throws Exception {

        long expire = 365 * 24 * 60 * 60 * 1000;

        KeyPair rootKeyPair = SM2Util.generateKeyPair();
        KeyPair middleKeyPair = SM2Util.generateKeyPair();
        X500Name rootCaDN = CertsHelper.buildCertDN("CN", "jd", "jd", "rootCA");
        X500Name middleCaDN = CertsHelper.buildCertDN("CN", "jd", "jd", "middleCA");

        X509Certificate rootCACert = SmCertMarker.createRootCACert(rootKeyPair, rootCaDN, expire);
        X509Certificate middleCACert = SmCertMarker.createMiddleCACert(rootKeyPair, rootCaDN, middleCaDN, middleKeyPair, expire);

        CertsHelper.buildNodeSMCerts(rootCACert, middleCACert, middleKeyPair, "node0-test", "node", "12345678", new File("D:\\block160\\cert\\"), null);


    }


    @Test
    public void testCert() throws Exception {

        long expire = 365 * 24 * 60 * 60 * 1000;

        KeyPair rootKeyPair = SM2Util.generateKeyPair();
        KeyPair middleKeyPair = SM2Util.generateKeyPair();
        KeyPair node0SigKeyPair = SM2Util.generateKeyPair();
        KeyPair node0EncKeyPair = SM2Util.generateKeyPair();
        X500Name rootCaDN = CertsHelper.buildCertDN("CN", "jd", "jd", "rootCA");
        X500Name middleCaDN = CertsHelper.buildCertDN("CN", "jd", "jd", "middleCA");
        X500Name node0SigDN = CertsHelper.buildCertDN("CN", "jd", "jd", "node0-sig");
        X500Name node0EncDN = CertsHelper.buildCertDN("CN", "jd", "jd", "node0-enc");

        X509Certificate rootCACert = SmCertMarker.createRootCACert(rootKeyPair, rootCaDN, expire);
        X509Certificate middleCACert = SmCertMarker.createMiddleCACert(rootKeyPair, rootCaDN, middleCaDN, middleKeyPair, expire);

        X509Certificate node0SignCert = SmCertMarker.createEntitySignCert(middleKeyPair, middleCaDN, node0SigDN, node0SigKeyPair, expire);
        X509Certificate node0EncCert = SmCertMarker.createEntityEncCert(middleKeyPair, middleCaDN, node0EncDN, node0EncKeyPair, expire);


        JcaPEMWriter jcaPEMWriter = new JcaPEMWriter(new FileWriter("sig.cert.pem"));
        jcaPEMWriter.writeObject(node0SignCert);
        jcaPEMWriter.close();

        jcaPEMWriter = new JcaPEMWriter(new FileWriter("sig.key.pem"));
        jcaPEMWriter.writeObject(node0SigKeyPair.getPrivate());
        jcaPEMWriter.close();

        KeyStore signKeyStore = KeyStore.getInstance("pkcs12");
        signKeyStore.load(null, null);
        signKeyStore.setKeyEntry("sign", node0SigKeyPair.getPrivate(), "12345678".toCharArray(), new Certificate[]{middleCACert});
        signKeyStore.store(new FileOutputStream("sign.pfx"), "12345678".toCharArray());

        KeyStore encKeyStore = KeyStore.getInstance("pkcs12");
        encKeyStore.load(null, null);
        encKeyStore.setKeyEntry("enc", node0EncKeyPair.getPrivate(), "12345678".toCharArray(), new Certificate[]{middleCACert});
        encKeyStore.store(new FileOutputStream("enc.pfx"), "12345678".toCharArray());


        KeyStore bothStore = KeyStore.getInstance("pkcs12");
        bothStore.load(null, null);

        bothStore.setCertificateEntry("RCA", rootCACert);
        bothStore.setCertificateEntry("OCA", middleCACert);
        bothStore.setCertificateEntry("enc", node0EncCert);
        bothStore.setCertificateEntry("sig", node0SignCert);
        bothStore.setKeyEntry("enc", node0EncKeyPair.getPrivate(), "12345678".toCharArray(), new Certificate[]{node0EncCert, middleCACert, rootCACert});
        bothStore.setKeyEntry("sig", node0SigKeyPair.getPrivate(), "12345678".toCharArray(), new Certificate[]{node0SignCert, middleCACert, rootCACert});


        bothStore.store(new FileOutputStream("both.pfx"), "12345678".toCharArray());


        System.out.println(".......");
        System.out.println(rootCACert);
        System.out.println(".......");
        System.out.println(middleCACert);
        System.out.println(".......");
        System.out.println(node0EncCert);
        System.out.println(".......");
        System.out.println(node0SignCert);


    }

    @Test
    public void testPemLoad() throws IOException, CertificateException {

        GmSSLProvider.enableGMSupport(GmSSLProvider.GMTLS);

        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        Certificate cert = cf.generateCertificate(new FileInputStream("root.ca.pem"));
        System.out.println(cert);

        cert = cf.generateCertificate(new FileInputStream("middle.ca.pem"));
        System.out.println(cert);

    }


    @Test
    public void test() throws Exception {

        GmSSLProvider.enableGMSupport(GmSSLProvider.GMTLS);

        String trustStoreFile = "client.jks";
        KeyStore trustStore = KeyStore.getInstance("jks");
        trustStore.load(null, null);


        String pfx = "D:\\block160\\cert\\sm2.node0\\sm2.node0.enc.pfx";
        KeyStore keyStore = KeyStore.getInstance("pkcs12");
        keyStore.load(new FileInputStream(pfx), "12345678".toCharArray());

        String sig = "D:\\block160\\cert\\sm2.node0\\sm2.node0.sig.pfx";
        KeyStore keyStoresig = KeyStore.getInstance("pkcs12");
        keyStoresig.load(new FileInputStream(sig), "12345678".toCharArray());


        String aEnc = "enc.pfx";
        KeyStore aEncKS = KeyStore.getInstance("pkcs12");
        aEncKS.load(new FileInputStream(aEnc), "12345678".toCharArray());

        String aSign = "sign.pfx";
        KeyStore aSignKS = KeyStore.getInstance("pkcs12");
        aSignKS.load(new FileInputStream(aSign), "12345678".toCharArray());

        String both = "D:\\block160\\cert\\sm2.node0\\sm2.node0.both.pfx";
        KeyStore keyStoreboth = KeyStore.getInstance("pkcs12");
        keyStoreboth.load(new FileInputStream(both), "12345678".toCharArray());

        String botha = "both.pfx";
        KeyStore bothStore = KeyStore.getInstance("pkcs12");
        bothStore.load(new FileInputStream(botha), "12345678".toCharArray());

        System.out.println("-------------");

//        System.out.println(keyStoreboth.getCertificate("rca"));
//        System.out.println(keyStoreboth.getCertificate("oca"));
//        System.out.println(keyStoreboth.getCertificate("enc"));
//        System.out.println(keyStoreboth.getCertificate("sig"));


        // keyboth:  Sign key, Enc key
        //           RCA certs  OCA certs sig certs  enc certs

//        Signature signature = Signature.getInstance("SM3WithSM2");
//
//        PemReader pemReader = new PemReader(Files.newBufferedReader(new File("D:\\block160\\cert\\sm2.node0\\sm2.oca.pem").toPath()));
//        PemObject pemObject = pemReader.readPemObject();
//
//        X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(pemObject.getContent());
//        KeyFactory keyFactory = KeyFactory.getInstance("EC");
//
//        PublicKey publicKey = keyFactory.generatePublic(pubKeySpec);

        keyStore = bothStore;

        Enumeration<String> aliases = keyStore.aliases();

        List<Certificate> addedList = new ArrayList<>();


        while (aliases.hasMoreElements()) {

            String alias = aliases.nextElement();

            if (keyStore.entryInstanceOf(alias, KeyStore.PrivateKeyEntry.class)) {
                KeyStore.Entry storeEntry = keyStore.getEntry(alias, new KeyStore.PasswordProtection("12345678".toCharArray()));
                KeyStore.PrivateKeyEntry pe = (KeyStore.PrivateKeyEntry) storeEntry;

                for (Certificate certificate : pe.getCertificateChain()) {
                    if (!addedList.contains(certificate)) {
                        trustStore.setCertificateEntry(UUID.randomUUID().toString(), certificate);
                        addedList.add(certificate);
                    }
                }
            }

            if (keyStore.entryInstanceOf(alias, KeyStore.TrustedCertificateEntry.class)) {
                KeyStore.Entry storeEntry = keyStore.getEntry(alias, null);
                KeyStore.TrustedCertificateEntry pe = (KeyStore.TrustedCertificateEntry) storeEntry;

                if (!addedList.contains(pe.getTrustedCertificate())) {
                    trustStore.setCertificateEntry(alias, pe.getTrustedCertificate());
                    addedList.add(pe.getTrustedCertificate());
                }
            }

            if (keyStore.entryInstanceOf(alias, KeyStore.SecretKeyEntry.class)) {
                KeyStore.Entry storeEntry = keyStore.getEntry(alias, new KeyStore.PasswordProtection("12345678".toCharArray()));
                KeyStore.SecretKeyEntry pe = (KeyStore.SecretKeyEntry) storeEntry;
                System.out.println(pe);
            }


        }


        trustStore.store(new FileOutputStream(trustStoreFile), "12345678".toCharArray());


    }

}
