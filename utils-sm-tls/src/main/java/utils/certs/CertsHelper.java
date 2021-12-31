package utils.certs;

import com.google.common.base.Strings;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.jce.X509Principal;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequestBuilder;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Map;
import java.util.Vector;
import java.util.function.BiConsumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public final class CertsHelper {

    private CertsHelper() {
    }

    public static X500Name buildCertDN(String country,
                                       String organization,
                                       String organizationalUnit,
                                       String commonName,
                                       String province,
                                       String locality,
                                       String email) {
        X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
        addRDN(builder, BCStyle.C, country);
        addRDN(builder, BCStyle.O, organization);
        addRDN(builder, BCStyle.OU, organizationalUnit);
        addRDN(builder, BCStyle.CN, commonName);
        addRDN(builder, BCStyle.ST, province);
        addRDN(builder, BCStyle.L, locality);
        addRDN(builder, BCStyle.EmailAddress, email);
        return builder.build();
    }


    public static X500Name buildCertDN(String country,
                                       String organization,
                                       String organizationalUnit,
                                       String commonName) {
        return buildCertDN(country, organization, organizationalUnit, commonName, null, null, null);
    }


    public static PKCS10CertificationRequest createCSRRequest(X500Name subject, PublicKey pubKey, PrivateKey priKey) throws OperatorCreationException {
        SMPublicKey sm2SubPub = new SMPublicKey(pubKey.getAlgorithm(), (BCECPublicKey) pubKey);
        PKCS10CertificationRequestBuilder csrBuilder = new JcaPKCS10CertificationRequestBuilder(subject, sm2SubPub);
        ContentSigner signerBuilder = new JcaContentSignerBuilder("SM3withSM2")
                .setProvider(BouncyCastleProvider.PROVIDER_NAME).build(priKey);
        return csrBuilder.build(signerBuilder);
    }


    public static void makeSMCaTestCerts(File smHomeDir,
                                         int nodes,
                                         int gws,
                                         int users,
                                         long expire,
                                         String password,
                                         Map<ASN1ObjectIdentifier, String> subjectDNMap) throws Exception {

        KeyPair rootKeyPair = SM2Util.generateKeyPair();
        X500Name rootCaDN = CertsHelper.buildCertDN(
                subjectDNMap.get(BCStyle.C),
                subjectDNMap.get(BCStyle.O),
                "root-ca",
                "Root CA for test",
                subjectDNMap.get(BCStyle.ST),
                subjectDNMap.get(BCStyle.L),
                null
        );

        KeyPair middleKeyPair = SM2Util.generateKeyPair();
        X500Name middleCaDN = CertsHelper.buildCertDN(
                subjectDNMap.get(BCStyle.C),
                subjectDNMap.get(BCStyle.O),
                "middle-ca",
                "Middle CA for test",
                subjectDNMap.get(BCStyle.ST),
                subjectDNMap.get(BCStyle.L),
                null
        );

        X509Certificate rootCACert = SmCertMarker.createRootCACert(rootKeyPair, rootCaDN, expire);
        X509Certificate middleCACert = SmCertMarker.createMiddleCACert(rootKeyPair, rootCaDN, middleCaDN, middleKeyPair, expire);

        File rootCaPerm = new File(smHomeDir, "sm2.rca.pem");
        Files.write(rootCaPerm.toPath(), certToPem(rootCACert), StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        File middleCaPerm = new File(smHomeDir, "sm2.oca.pem");
        Files.write(middleCaPerm.toPath(), certToPem(middleCACert), StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        KeyStore trustKeyStore = KeyStore.getInstance("JKS");
        trustKeyStore.load(null, password.toCharArray());
        trustKeyStore.setCertificateEntry("rca", rootCACert);
        trustKeyStore.setCertificateEntry("oca", middleCACert);

        BiConsumer<String, X509Certificate> certificateConsumer = (alias, cert) -> {
            try {
                trustKeyStore.setCertificateEntry(alias, cert);
            } catch (KeyStoreException e) {
                //ignore
            }
        };

        for (int i = 0; i < nodes; i++) {
            CertsHelper.makeSMCerts(rootCACert, middleCACert, middleKeyPair, "node" + i, "node", password, smHomeDir, certificateConsumer);
        }

        for (int i = 0; i < gws; i++) {
            CertsHelper.makeSMCerts(rootCACert, middleCACert, middleKeyPair, "gw" + i, "gw", password, smHomeDir, certificateConsumer);
        }

        for (int i = 0; i < users; i++) {
            CertsHelper.makeSMCerts(rootCACert, middleCACert, middleKeyPair, "user" + i, "user", password, smHomeDir, certificateConsumer);
        }

        trustKeyStore.store(new FileOutputStream(new File(smHomeDir, "client.jks")), password.toCharArray());

    }

    public static void makeSMCerts(X509Certificate rootCa,
                                   X509Certificate middleCa,
                                   KeyPair middleCaKeyPair,
                                   String commonName,
                                   String ouName,
                                   String password,
                                   File outputPathDir,
                                   BiConsumer<String, X509Certificate> certificateConsumer
    ) throws Exception {

        KeyPair sigKeyPair = SM2Util.generateKeyPair();
        KeyPair encKeyPair = SM2Util.generateKeyPair();

        X509Principal middleCaX509Principal = (X509Principal) middleCa.getSubjectDN();
        String country = getValue(middleCaX509Principal, BCStyle.C);
        String org = getValue(middleCaX509Principal, BCStyle.O);
        String unit = getValue(middleCaX509Principal, BCStyle.OU);
        String cn = getValue(middleCaX509Principal, BCStyle.CN);
        String province = getValue(middleCaX509Principal, BCStyle.ST);
        String locality = getValue(middleCaX509Principal, BCStyle.L);
        String email = getValue(middleCaX509Principal, BCStyle.EmailAddress);

        X500Name middleCaDN = CertsHelper.buildCertDN(country, org, unit, cn, province, locality, email);
        X500Name signDN = CertsHelper.buildCertDN(country, org, ouName, commonName, province, locality, email);
        X500Name encDN = CertsHelper.buildCertDN(country, org, ouName, commonName, province, locality, email);

        Date expireDate = rootCa.getNotAfter();
        long expire = expireDate.getTime() - System.currentTimeMillis();

        X509Certificate signCert = SmCertMarker.createEntitySignCert(middleCaKeyPair, middleCaDN, signDN, sigKeyPair, expire);
        X509Certificate encCert = SmCertMarker.createEntityEncCert(middleCaKeyPair, middleCaDN, encDN, encKeyPair, expire);

        File zipFile = new File(outputPathDir, "sm2." + commonName + ".zip");

        KeyStore signKeyStore = KeyStore.getInstance("pkcs12");
        signKeyStore.load(null, null);
        signKeyStore.setKeyEntry("sign", sigKeyPair.getPrivate(), password.toCharArray(), new Certificate[]{middleCa});


        KeyStore encKeyStore = KeyStore.getInstance("pkcs12");
        encKeyStore.load(null, null);
        encKeyStore.setKeyEntry("enc", encKeyPair.getPrivate(), password.toCharArray(), new Certificate[]{middleCa});

        KeyStore bothStore = KeyStore.getInstance("pkcs12");
        bothStore.load(null, null);
        bothStore.setCertificateEntry("RCA", rootCa);
        bothStore.setCertificateEntry("OCA", middleCa);
        bothStore.setCertificateEntry("enc", encCert);
        bothStore.setCertificateEntry("sig", signCert);
        bothStore.setKeyEntry("enc", encKeyPair.getPrivate(), password.toCharArray(), new Certificate[]{encCert, middleCa, rootCa});
        bothStore.setKeyEntry("sig", sigKeyPair.getPrivate(), password.toCharArray(), new Certificate[]{signCert, middleCa, rootCa});


        try (ZipOutputStream zipOutputStream = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFile)))) {

            zipOutputStream.putNextEntry(new ZipEntry("password.txt"));
            zipOutputStream.write(password.getBytes());

            zipOutputStream.putNextEntry(new ZipEntry("sm2.rca.pem"));
            zipOutputStream.write(certToPem(rootCa));

            zipOutputStream.putNextEntry(new ZipEntry("sm2.oca.pem"));
            zipOutputStream.write(certToPem(middleCa));

            //sign
            zipOutputStream.putNextEntry(new ZipEntry(String.format("sm2.%s.sign.key.pem", commonName)));
            zipOutputStream.write(keyToPem(sigKeyPair.getPrivate()));

            zipOutputStream.putNextEntry(new ZipEntry(String.format("sm2.%s.sign.cert.pem", commonName)));
            zipOutputStream.write(certToPem(signCert));

            zipOutputStream.putNextEntry(new ZipEntry(String.format("sm2.%s.sign.cert.cer", commonName)));
            zipOutputStream.write(signCert.getEncoded());

            zipOutputStream.putNextEntry(new ZipEntry(String.format("sm2.%s.sign.key", commonName)));
            zipOutputStream.write(sigKeyPair.getPrivate().getEncoded());

            zipOutputStream.putNextEntry(new ZipEntry(String.format("sm2.%s.sign.pfx", commonName)));
            signKeyStore.store(zipOutputStream, password.toCharArray());

            //enc
            zipOutputStream.putNextEntry(new ZipEntry(String.format("sm2.%s.enc.key.pem", commonName)));
            zipOutputStream.write(keyToPem(encKeyPair.getPrivate()));

            zipOutputStream.putNextEntry(new ZipEntry(String.format("sm2.%s.enc.cert.pem", commonName)));
            zipOutputStream.write(certToPem(encCert));

            zipOutputStream.putNextEntry(new ZipEntry(String.format("sm2.%s.enc.cert.cer", commonName)));
            zipOutputStream.write(encCert.getEncoded());

            zipOutputStream.putNextEntry(new ZipEntry(String.format("sm2.%s.enc.key", commonName)));
            zipOutputStream.write(encKeyPair.getPrivate().getEncoded());

            zipOutputStream.putNextEntry(new ZipEntry(String.format("sm2.%s.enc.pfx", commonName)));
            encKeyStore.store(zipOutputStream, password.toCharArray());

            //both
            zipOutputStream.putNextEntry(new ZipEntry(String.format("sm2.%s.both.pfx", commonName)));
            bothStore.store(zipOutputStream, password.toCharArray());
        }

        if (certificateConsumer != null) {
            certificateConsumer.accept(commonName + "-enc", encCert);
            certificateConsumer.accept(commonName + "-sig", signCert);
        }

    }

    private static byte[] certToPem(X509Certificate x509Certificate) throws IOException {
        StringWriter sw = new StringWriter();
        JcaPEMWriter pemWriter = new JcaPEMWriter(sw);
        pemWriter.writeObject(x509Certificate);
        pemWriter.flush();
        return sw.toString().getBytes();
    }

    private static byte[] keyToPem(PrivateKey privateKey) throws IOException {
        StringWriter sw = new StringWriter();
        JcaPEMWriter pemWriter = new JcaPEMWriter(sw);
        pemWriter.writeObject(privateKey);
        pemWriter.flush();
        return sw.toString().getBytes();
    }

    private static String getValue(X509Principal principal, ASN1ObjectIdentifier identifier) {
        Vector vector = principal.getValues(identifier);
        if (vector == null || vector.isEmpty()) {
            return "";
        }
        return String.valueOf(vector.get(0));
    }

    private static void addRDN(X500NameBuilder builder, ASN1ObjectIdentifier identifier, String value) {
        if (!Strings.isNullOrEmpty(value)) {
            builder.addRDN(identifier, value);
        }
    }

}
