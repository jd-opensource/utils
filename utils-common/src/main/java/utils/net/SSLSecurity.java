package utils.net;

import utils.StringUtils;

/**
 * @description: SSL连接配置
 * @author: imuge
 * @date: 2021/11/10
 **/
public class SSLSecurity {
    // keystore 类型
    private String keyStoreType;
    // keystore 路径
    private String keyStore;
    // key别名
    private String keyAlias;
    // 密码
    private String keyStorePassword;
    // 信任库路径
    private String trustStore;
    // 信任库密码
    private String trustStorePassword;
    // 信任库类型
    private String trustStoreType;

    public SSLSecurity() {
    }

    public SSLSecurity(String keyStoreType, String keyStore, String keyAlias, String keyStorePassword, String trustStore, String trustStorePassword, String trustStoreType) {
        this.keyStoreType = keyStoreType;
        this.keyStore = keyStore;
        this.keyAlias = keyAlias;
        this.keyStorePassword = keyStorePassword;
        this.trustStore = trustStore;
        this.trustStorePassword = trustStorePassword;
        this.trustStoreType = trustStoreType;
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
        if (isClient && StringUtils.isEmpty(keyStore)) {
            return SSLMode.ONE_WAY;
        }
        if (!isClient && StringUtils.isEmpty(trustStore)) {
            return SSLMode.ONE_WAY;
        }
        return SSLMode.OFF;
    }

    @Override
    public String toString() {
        return "SSLSecurity{" +
                ", keyStoreType='" + keyStoreType + '\'' +
                ", keyStore='" + keyStore + '\'' +
                ", keyAlias='" + keyAlias + '\'' +
                ", keyStorePassword='" + keyStorePassword + '\'' +
                ", trustStore='" + trustStore + '\'' +
                ", trustStorePassword='" + trustStorePassword + '\'' +
                ", trustStoreType='" + trustStoreType + '\'' +
                '}';
    }
}
