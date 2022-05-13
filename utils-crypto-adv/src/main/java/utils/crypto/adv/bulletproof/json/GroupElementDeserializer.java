package utils.crypto.adv.bulletproof.json;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.JSONToken;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.encoders.Base64;
import utils.crypto.adv.bulletproof.algebra.BouncyCastleECPoint;
import utils.crypto.adv.bulletproof.algebra.GroupElement;
import utils.crypto.adv.bulletproof.util.ECConstants;

import java.lang.reflect.Type;

public class GroupElementDeserializer implements ObjectDeserializer {

    public static final GroupElementDeserializer INSTANCE = new GroupElementDeserializer();

    @Override
    public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
        if (type instanceof Class && GroupElement.class.isAssignableFrom((Class<?>) type)) {

            JSONObject object = parser.parseObject(JSONObject.class);
            ECPoint point = ECConstants.BITCOIN_CURVE.decodePoint(Base64.decode(object.getString("point")));
            return (T) new BouncyCastleECPoint(point);
        }
        return (T) parser.parse(fieldName);
    }

    @Override
    public int getFastMatchToken() {
        return JSONToken.LBRACE;
    }
}
