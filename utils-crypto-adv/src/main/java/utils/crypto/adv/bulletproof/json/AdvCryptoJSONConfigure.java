package utils.crypto.adv.bulletproof.json;

import org.bouncycastle.math.ec.ECPoint;
import utils.crypto.adv.bulletproof.algebra.GroupElement;
import utils.crypto.adv.bulletproof.linearalgebra.GeneratorVector;
import utils.crypto.adv.bulletproof.rangeproof.RangeProof;
import utils.serialize.json.JSONAutoConfigure;
import utils.serialize.json.JSONConfigurator;

public class AdvCryptoJSONConfigure implements JSONAutoConfigure {

    @Override
    public void configure(JSONConfigurator configurator) {
        // CryptoSetting
        configurator.configSuperSerializer(ECPoint.class, ECPointSerializer.INSTANCE);
        configurator.configDeserializer(GroupElement.class, GroupElementDeserializer.INSTANCE);
        configurator.configDeserializer(RangeProof.class, RangeProofDeserializer.INSTANCE);
        configurator.configDeserializer(GeneratorVector.class, GeneratorVectorDeserializer.INSTANCE);
    }

}
