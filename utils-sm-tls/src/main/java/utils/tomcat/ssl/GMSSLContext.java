package utils.tomcat.ssl;


import org.apache.tomcat.util.net.SSLContext;
import utils.GmSSLProvider;

import javax.net.ssl.*;
import java.security.KeyManagementException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

class GMSSLContext implements SSLContext {

    private javax.net.ssl.SSLContext context;
    private KeyManager[] kms;
    private TrustManager[] tms;

    GMSSLContext(String protocol) throws Exception {
        context = javax.net.ssl.SSLContext.getInstance(protocol, GmSSLProvider.GM_PROVIDER);
    }

    @Override
    public void init(KeyManager[] kms, TrustManager[] tms, SecureRandom sr)
            throws KeyManagementException {
        this.kms = kms;
        this.tms = tms;

        context.init(kms, tms, sr);

        context.getServerSessionContext().setSessionCacheSize(8192);
        context.getServerSessionContext().setSessionTimeout(0);
    }

    @Override
    public void destroy() {
    }

    @Override
    public SSLSessionContext getServerSessionContext() {
        return context.getServerSessionContext();
    }

    @Override
    public SSLEngine createSSLEngine() {
        SSLEngine engine = context.createSSLEngine();

        engine.setEnabledCipherSuites("ECC_SM4_GCM_SM3".split(","));
        engine.setEnabledProtocols("GMSSLv1.1".split(","));

        return engine;
    }

    @Override
    public SSLServerSocketFactory getServerSocketFactory() {
        return context.getServerSocketFactory();
    }

    @Override
    public SSLParameters getSupportedSSLParameters() {
        return context.getSupportedSSLParameters();
    }

    @Override
    public X509Certificate[] getCertificateChain(String alias) {
        X509Certificate[] result = null;
        if (kms != null) {
            for (int i = 0; i < kms.length && result == null; i++) {
                if (kms[i] instanceof X509KeyManager) {
                    result = ((X509KeyManager) kms[i]).getCertificateChain(alias);
                }
            }
        }
        return result;
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        Set<X509Certificate> certs = new HashSet<>();
        if (tms != null) {
            for (TrustManager tm : tms) {
                if (tm instanceof X509TrustManager) {
                    X509Certificate[] accepted = ((X509TrustManager) tm).getAcceptedIssuers();
                    if (accepted != null) {
                        certs.addAll(Arrays.asList(accepted));
                    }
                }
            }
        }
        return certs.toArray(new X509Certificate[0]);
    }
}
