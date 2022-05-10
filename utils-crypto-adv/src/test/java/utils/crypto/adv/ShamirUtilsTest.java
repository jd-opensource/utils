package utils.crypto.adv;

import org.junit.Assert;
import org.junit.Test;

import java.nio.charset.StandardCharsets;

public class ShamirUtilsTest {

    @Test
    public void test() {
        String secret = "JD Chain";
        String[] split = ShamirUtils.split(5, 3, secret.getBytes(StandardCharsets.UTF_8));
        byte[] recover = ShamirUtils.recover(split);
        Assert.assertEquals(secret, new String(recover, StandardCharsets.UTF_8));


    }
}
