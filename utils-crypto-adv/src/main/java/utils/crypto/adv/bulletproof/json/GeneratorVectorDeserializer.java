package utils.crypto.adv.bulletproof.json;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.JSONToken;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import cyclops.collections.immutable.VectorX;
import utils.crypto.adv.bulletproof.algebra.BouncyCastleECPoint;
import utils.crypto.adv.bulletproof.algebra.GroupElement;
import utils.crypto.adv.bulletproof.algebra.Secp256k1;
import utils.crypto.adv.bulletproof.linearalgebra.GeneratorVector;
import utils.serialize.json.JSONSerializeUtils;

import java.lang.reflect.Type;

public class GeneratorVectorDeserializer implements ObjectDeserializer {

    public static final GeneratorVectorDeserializer INSTANCE = new GeneratorVectorDeserializer();

    @Override
    public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
        if (type instanceof Class && GeneratorVector.class.isAssignableFrom((Class<?>) type)) {

            JSONArray vector = parser.parseObject().getJSONArray("vector");
            BouncyCastleECPoint[] ts = new BouncyCastleECPoint[vector.size()];
            for (int i = 0; i < vector.size(); i++) {
                ts[i] = (BouncyCastleECPoint) JSONSerializeUtils.deserializeFromJSON(vector.getString(i), GroupElement.class);
            }
            return (T) new GeneratorVector(VectorX.of(ts), new Secp256k1());
        }
        return (T) parser.parse(fieldName);
    }

    @Override
    public int getFastMatchToken() {
        return JSONToken.LBRACE;
    }
}
