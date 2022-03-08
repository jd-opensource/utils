package utils.net;

import utils.StringUtils;

import java.util.Arrays;

/**
 * @description: SSL连接配置
 * @author: imuge
 * @date: 2021/11/10
 **/
public class SSLSecurity {

    private static final String DEFAULT_KEY_STORE_TYPE = "PKCS12";
    private static final String DEFAULT_TRUST_STORE_TYPE = "JKS";
    private static final String DEFAULT_PROTOCOL = "TLS";
    public static final String DEFAULT_HOST_NAME_VERIFIER = "NO-OP";

    private String keyStoreType = DEFAULT_KEY_STORE_TYPE;
    private String keyStore;
    private String keyAlias;
    private String keyStorePassword;
    private String trustStore;
    private String trustStorePassword;
    private String trustStoreType = DEFAULT_TRUST_STORE_TYPE;
    private String protocol = DEFAULT_PROTOCOL;
    private String[] enabledProtocols;
    private String[] ciphers;
    private String hostNameVerifier = DEFAULT_HOST_NAME_VERIFIER;

    public SSLSecurity() {
    }

    public SSLSecurity(String keyStoreType, String keyStore, String keyAlias, String keyStorePassword,
                       String trustStore, String trustStorePassword, String trustStoreType,
                       String protocol, String enabledProtocols, String ciphers) {
        this.keyStoreType = StringUtils.isEmpty(keyStoreType) ? DEFAULT_KEY_STORE_TYPE : keyStoreType;
        this.keyStore = keyStore;
        this.keyAlias = keyAlias;
        this.keyStorePassword = keyStorePassword;
        this.trustStore = trustStore;
        this.trustStorePassword = trustStorePassword;
        this.trustStoreType = StringUtils.isEmpty(trustStoreType) ? DEFAULT_TRUST_STORE_TYPE : trustStoreType;
        this.protocol = StringUtils.isEmpty(protocol) ? DEFAULT_PROTOCOL : protocol;
        this.enabledProtocols = StringUtils.isEmpty(enabledProtocols) ? null : enabledProtocols.split(",");
        this.ciphers = StringUtils.isEmpty(ciphers) ? null : ciphers.split(",");
    }

    public SSLSecurity(String keyStoreType, String keyStore, String keyAlias, String keyStorePassword,
                       String trustStore, String trustStorePassword, String trustStoreType,
                       String protocol, String enabledProtocols, String ciphers, String hostNameVerifier) {
        this(keyStoreType, keyStore, keyAlias, keyStorePassword, trustStore, trustStorePassword, trustStoreType, protocol, enabledProtocols, ciphers);
        this.hostNameVerifier = hostNameVerifier == null || "".equals(hostNameVerifier) ? DEFAULT_HOST_NAME_VERIFIER : hostNameVerifier;
    }


    public String getTrustStoreType() {
        return trustStoreType;
    }

    public void setTrustStoreType(String trustStoreType) {
        this.trustStoreType = trustStoreType;
    }

    public String getKeyStoreType() {
        return keyStoreType;
    }

    public void setKeyStoreType(String keyStoreType) {
        this.keyStoreType = keyStoreType;
    }

    public String getKeyStore() {
        return keyStore;
    }

    public void setKeyStore(String keyStore) {
        this.keyStore = keyStore;
    }

    public String getKeyAlias() {
        return keyAlias;
    }

    public void setKeyAlias(String keyAlias) {
        this.keyAlias = keyAlias;
    }

    public String getKeyStorePassword() {
        return keyStorePassword;
    }

    public void setKeyStorePassword(String keyStorePassword) {
        this.keyStorePassword = keyStorePassword;
    }

    public String getTrustStore() {
        return trustStore;
    }

    public void setTrustStore(String trustStore) {
        this.trustStore = trustStore;
    }

    public String getTrustStorePassword() {
        return trustStorePassword;
    }

    public void setTrustStorePassword(String trustStorePassword) {
        this.trustStorePassword = trustStorePassword;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String[] getEnabledProtocols() {
        return enabledProtocols;
    }

    public void setEnabledProtocols(String[] enabledProtocols) {
        this.enabledProtocols = enabledProtocols;
    }

    public String[] getCiphers() {
        return ciphers;
    }

    public void setCiphers(String[] ciphers) {
        this.ciphers = ciphers;
    }

    /**
     * 认证模式，分客户端和服务端
     *
     * @param isClient
     * @return
     */
    public SSLMode getSslMode(boolean isClient) {
        if (!StringUtils.isEmpty(keyStore) && !StringUtils.isEmpty(trustStore)) {
            return SSLMode.TWO_WAY;
        }
        if (isClient && !StringUtils.isEmpty(trustStore)) {
            return SSLMode.ONE_WAY;
        }
        if (!isClient && !StringUtils.isEmpty(keyStore)) {
            return SSLMode.ONE_WAY;
        }
        return SSLMode.OFF;
    }


    public boolean isNoopHostnameVerifier() {
        return DEFAULT_HOST_NAME_VERIFIER.equals(this.hostNameVerifier);
    }

    public String getHostNameVerifier() {
        return hostNameVerifier;
    }

    public void setHostNameVerifier(String hostNameVerifier) {
        this.hostNameVerifier = hostNameVerifier;
    }

    @Override
    public String toString() {
        return "SSLSecurity{" +
                "keyStoreType='" + keyStoreType + '\'' +
                ", keyStore='" + keyStore + '\'' +
                ", keyAlias='" + keyAlias + '\'' +
                ", keyStorePassword='" + keyStorePassword + '\'' +
                ", trustStore='" + trustStore + '\'' +
                ", trustStorePassword='" + trustStorePassword + '\'' +
                ", trustStoreType='" + trustStoreType + '\'' +
                ", protocol='" + protocol + '\'' +
                ", enabledProtocols=" + Arrays.toString(enabledProtocols) +
                ", ciphers=" + Arrays.toString(ciphers) +
                ", hostNameVerifier='" + hostNameVerifier + '\'' +
                '}';
    }
}
