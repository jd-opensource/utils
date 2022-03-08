package utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Provider;
import java.security.Security;

public class GmSSLProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(GmSSLProvider.class);

    public static final String GMTLS = "GMSSLv1.1";
    public static final String GM_PROVIDER = "GMJSSE";

    public static final String[] ENABLE_PROTOCOLS = new String[]{"GMSSLv1.1"};
    public static final String[] ENABLE_CIPHERS = new String[]{"ECC_SM4_CBC_SM3"};

    private static void loadGMProvider() {
        try {
            Security.insertProviderAt((Provider) Class.forName("cn.gmssl.jce.provider.GMJCE").newInstance(), 1);
            Security.insertProviderAt((Provider) Class.forName("cn.gmssl.jsse.provider.GMJSSE").newInstance(), 2);
        } catch (Exception e) {
            LOGGER.error("enable sm tls error", e);
        }
    }

    public static void enableGMSupport(String protocol) {
        if (isGMSSL(protocol)) {
            LOGGER.info("enable gm protocol: {}", protocol);
            loadGMProvider();
        }
    }


    public static boolean isGMSSL(String protocol) {
        return GMTLS.equals(protocol);
    }

    public static boolean supportGMSSL(String sslEnabled, String protocol) {
        return Boolean.parseBoolean(sslEnabled) && GMTLS.equals(protocol);
    }


}
