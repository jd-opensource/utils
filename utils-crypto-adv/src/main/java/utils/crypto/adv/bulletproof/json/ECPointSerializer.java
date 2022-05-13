package utils.crypto.adv.bulletproof.json;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.encoders.Base64;

import java.io.IOException;
import java.lang.reflect.Type;

public class ECPointSerializer implements ObjectSerializer {

    public static final ECPointSerializer INSTANCE = new ECPointSerializer();

    @Override
    public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) throws IOException {
        SerializeWriter out = serializer.out;
        if (object == null) {
            out.writeNull();
            return;
        }

        out.writeString(Base64.toBase64String(((ECPoint) object).getEncoded(true)));
    }
}
