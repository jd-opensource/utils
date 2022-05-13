package utils.crypto.adv.bulletproof.json;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.JSONToken;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import cyclops.collections.immutable.VectorX;
import utils.crypto.adv.bulletproof.algebra.BouncyCastleECPoint;
import utils.crypto.adv.bulletproof.algebra.GroupElement;
import utils.crypto.adv.bulletproof.algebra.Secp256k1;
import utils.crypto.adv.bulletproof.innerproduct.InnerProductProof;
import utils.crypto.adv.bulletproof.linearalgebra.GeneratorVector;
import utils.crypto.adv.bulletproof.rangeproof.RangeProof;
import utils.serialize.json.JSONSerializeUtils;

import java.lang.reflect.Type;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class RangeProofDeserializer implements ObjectDeserializer {

    public static final RangeProofDeserializer INSTANCE = new RangeProofDeserializer();

    @Override
    public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
        if (type instanceof Class && RangeProof.class.isAssignableFrom((Class<?>) type)) {
            JSONObject json = parser.parseObject();
            GroupElement aI = JSONSerializeUtils.deserializeFromJSON(json.getString("aI"), GroupElement.class);
            GroupElement s = JSONSerializeUtils.deserializeFromJSON(json.getString("s"), GroupElement.class);
            BigInteger tauX = new BigInteger(json.getString("tauX"));
            BigInteger mu = new BigInteger(json.getString("mu"));
            BigInteger t = new BigInteger(json.getString("t"));
            JSONObject productProof = json.getJSONObject("productProof");
            BigInteger productProofA = new BigInteger(productProof.getString("a"));
            BigInteger productProofB = new BigInteger(productProof.getString("b"));
            JSONArray productProofLArr = productProof.getJSONArray("l");
            List<GroupElement> productProofL = new ArrayList<>();
            for (int i = 0; i < productProofLArr.size(); i++) {
                productProofL.add(JSONSerializeUtils.deserializeFromJSON(productProofLArr.getString(i), GroupElement.class));
            }
            JSONArray productProofRArr = productProof.getJSONArray("r");
            List<GroupElement> productProofR = new ArrayList<>();
            for (int i = 0; i < productProofRArr.size(); i++) {
                productProofR.add(JSONSerializeUtils.deserializeFromJSON(productProofRArr.getString(i), GroupElement.class));
            }
            InnerProductProof proof = new InnerProductProof(productProofL, productProofR, productProofA, productProofB);

            GeneratorVector generatorVector = JSONSerializeUtils.deserializeFromJSON(json.getString("tCommits"), GeneratorVector.class);
//            JSONArray vector = json.getJSONObject("tCommits").getJSONArray("vector");
//            GroupElement t1 = JSONSerializeUtils.deserializeFromJSON(vector.getString(0), GroupElement.class);
//            GroupElement t2 = JSONSerializeUtils.deserializeFromJSON(vector.getString(1), GroupElement.class);
//            GeneratorVector generatorVector = new GeneratorVector(VectorX.of((BouncyCastleECPoint) t1, (BouncyCastleECPoint) t2), new Secp256k1());
            return (T) new RangeProof<BouncyCastleECPoint>((BouncyCastleECPoint) aI, (BouncyCastleECPoint) s, generatorVector, tauX, mu, t, proof);
        }
        return (T) parser.parse(fieldName);
    }

    @Override
    public int getFastMatchToken() {
        return JSONToken.LBRACE;
    }
}
