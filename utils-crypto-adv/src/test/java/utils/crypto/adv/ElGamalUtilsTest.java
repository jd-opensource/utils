package utils.crypto.adv;

import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.params.ElGamalPrivateKeyParameters;
import org.bouncycastle.crypto.params.ElGamalPublicKeyParameters;
import org.bouncycastle.util.encoders.Hex;
import org.junit.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class ElGamalUtilsTest {

    @Test
    public void testGenerateKeyPair() {

        AsymmetricCipherKeyPair keyPair = ElGamalUtils.generateKeyPair();

        ElGamalPublicKeyParameters pubKeyParams = (ElGamalPublicKeyParameters) keyPair.getPublic();
        ElGamalPrivateKeyParameters privKeyParams = (ElGamalPrivateKeyParameters) keyPair.getPrivate();

        byte[] privKeyBytes = ElGamalUtils.privKey2Bytes_RawKey(privKeyParams);
        byte[] pubKeyBytes = ElGamalUtils.pubKey2Bytes_RawKey(pubKeyParams);

        byte[] retrievedPubKeyBytes = ElGamalUtils.retrievePublicKey(privKeyBytes);

        assertEquals(64, privKeyBytes.length);
        assertEquals(64, pubKeyBytes.length);

        assertArrayEquals(retrievedPubKeyBytes, pubKeyBytes);
    }


    @Test
    public void testDecrypt() {

        AsymmetricCipherKeyPair keyPair = ElGamalUtils.generateKeyPair();

        ElGamalPublicKeyParameters pubKeyParams = (ElGamalPublicKeyParameters) keyPair.getPublic();
        ElGamalPrivateKeyParameters privKeyParams = (ElGamalPrivateKeyParameters) keyPair.getPrivate();

        byte[] privKeyBytes = ElGamalUtils.privKey2Bytes_RawKey(privKeyParams);
        byte[] pubKeyBytes = ElGamalUtils.pubKey2Bytes_RawKey(pubKeyParams);

        byte[] message = "hello".getBytes(StandardCharsets.UTF_8);

        byte[] ciphertext = ElGamalUtils.encrypt(message, pubKeyBytes);
        byte[] plaintext = ElGamalUtils.decrypt(ciphertext, privKeyBytes);

        assertEquals(128, ciphertext.length);
        assertArrayEquals(plaintext, message);
    }
}