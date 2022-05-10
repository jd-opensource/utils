package utils.crypto.adv;

import com.codahale.shamir.Scheme;
import utils.codec.Base58Utils;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

public class ShamirUtils {

    public static String[] split(int n, int k, byte[] secret) {
        Scheme scheme = new Scheme(new SecureRandom(), n, k);
        Map<Integer, byte[]> partsMap = scheme.split(secret);
        String[] parts = new String[partsMap.size()];
        for (int i = 0; i < partsMap.size(); i++) {
            parts[i] = Base58Utils.encode(partsMap.get(i + 1));
        }

        return parts;
    }

    public static byte[] recover(String[] parts) {
        Map<Integer, byte[]> partsMap = new HashMap<>();
        for (int i = 0; i < parts.length; i++) {
            partsMap.put(i + 1, Base58Utils.decode(parts[i]));
        }

        return new Scheme(new SecureRandom(), 2, 2).join(partsMap);
    }

}
