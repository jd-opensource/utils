package utils.crypto.adv.bulletproof;

import java.math.BigInteger;

/**
 * Created by buenz on 7/7/17.
 */
public class FieldPolynomial {
    private final BigInteger[] coefficients;


    public FieldPolynomial(BigInteger... coefficients) {
        this.coefficients = coefficients;
    }


    public BigInteger[] getCoefficients() {
        return coefficients;
    }


}
