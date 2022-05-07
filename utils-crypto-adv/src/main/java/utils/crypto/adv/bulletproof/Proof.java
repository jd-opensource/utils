package utils.crypto.adv.bulletproof;

import java.io.Serializable;

/**
 * Created by buenz on 7/10/17.
 */
public interface Proof extends Serializable {
    byte[] serialize();

}
