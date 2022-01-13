package utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Provider;
import java.security.Security;

/*
 *
 * GM SSL 支持以下工具包:
 *  a. gmssl.cn开源版。 商业版参考: https://www.gmssl.cn
 *  b. https://gitee.com/openeuler/bgmprovider  该工具包仅在openjdk下使用
 *
 *  使用gmssl.cn开源版(lib/gmssl_provider-gmsslcn.jar)时，协议配置为 GMSSLv1.1
 *  使用bgmprovider(lib/gmssl_provider-bgmprovider.jar.jar)时，协议配置为 GMTLS
 *  使用bgmprovider-gmsslv1.1(lib/gmssl_provider-bgmprovider-gmsslv1.1.jar.jar)时, 协议配置为 GMSSLv1.1
 */

public class GmSSLProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(GmSSLProvider.class);

    public static final String ECC_SM4_CBC_SM_3 = "ECC_SM4_CBC_SM3";
    public static final String[] ENABLE_CIPHERS = new String[]{ECC_SM4_CBC_SM_3};

    public static final String[] JCE_PROVIDERS = new String[]{"cn.gmssl.jce.provider.GMJCE", "org.bouncycastle.jce.provider.BouncyCastleProvider"};
    public static final String[] JSSE_PROVIDERS = new String[]{"cn.gmssl.jsse.provider.GMJSSE", "org.openeuler.BGMProvider"};
    public static final String[] GM_PROTOCOLS = new String[]{"GMSSLv1.1", "GMTLS"};

    protected static final Provider JSSE_PROVIDER;
    protected static final Provider JCE_PROVIDER;

    public static final String GM_PROVIDER;
    public static final String GMTLS;
    public static final String[] ENABLE_PROTOCOLS;

    static {
        try {
            Class jsseProviderClass = findJSSEProvider();
            Class jceProviderClass = findJCEProvider();

            LOGGER.info("find jsse provider: {}", jsseProviderClass);
            LOGGER.info("find jce provider: {}", jceProviderClass);

            JCE_PROVIDER = jceProviderClass != null ? (Provider) jceProviderClass.newInstance() : null;
            JSSE_PROVIDER = jsseProviderClass != null ? (Provider) jsseProviderClass.newInstance() : null;
            GM_PROVIDER = JSSE_PROVIDER != null ? JSSE_PROVIDER.getName() : null;
            GMTLS = findGMProtocol(JSSE_PROVIDER);
            LOGGER.info("find tls protocol: {}", GMTLS);
            ENABLE_PROTOCOLS = new String[]{GMTLS};

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static void enableGMSupport(String protocol) {
        if (isGMSSL(protocol)) {
            LOGGER.info("enable gm protocol: {}", protocol);
            loadGMProvider();
        }
    }

    private static void loadGMProvider() {
        try {
            Security.insertProviderAt(JSSE_PROVIDER, 1);
            Security.insertProviderAt(JCE_PROVIDER, 2);
        } catch (Exception e) {
            LOGGER.error("enable sm tls error", e);
        }
    }


    public static boolean isGMSSL(String protocol) {
        return GMTLS != null && GMTLS.equals(protocol);
    }

    public static boolean supportGMSSL(String sslEnabled, String protocol) {
        return Boolean.parseBoolean(sslEnabled) && isGMSSL(protocol);
    }


    private static Class findJSSEProvider() {

        for (String provider : JSSE_PROVIDERS) {
            try {
                return Class.forName(provider);
            } catch (ClassNotFoundException e) {
                LOGGER.debug("not found provider: " + provider);
            }
        }

        return null;
    }

    private static Class findJCEProvider() {

        for (String provider : JCE_PROVIDERS) {
            try {
                return Class.forName(provider);
            } catch (ClassNotFoundException e) {
                LOGGER.debug("not found provider: " + provider);
            }
        }

        return null;
    }


    private static String findGMProtocol(Provider jsseProvider) {

        if (jsseProvider == null) {
            return null;
        }

        for (String protocol : GM_PROTOCOLS) {
            if (jsseProvider.containsKey(String.format("SSLContext.%s", protocol))) {
                return protocol;
            }
        }

        return null;
    }

}
