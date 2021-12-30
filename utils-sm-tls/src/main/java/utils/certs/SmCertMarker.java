package utils.certs;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.x500.AttributeTypeAndValue;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class SmCertMarker {

    private enum CertType {
        ROOT_CA,
        MIDDLE_CA,
        ENTITY;
    }

    public static final String SIGN_ALGO = "SM3withSM2";
    public static final String PROVIDER_NAME = "BC";

    public static X509Certificate createRootCACert(KeyPair rootKeyPair,
                                                   X500Name rootSubjectDN,
                                                   long certExpire) throws Exception {
        KeyUsage usage = new KeyUsage(KeyUsage.keyCertSign | KeyUsage.cRLSign | KeyUsage.digitalSignature | KeyUsage.nonRepudiation);
        PKCS10CertificationRequest request = CertsHelper.createCSRRequest(rootSubjectDN, rootKeyPair.getPublic(), rootKeyPair.getPrivate());
        return makeCertificate(CertType.ROOT_CA, rootKeyPair, rootSubjectDN, request, certExpire, new BasicConstraints(true), usage, null);
    }

    public static X509Certificate createMiddleCACert(KeyPair issueKeyPair,
                                                     X500Name issueDN,
                                                     X500Name middleSubjectDN,
                                                     KeyPair middleCAKeyPair,
                                                     long certExpire) throws Exception {
        KeyUsage usage = new KeyUsage(KeyUsage.keyCertSign | KeyUsage.cRLSign | KeyUsage.digitalSignature | KeyUsage.nonRepudiation);
        PKCS10CertificationRequest request = CertsHelper.createCSRRequest(middleSubjectDN, middleCAKeyPair.getPublic(), middleCAKeyPair.getPrivate());
        return makeCertificate(CertType.MIDDLE_CA, issueKeyPair, issueDN, request, certExpire, new BasicConstraints(true), usage, null);
    }


    public static X509Certificate createEntityCert(KeyPair issueKeyPair,
                                                   X500Name issueDN,
                                                   X500Name entitySubjectDN,
                                                   KeyPair entityKeyPair,
                                                   long certExpire) throws Exception {
        KeyUsage usage = new KeyUsage(KeyUsage.digitalSignature | KeyUsage.keyAgreement | KeyUsage.dataEncipherment | KeyUsage.keyEncipherment);
        PKCS10CertificationRequest request = CertsHelper.createCSRRequest(entitySubjectDN, entityKeyPair.getPublic(), entityKeyPair.getPrivate());
        return makeCertificate(CertType.ENTITY, issueKeyPair, issueDN, request, certExpire, new BasicConstraints(false), usage, null);
    }


    public static X509Certificate createEntitySignCert(KeyPair issueKeyPair,
                                                       X500Name issueDN,
                                                       X500Name entitySubjectDN,
                                                       KeyPair entityKeyPair,
                                                       long certExpire) throws Exception {
        KeyUsage usage = new KeyUsage(KeyUsage.digitalSignature | KeyUsage.nonRepudiation);
        PKCS10CertificationRequest request = CertsHelper.createCSRRequest(entitySubjectDN, entityKeyPair.getPublic(), entityKeyPair.getPrivate());
        return makeCertificate(CertType.ENTITY, issueKeyPair, issueDN, request, certExpire, new BasicConstraints(false), usage, null);
    }

    public static X509Certificate createEntityEncCert(KeyPair issueKeyPair,
                                                      X500Name issueDN,
                                                      X500Name entitySubjectDN,
                                                      KeyPair entityKeyPair,
                                                      long certExpire) throws Exception {
        KeyUsage usage = new KeyUsage(KeyUsage.keyAgreement | KeyUsage.dataEncipherment | KeyUsage.keyEncipherment);
        PKCS10CertificationRequest request = CertsHelper.createCSRRequest(entitySubjectDN, entityKeyPair.getPublic(), entityKeyPair.getPrivate());
        return makeCertificate(CertType.ENTITY, issueKeyPair, issueDN, request, certExpire, new BasicConstraints(false), usage, null);
    }


    private static X509Certificate makeCertificate(CertType certType,
                                                   KeyPair issuerKeyPair,
                                                   X500Name issuerDN,
                                                   PKCS10CertificationRequest request,
                                                   long certExpire,
                                                   BasicConstraints basicConstraints,
                                                   KeyUsage keyUsage,
                                                   KeyPurposeId[] extendedKeyUsages)
            throws Exception {

        SubjectPublicKeyInfo subPub = request.getSubjectPublicKeyInfo();

        PrivateKey issPriv = issuerKeyPair.getPrivate();
        PublicKey issPub = issuerKeyPair.getPublic();

        X500Name subject = request.getSubject();
        String email = null;
        String commonName = null;

        RDN[] rdns = subject.getRDNs();
        List<RDN> newRdns = new ArrayList<>(rdns.length);
        for (int i = 0; i < rdns.length; i++) {
            RDN rdn = rdns[i];

            AttributeTypeAndValue atv = rdn.getFirst();
            ASN1ObjectIdentifier type = atv.getType();
            if (BCStyle.EmailAddress.equals(type)) {
                email = IETFUtils.valueToString(atv.getValue());
            } else {
                if (BCStyle.CN.equals(type)) {
                    commonName = IETFUtils.valueToString(atv.getValue());
                }
                newRdns.add(rdn);
            }
        }

        List<GeneralName> subjectAltNames = new LinkedList<>();
        if (email != null) {
            subject = new X500Name(newRdns.toArray(new RDN[0]));
            subjectAltNames.add(
                    new GeneralName(GeneralName.rfc822Name,
                            new DERIA5String(email, true)));
        }

        boolean selfSignedEECert = false;
        switch (certType) {
            case ROOT_CA:
                if (issuerDN.equals(subject)) {
                    subject = issuerDN;
                } else {
                    throw new IllegalArgumentException("subject != issuer for certLevel " + CertType.ROOT_CA);
                }
                break;
            case MIDDLE_CA:
                if (issuerDN.equals(subject)) {
                    throw new IllegalArgumentException(
                            "subject MUST not equals issuer for certLevel " + certType);
                }
                break;
            default:
                if (issuerDN.equals(subject)) {
                    selfSignedEECert = true;
                    subject = issuerDN;
                }
        }

        BigInteger serialNumber = new RandomSNAllocator().nextSerialNumber();
        Date notBefore = new Date();
        Date notAfter = new Date(notBefore.getTime() + certExpire);
        X509v3CertificateBuilder v3CertGen = new X509v3CertificateBuilder(
                issuerDN, serialNumber,
                notBefore, notAfter,
                subject, subPub);

        JcaX509ExtensionUtils extUtils = new JcaX509ExtensionUtils();
        v3CertGen.addExtension(Extension.subjectKeyIdentifier, false,
                extUtils.createSubjectKeyIdentifier(subPub));
        if (certType != CertType.ROOT_CA && !selfSignedEECert) {
            v3CertGen.addExtension(Extension.authorityKeyIdentifier, false,
                    extUtils.createAuthorityKeyIdentifier(SubjectPublicKeyInfo.getInstance(issPub.getEncoded())));
        }

        // RFC 5280 §4.2.1.9 Basic Constraints:
        // Conforming CAs MUST include this extension in all CA certificates
        // that contain public keys used to validate digital signatures on
        // certificates and MUST mark the extension as critical in such
        // certificates.
//        BasicConstraints basicConstraints;
//        if (certType == CertType.EndEntity) {
//            basicConstraints = new BasicConstraints(false);
//        } else {
//            basicConstraints = pathLenConstrain == null
//                    ? new BasicConstraints(true) : new BasicConstraints(pathLenConstrain.intValue());
//        }
        v3CertGen.addExtension(Extension.basicConstraints, true, basicConstraints);
        v3CertGen.addExtension(Extension.keyUsage, true, keyUsage);

        if (extendedKeyUsages != null) {
            ExtendedKeyUsage xku = new ExtendedKeyUsage(extendedKeyUsages);
            v3CertGen.addExtension(Extension.extendedKeyUsage, false, xku);

            boolean forSSLServer = false;
            for (KeyPurposeId purposeId : extendedKeyUsages) {
                if (KeyPurposeId.id_kp_serverAuth.equals(purposeId)) {
                    forSSLServer = true;
                    break;
                }
            }

            if (forSSLServer) {
                if (commonName == null) {
                    throw new IllegalArgumentException("commonName must not be null");
                }
                GeneralName name = new GeneralName(GeneralName.dNSName,
                        new DERIA5String(commonName, true));
                subjectAltNames.add(name);
            }
        }

        if (!subjectAltNames.isEmpty()) {
            v3CertGen.addExtension(Extension.subjectAlternativeName, false,
                    new GeneralNames(subjectAltNames.toArray(new GeneralName[0])));
        }

        JcaContentSignerBuilder contentSignerBuilder = new JcaContentSignerBuilder(SIGN_ALGO).setProvider(PROVIDER_NAME);
        X509Certificate cert = new JcaX509CertificateConverter().setProvider(PROVIDER_NAME)
                .getCertificate(v3CertGen.build(contentSignerBuilder.build(issPriv)));
        cert.verify(issPub);

        return cert;
    }

}