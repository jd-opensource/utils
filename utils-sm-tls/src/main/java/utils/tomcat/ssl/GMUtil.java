package utils.tomcat.ssl;

import java.util.*;
import java.io.*;
import java.net.*;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509ExtendedTrustManager;
import javax.net.ssl.X509TrustManager;

import com.google.common.io.Resources;
import org.apache.juli.logging.Log;
import org.apache.tomcat.util.net.SSLContext;
import org.apache.tomcat.util.net.SSLHostConfigCertificate;
import org.apache.tomcat.util.net.SSLUtilBase;
import org.apache.tomcat.util.net.openssl.ciphers.Cipher;
import org.apache.tomcat.util.res.StringManager;
import utils.GmSSLProvider;


/**
 * SSLUtil implementation for JSSE.
 *
 * @author Harish Prabandham
 * @author Costin Manolache
 * @author Stefan Freyr Stefansson
 * @author EKR
 * @author Jan Luehe
 */
public class GMUtil extends SSLUtilBase {
    public static final boolean DEBUG = false;

    private static final StringManager sm = StringManager.getManager(GMUtil.class);

    private static final Set<String> implementedProtocols;
    private static final Set<String> implementedCiphers;

    private SSLHostConfigCertificate conf = null;

    static {
        if (GMUtil.DEBUG) {
            System.out.println("GMUtil ...");
        }

        SSLContext context = null;
        try {
            context = new GMSSLContext(GmSSLProvider.GMTLS);
            context.init(null, null, null);
        } catch (Exception e) {
            // This is fatal for the connector so throw an exception to prevent
            // it from starting
            e.printStackTrace();
            throw new IllegalArgumentException(e);
        }

        String[] implementedProtocolsArray = context.getSupportedSSLParameters().getProtocols();
        implementedProtocols = new HashSet<>(implementedProtocolsArray.length);

        // Filter out SSLv2 from the list of implemented protocols (just in case
        // we are running on a JVM that supports it) since it is no longer
        // considered secure but allow SSLv2Hello.
        // Note SSLv3 is allowed despite known insecurities because some users
        // still have a requirement for it.
        for (String protocol : implementedProtocolsArray) {
            String protocolUpper = protocol.toUpperCase(Locale.ENGLISH);
            if (!"SSLV2HELLO".equals(protocolUpper) && !"SSLV3".equals(protocolUpper)) {
                if (protocolUpper.contains("SSL")) {
                    continue;
                }
            }
            implementedProtocols.add(protocol);
        }

        String[] implementedCipherSuiteArray = context.getSupportedSSLParameters().getCipherSuites();
        // The IBM JRE will accept cipher suites names SSL_xxx or TLS_xxx but
        // only returns the SSL_xxx form for supported cipher suites. Therefore
        // need to filter the requested cipher suites using both forms with an
        // IBM JRE.
        {
            implementedCiphers = new HashSet<>(implementedCipherSuiteArray.length);
            implementedCiphers.addAll(Arrays.asList(implementedCipherSuiteArray));
        }
    }


    public GMUtil(SSLHostConfigCertificate certificate) {
        this(certificate, true);
    }


    public GMUtil(SSLHostConfigCertificate certificate, boolean warnOnSkip) {
        super(certificate, warnOnSkip);
        conf = certificate;
    }




    @Override
    public KeyManager[] getKeyManagers() throws Exception {
        String keystoreFile = null;
        String keystorePass = null;
        String keystoreType = null;
        keystoreFile = conf.getCertificateKeystoreFile();
        keystorePass = conf.getCertificateKeystorePassword();
        keystoreType = conf.getCertificateKeystoreType();
        String ciphers = conf.getSSLHostConfig().getCiphers();

        if (GMUtil.DEBUG) {
            System.out.println("getKeyManagers...");
            System.out.println("keystoreFile=" + keystoreFile);
            System.out.println("keystorePass=" + keystorePass);
            System.out.println("keystoreType=" + keystoreType);
            System.out.println("ciphers=" + ciphers);

            LinkedHashSet<Cipher> cs = conf.getSSLHostConfig().getCipherList();
            Iterator<Cipher> iterator = cs.iterator();
            while (iterator.hasNext()) {
                System.out.println("ciphersx=" + iterator.next());
            }
        }

        KeyManager[] keyManagers = null;
        try {
            KeyStore pfx = KeyStore.getInstance(keystoreType);

            InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(keystoreFile);
            if (inputStream == null) {
                inputStream = Resources.asByteSource(new URL(keystoreFile)).openStream();
            }


            if (GMUtil.DEBUG)
                System.out.println("xxx pfx inputStream2=" + inputStream.available());

            pfx.load(inputStream, keystorePass.toCharArray());

            if (GMUtil.DEBUG)
                System.out.println("xxx pfx size=" + pfx.size());

            if (pfx != null) {
                KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509", GmSSLProvider.GM_PROVIDER);
                keyManagerFactory.init(pfx, keystorePass.toCharArray());
                keyManagers = keyManagerFactory.getKeyManagers();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        KeyManager[] kms = keyManagers;//super.getKeyManagers();
        if (GMUtil.DEBUG)
            System.out.println("getKeyManagers kms=" + kms);

        return kms;
    }

    public TrustManager[] getTrustManagers() throws Exception {
        if (GMUtil.DEBUG)
            System.out.println("getTrustManagers...");

        TrustManager[] trustManagers = null;
        {
            trustManagers = new TrustManager[]{new GMTrustManager()};
        }

        TrustManager[] tms = trustManagers;//super.getTrustManagers();
        if (GMUtil.DEBUG)
            System.out.println("getTrustManagers tms=" + tms);

        return tms;
    }

    @Override
    protected Set<String> getImplementedProtocols() {
        if (GMUtil.DEBUG) {
            Iterator i = implementedProtocols.iterator();
            while (i.hasNext()) {
                System.out.println("implementedProtocol=" + i.next());
            }
        }
        return implementedProtocols;
    }


    @Override
    protected Set<String> getImplementedCiphers() {
        if (GMUtil.DEBUG) {
            Iterator i = implementedCiphers.iterator();
            while (i.hasNext()) {
                System.out.println("implementedCipher=" + i.next());
            }
        }
        return implementedCiphers;
    }

    @Override
    public String[] getEnabledProtocols() {
        String[] ss = super.getEnabledProtocols();
        if (GMUtil.DEBUG) {
            for (int i = 0; i < ss.length; i++) {
                System.out.println("getEnabledProtocolsx1 [" + i + "]=" + ss[i]);
            }
        }
        String[] ss2 = new String[ss.length + 2];
        for (int i = 0; i < ss.length; i++) {
            ss2[i] = ss[i];
        }
        ss2[ss.length] = GmSSLProvider.GMTLS;
        ss2[ss.length + 1] = "TLSv1.2";

        if (GMUtil.DEBUG) {
            for (int i = 0; i < ss2.length; i++) {
                System.out.println("getEnabledProtocolsx2 [" + i + "]=" + ss2[i]);
            }
        }

        return ss2;
    }

    @Override
    public String[] getEnabledCiphers() {
        String[] ss = super.getEnabledCiphers();
        if (GMUtil.DEBUG) {
            for (int i = 0; i < ss.length; i++) {
                System.out.println("getEnabledCiphersx1 [" + i + "]=" + ss[i]);
            }
        }

        // 2020.06.10，回自动协商到国密证书上去
        Vector<String> v = new Vector();
        for (int i = 0; i < ss.length; i++) {
            if (ss[i].indexOf("ECDSA") != -1) {
                continue;
            }
            if (ss[i].indexOf("_DSS_") != -1) {
                continue;
            }
            v.addElement(ss[i]);
        }

        LinkedHashSet<Cipher> cs = conf.getSSLHostConfig().getCipherList();
        if (GMUtil.DEBUG) {
            if (cs == null) {
                System.out.println("getCipherList is null");
            } else {
                Iterator<Cipher> iterator = cs.iterator();
                while (iterator.hasNext()) {
                    String s = iterator.next().toString();
                    System.out.println("getCipherList=" + s);
                }
            }
        }
        String cc = conf.getSSLHostConfig().getCiphers();
        if (GMUtil.DEBUG) {
            System.out.println("getCiphers=" + cc);
        }

        v.addElement("SSL_RSA_WITH_3DES_EDE_CBC_SHA");
        v.addElement("TLS_RSA_WITH_AES_128_CBC_SHA256");

        //2021.05.15
        v.addElement("ECC_SM4_GCM_SM3");
        v.addElement("ECC_SM4_CBC_SM3");
        v.addElement("ECDHE_SM4_GCM_SM3");
        v.addElement("ECDHE_SM4_CBC_SM3");

        //GCM不支持
        //v.addElement("TLS_RSA_WITH_AES_128_GCM_SHA256");

        ss = new String[v.size()];
        for (int i = 0; i < v.size(); i++) {
            ss[i] = v.elementAt(i);
        }

        if (GMUtil.DEBUG) {
            System.out.println("getCiphers=" + cc);
            for (int i = 0; i < ss.length; i++) {
                System.out.println("getEnabledCiphersx2 [" + i + "]=" + ss[i]);
            }
        }

        return ss;
    }

    @Override
    protected boolean isTls13RenegAuthAvailable() {
        // TLS 1.3 does not support authentication after the initial handshake
        return false;
    }


    @Override
    public SSLContext createSSLContextInternal(List<String> negotiableProtocols)
            throws Exception {
        return new GMSSLContext(sslHostConfig.getSslProtocol());
    }



    @Override
    protected Log getLog() {
        return new Log() {
            @Override
            public boolean isDebugEnabled() {
                return false;
            }

            @Override
            public boolean isErrorEnabled() {
                return false;
            }

            @Override
            public boolean isFatalEnabled() {
                return false;
            }

            @Override
            public boolean isInfoEnabled() {
                return false;
            }

            @Override
            public boolean isTraceEnabled() {
                return false;
            }

            @Override
            public boolean isWarnEnabled() {
                return false;
            }

            @Override
            public void trace(Object message) {

            }

            @Override
            public void trace(Object message, Throwable t) {

            }

            @Override
            public void debug(Object message) {

            }

            @Override
            public void debug(Object message, Throwable t) {

            }

            @Override
            public void info(Object message) {

            }

            @Override
            public void info(Object message, Throwable t) {

            }

            @Override
            public void warn(Object message) {

            }

            @Override
            public void warn(Object message, Throwable t) {

            }

            @Override
            public void error(Object message) {

            }

            @Override
            public void error(Object message, Throwable t) {

            }

            @Override
            public void fatal(Object message) {

            }

            @Override
            public void fatal(Object message, Throwable t) {

            }
        };
    }


}

class GMTrustManager extends X509ExtendedTrustManager {
    protected X509TrustManager tm = null;

    public GMTrustManager() {
    }

    public GMTrustManager(List<X509Certificate> trustCerts) {
    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType, Socket socket) throws CertificateException {
        checkClientTrusted(chain, authType);
    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType, SSLEngine engine)
            throws CertificateException {
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType, Socket socket) throws CertificateException {
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType, SSLEngine engine)
            throws CertificateException {
    }



}


