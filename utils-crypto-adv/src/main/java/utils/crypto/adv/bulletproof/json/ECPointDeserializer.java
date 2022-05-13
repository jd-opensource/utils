package utils.crypto.adv.bulletproof.json;

import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.JSONToken;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.encoders.Base64;
import utils.crypto.adv.bulletproof.util.ECConstants;

import java.lang.reflect.Type;

public class ECPointDeserializer implements ObjectDeserializer {

    public static final ECPointDeserializer INSTANCE = new ECPointDeserializer();

    @Override
    public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
        if (type instanceof Class && ECPoint.class.isAssignableFrom((Class<?>) type)) {

            String base64Str = parser.parseObject(String.class);
            return (T) ECConstants.BITCOIN_CURVE.decodePoint(Base64.decode(base64Str));
        }
        return (T) parser.parse(fieldName);
    }

    @Override
    public int getFastMatchToken() {
        return JSONToken.LBRACE;
    }
}
