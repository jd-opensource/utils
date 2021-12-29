package utils;

import org.bouncycastle.math.ec.custom.gm.SM2P256V1Curve;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;

public class TestKeyStore {

    @Test
    public void test() throws Exception{

        GmSSLProvider.enableGMSupport(GmSSLProvider.GMTLS);

        String trustStoreFile = "all.truststore";
        KeyStore trustStore = KeyStore.getInstance("pkcs12");
        trustStore.load(null, null);


        String pfx = "D:\\block160\\cert\\sm2.127.0.0.1.both.pfx";
        KeyStore keyStore = KeyStore.getInstance("pkcs12");
        keyStore.load(new FileInputStream(pfx), "12345678".toCharArray());

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

    @Test
    public void testCert(){

        SM2P256V1Curve p256V1Curve = new SM2P256V1Curve();



    }



}
