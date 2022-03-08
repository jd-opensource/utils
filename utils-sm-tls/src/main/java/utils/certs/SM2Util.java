package utils.certs;

import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.custom.gm.SM2P256V1Curve;

import java.math.BigInteger;
import java.security.*;

public class SM2Util {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static final SM2P256V1Curve CURVE = new SM2P256V1Curve();
    public final static BigInteger SM2_ECC_GX = new BigInteger("32C4AE2C1F1981195F9904466A39C9948FE30BBFF2660BE1715A4589334C74C7", 16);
    public final static BigInteger SM2_ECC_GY = new BigInteger("BC3736A2F4F6779C59BDCEE36B692153D0A9877CC62A474002DF32E52139F0A0", 16);
    public static final ECPoint G_POINT = CURVE.createPoint(SM2_ECC_GX, SM2_ECC_GY);
    public static final ECDomainParameters DOMAIN_PARAMS = new ECDomainParameters(CURVE, G_POINT, CURVE.getOrder(), CURVE.getCofactor());

    /**
     * 生成ECC密钥对
     */
    public static KeyPair generateKeyPair() throws NoSuchProviderException, NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        SecureRandom random = new SecureRandom();

        KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC", BouncyCastleProvider.PROVIDER_NAME);
        ECParameterSpec parameterSpec = new ECParameterSpec(DOMAIN_PARAMS.getCurve(), DOMAIN_PARAMS.getG(),
                DOMAIN_PARAMS.getN(), DOMAIN_PARAMS.getH());
        kpg.initialize(parameterSpec, random);
        return kpg.generateKeyPair();
    }

}
